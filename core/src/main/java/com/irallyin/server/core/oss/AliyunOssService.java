package com.irallyin.server.core.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.irallyin.server.common.exception.BusinessException;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AliyunOssService {
    private static final DateTimeFormatter DATE_PATH_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif", "heic", "heif");
    private static final Set<String> VIDEO_EXTENSIONS = Set.of("mp4", "mov", "m4v", "webm", "avi", "mkv");

    private final AliyunOssProperties properties;
    private volatile OSS ossClient;

    public OssUploadResult uploadImage(InputStream inputStream,
                                       long contentLength,
                                       String originalFilename,
                                       String contentType) {
        return upload(inputStream, contentLength, originalFilename, contentType, OssMediaType.IMAGE);
    }

    public OssUploadResult uploadVideo(InputStream inputStream,
                                       long contentLength,
                                       String originalFilename,
                                       String contentType) {
        return upload(inputStream, contentLength, originalFilename, contentType, OssMediaType.VIDEO);
    }

    public OssUploadResult upload(InputStream inputStream,
                                  long contentLength,
                                  String originalFilename,
                                  String contentType,
                                  OssMediaType mediaType) {
        if (inputStream == null) {
            throw new BusinessException("上传文件不能为空");
        }
        if (mediaType == null) {
            throw new BusinessException("上传文件类型不能为空");
        }
        validateSize(contentLength, mediaType);

        String normalizedContentType = normalizeContentType(originalFilename, contentType);
        validateContentType(normalizedContentType, mediaType);

        String objectKey = buildObjectKey(mediaType, originalFilename, normalizedContentType);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(normalizedContentType);
        if (contentLength >= 0) {
            metadata.setContentLength(contentLength);
        }

        try {
            PutObjectRequest request = new PutObjectRequest(
                    requireText(properties.getBucketName(), "OSS bucketName未配置"),
                    objectKey,
                    inputStream,
                    metadata
            );
            PutObjectResult result = getClient().putObject(request);
            return OssUploadResult.builder()
                    .bucketName(properties.getBucketName())
                    .objectKey(objectKey)
                    .url(buildPublicUrl(objectKey))
                    .contentType(normalizedContentType)
                    .contentLength(contentLength >= 0 ? contentLength : null)
                    .etag(result.getETag())
                    .build();
        } catch (OSSException | ClientException ex) {
            throw new BusinessException(502, "上传到阿里云OSS失败：" + ex.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException ignored) {
                // Input stream cleanup best-effort after OSS upload.
            }
        }
    }

    public boolean doesObjectExist(String objectKey) {
        String normalizedObjectKey = requireText(objectKey, "OSS objectKey不能为空");
        try {
            return getClient().doesObjectExist(requireText(properties.getBucketName(), "OSS bucketName未配置"), normalizedObjectKey);
        } catch (OSSException | ClientException ex) {
            throw new BusinessException(502, "访问阿里云OSS失败：" + ex.getMessage());
        }
    }

    public String generateSignedUrl(String objectKey) {
        return generateSignedUrl(objectKey, Duration.ofHours(properties.getSignedUrlExpireHours()));
    }

    public String resolveAccessibleUrl(String objectKeyOrUrl) {
        String normalized = trimToNull(objectKeyOrUrl);
        if (normalized == null) {
            return null;
        }
        Optional<String> objectKey = extractObjectKey(normalized);
        return objectKey.map(this::generateSignedUrl).orElse(normalized);
    }

    public String generateSignedUrl(String objectKey, Duration expiresIn) {
        String normalizedObjectKey = normalizeObjectKey(objectKey);
        if (expiresIn == null || expiresIn.isNegative() || expiresIn.isZero()) {
            throw new BusinessException("签名URL有效期无效");
        }
        Date expiration = Date.from(Instant.now().plus(expiresIn));
        try {
            return getClient()
                    .generatePresignedUrl(requireText(properties.getBucketName(), "OSS bucketName未配置"), normalizedObjectKey, expiration)
                    .toString();
        } catch (OSSException | ClientException ex) {
            throw new BusinessException(502, "生成阿里云OSS签名URL失败：" + ex.getMessage());
        }
    }

    @PreDestroy
    public void close() {
        OSS client = ossClient;
        if (client != null) {
            client.shutdown();
        }
    }

    private OSS getClient() {
        if (!properties.isEnabled()) {
            throw new BusinessException("OSS服务未启用");
        }
        OSS client = ossClient;
        if (client == null) {
            synchronized (this) {
                client = ossClient;
                if (client == null) {
                    client = new OSSClientBuilder().build(
                            requireText(properties.getEndpoint(), "OSS endpoint未配置"),
                            requireText(properties.getAccessKeyId(), "OSS accessKeyId未配置"),
                            requireText(properties.getAccessKeySecret(), "OSS accessKeySecret未配置")
                    );
                    ossClient = client;
                }
            }
        }
        return client;
    }

    private void validateSize(long contentLength, OssMediaType mediaType) {
        if (contentLength < 0) {
            return;
        }
        long maxBytes = switch (mediaType) {
            case IMAGE -> properties.getImageMaxSizeMb() * 1024L * 1024L;
            case VIDEO -> properties.getVideoMaxSizeMb() * 1024L * 1024L;
        };
        if (maxBytes > 0 && contentLength > maxBytes) {
            throw new BusinessException("上传文件大小超过限制");
        }
    }

    private String normalizeContentType(String originalFilename, String contentType) {
        String normalized = trimToNull(contentType);
        if (normalized == null) {
            normalized = URLConnection.guessContentTypeFromName(originalFilename);
        }
        return normalized == null ? "application/octet-stream" : normalized.toLowerCase(Locale.ROOT);
    }

    private void validateContentType(String contentType, OssMediaType mediaType) {
        boolean valid = switch (mediaType) {
            case IMAGE -> contentType.startsWith("image/");
            case VIDEO -> contentType.startsWith("video/");
        };
        if (!valid) {
            throw new BusinessException(mediaType == OssMediaType.IMAGE ? "仅支持上传图片文件" : "仅支持上传视频文件");
        }
    }

    private String buildObjectKey(OssMediaType mediaType, String originalFilename, String contentType) {
        String directory = mediaType == OssMediaType.IMAGE ? properties.getImageDir() : properties.getVideoDir();
        String extension = resolveExtension(originalFilename, contentType, mediaType);
        return joinPath(
                properties.getObjectKeyPrefix(),
                directory,
                LocalDate.now().format(DATE_PATH_FORMATTER),
                UUID.randomUUID() + "." + extension
        );
    }

    private String resolveExtension(String originalFilename, String contentType, OssMediaType mediaType) {
        String extension = extensionFromFilename(originalFilename);
        Set<String> allowedExtensions = mediaType == OssMediaType.IMAGE ? IMAGE_EXTENSIONS : VIDEO_EXTENSIONS;
        if (extension != null && allowedExtensions.contains(extension)) {
            return extension;
        }
        if (contentType.contains("/")) {
            String subtype = contentType.substring(contentType.indexOf('/') + 1);
            int separator = subtype.indexOf(';');
            if (separator >= 0) {
                subtype = subtype.substring(0, separator);
            }
            subtype = switch (subtype) {
                case "jpeg", "pjpeg" -> "jpg";
                case "quicktime" -> "mov";
                default -> subtype;
            };
            if (allowedExtensions.contains(subtype)) {
                return subtype;
            }
        }
        return mediaType == OssMediaType.IMAGE ? "jpg" : "mp4";
    }

    private String extensionFromFilename(String originalFilename) {
        String filename = trimToNull(originalFilename);
        if (filename == null) {
            return null;
        }
        int index = filename.lastIndexOf('.');
        if (index < 0 || index == filename.length() - 1) {
            return null;
        }
        return filename.substring(index + 1).toLowerCase(Locale.ROOT);
    }

    private String buildPublicUrl(String objectKey) {
        String publicBaseUrl = trimToNull(properties.getPublicBaseUrl());
        if (publicBaseUrl != null) {
            return publicBaseUrl.replaceAll("/+$", "") + "/" + objectKey;
        }
        String endpoint = requireText(properties.getEndpoint(), "OSS endpoint未配置")
                .replaceFirst("^https?://", "")
                .replaceAll("/+$", "");
        return "https://" + properties.getBucketName() + "." + endpoint + "/" + objectKey;
    }

    private Optional<String> extractObjectKey(String objectKeyOrUrl) {
        if (!objectKeyOrUrl.startsWith("http://") && !objectKeyOrUrl.startsWith("https://")) {
            return Optional.of(normalizeObjectKey(objectKeyOrUrl));
        }

        try {
            URI uri = URI.create(objectKeyOrUrl);
            String host = trimToNull(uri.getHost());
            String path = trimToNull(uri.getRawPath());
            if (host == null || path == null) {
                return Optional.empty();
            }

            String rawObjectKey = path.replaceFirst("^/+", "");
            if (rawObjectKey.isEmpty()) {
                return Optional.empty();
            }

            if (isConfiguredOssHost(host)) {
                return Optional.of(normalizeObjectKey(rawObjectKey));
            }
            return Optional.empty();
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private boolean isConfiguredOssHost(String host) {
        return configuredOssHosts().stream().anyMatch(configuredHost -> configuredHost.equalsIgnoreCase(host));
    }

    private List<String> configuredOssHosts() {
        String endpointHost = endpointHost();
        String bucketHost = endpointHost == null || trimToNull(properties.getBucketName()) == null
                ? null
                : properties.getBucketName() + "." + endpointHost;
        return java.util.stream.Stream.of(bucketHost, baseUrlHost(properties.getPublicBaseUrl()))
                .filter(Objects::nonNull)
                .toList();
    }

    private String endpointHost() {
        String endpoint = trimToNull(properties.getEndpoint());
        if (endpoint == null) {
            return null;
        }
        try {
            URI uri = endpoint.startsWith("http://") || endpoint.startsWith("https://")
                    ? URI.create(endpoint)
                    : URI.create("https://" + endpoint);
            return trimToNull(uri.getHost());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private String baseUrlHost(String baseUrl) {
        String normalized = trimToNull(baseUrl);
        if (normalized == null) {
            return null;
        }
        try {
            URI uri = URI.create(normalized);
            return trimToNull(uri.getHost());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private String normalizeObjectKey(String objectKey) {
        String normalized = requireText(objectKey, "OSS objectKey不能为空");
        if (normalized.startsWith("/") || normalized.contains("..") || normalized.contains("\\")
                || normalized.startsWith("http://") || normalized.startsWith("https://")) {
            throw new BusinessException("OSS objectKey格式无效");
        }
        List<String> allowedPrefixes = properties.getAllowedReadPrefixes() == null
                ? List.of()
                : properties.getAllowedReadPrefixes();
        boolean allowed = allowedPrefixes.stream()
                .map(this::trimToNull)
                .filter(Objects::nonNull)
                .map(prefix -> prefix.replaceAll("^/+", "").replaceAll("/+$", ""))
                .filter(prefix -> !prefix.isEmpty())
                .anyMatch(prefix -> normalized.equals(prefix) || normalized.startsWith(prefix + "/"));
        if (!allowed) {
            throw new BusinessException("OSS objectKey不在允许访问目录内");
        }
        return normalized;
    }

    private String joinPath(String... segments) {
        StringBuilder builder = new StringBuilder();
        for (String segment : segments) {
            String normalized = trimToNull(segment);
            if (normalized == null) {
                continue;
            }
            normalized = normalized.replaceAll("^/+", "").replaceAll("/+$", "");
            if (normalized.isEmpty()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append('/');
            }
            builder.append(normalized);
        }
        return builder.toString();
    }

    private String requireText(String value, String message) {
        String normalized = trimToNull(value);
        if (normalized == null) {
            throw new BusinessException(message);
        }
        return normalized;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

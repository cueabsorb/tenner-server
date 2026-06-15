package com.irallyin.server.appgateway.controller;

import com.irallyin.server.common.response.ApiResponse;
import com.irallyin.server.core.oss.OssSignedUrlService;
import com.irallyin.server.core.oss.dto.OssSignedUrlRequest;
import com.irallyin.server.core.oss.dto.OssSignedUrlResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping({"/mobile/oss", "/api/mobile/oss"})
@RequiredArgsConstructor
@Tag(name = "Mobile OSS", description = "移动端OSS私有资源访问")
public class MobileOssController {
    private final OssSignedUrlService ossSignedUrlService;

    @PostMapping("/signed-url")
    @Operation(summary = "生成OSS临时访问URL", description = "登录用户获取24小时有效的OSS私有文件签名URL")
    public ApiResponse<OssSignedUrlResponse> generateSignedUrl(
            Authentication authentication,
            @Valid @RequestBody OssSignedUrlRequest request
    ) {
        return ApiResponse.success(ossSignedUrlService.generateReadUrl(request.getObjectKey(), currentUserId(authentication)));
    }

    private String currentUserId(Authentication authentication) {
        return ((UUID) authentication.getPrincipal()).toString();
    }
}

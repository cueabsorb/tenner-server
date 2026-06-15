package com.irallyin.server.core.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppMessageResponse {
    private String id;
    private String messageType;
    private String direction;
    private String senderId;
    private String senderName;
    private String senderAvatarUrl;
    private String recipientId;
    private String title;
    private String content;
    private Boolean readByRecipient;
    private String createdAt;
}

package com.irallyin.server.core.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppMessageUnreadCountResponse {
    private Integer totalUnreadCount;
    private Integer chatUnreadCount;
    private Integer systemUnreadCount;
}

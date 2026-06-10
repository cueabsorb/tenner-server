package com.irallyin.server.core.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRacketAddResponse {
    private String racketId;
    private Integer racketCount;
    private String message;
}

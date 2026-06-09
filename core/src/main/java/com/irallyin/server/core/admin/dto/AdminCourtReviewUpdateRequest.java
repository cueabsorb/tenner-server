package com.irallyin.server.core.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminCourtReviewUpdateRequest {
    @NotBlank(message = "审核状态不能为空")
    @Pattern(regexp = "pending|approved|voided|blacklisted", message = "审核状态不合法")
    private String approvalStatus;

    @Size(max = 500, message = "原因不能超过500个字符")
    private String reason;
}

package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RacketCatalogCreateRequest {
    @NotBlank(message = "品牌不能为空")
    private String brand;

    @NotBlank(message = "球拍型号不能为空")
    private String model;

    private Integer unstrungWeightGram;
    private String stringPattern;
    private Integer balancePointMm;
    private Double lengthInch;
    private String gripSize;
    private Integer releaseYear;
    private String imageUrl;
}

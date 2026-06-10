package com.irallyin.server.core.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RacketCatalogResponse {
    private String id;
    private String brand;
    private String model;
    private Integer unstrungWeightGram;
    private String stringPattern;
    private Integer balancePointMm;
    private Double lengthInch;
    private String gripSize;
    private Integer releaseYear;
    private String imageUrl;
}

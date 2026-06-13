package com.irallyin.server.appgateway.controller;

import com.irallyin.server.common.response.ApiResponse;
import com.irallyin.server.core.ranking.dto.RankingSnapshotResponse;
import com.irallyin.server.core.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/mobile/rankings", "/api/mobile/rankings"})
public class MobileRankingController {
    private final RankingService rankingService;

    @GetMapping
    public ApiResponse<RankingSnapshotResponse> getLatestSnapshot() {
        return ApiResponse.success(rankingService.getLatestSnapshot());
    }
}

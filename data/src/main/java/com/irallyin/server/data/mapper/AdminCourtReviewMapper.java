package com.irallyin.server.data.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AdminCourtReviewMapper {

    List<Map<String, Object>> findCourts(@Param("status") String status);

    Map<String, Object> findCourtById(@Param("courtId") String courtId);

    Map<String, Object> findPendingChangeRequestByCourtId(@Param("courtId") String courtId);

    int updatePendingCourtDraft(@Param("courtId") String courtId, @Param("values") Map<String, Object> values);

    int updatePendingChangeRequestDraft(@Param("requestId") String requestId, @Param("values") Map<String, Object> values);

    int updateCourtReviewStatus(
            @Param("courtId") String courtId,
            @Param("approvalStatus") String approvalStatus,
            @Param("venueStatus") String venueStatus,
            @Param("reviewedBy") String reviewedBy,
            @Param("reason") String reason
    );

    int applyCourtChangeRequest(@Param("requestId") String requestId, @Param("reviewedBy") String reviewedBy);

    int updateCourtChangeRequestReviewStatus(
            @Param("requestId") String requestId,
            @Param("requestStatus") String requestStatus,
            @Param("reviewedBy") String reviewedBy,
            @Param("reason") String reason
    );

    int deletePendingCourt(@Param("courtId") String courtId, @Param("reviewedBy") String reviewedBy, @Param("reason") String reason);

    int deletePendingChangeRequest(@Param("requestId") String requestId, @Param("reviewedBy") String reviewedBy, @Param("reason") String reason);
}

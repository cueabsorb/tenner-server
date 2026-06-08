package com.irallyin.server.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "游标分页结果")
public class PageResult<T> {

    @Schema(description = "数据列表")
    private List<T> items;

    @Schema(description = "下一页游标(最后一条记录ID)")
    private String nextCursor;

    @Schema(description = "是否还有更多数据")
    private boolean hasMore;
}

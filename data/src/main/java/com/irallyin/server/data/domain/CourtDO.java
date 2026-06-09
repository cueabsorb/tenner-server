package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 球场。
 */
@Data
@TableName("courts")
public class CourtDO implements Serializable {
    /**
     * 序列化版本号。
     */
    private static final long serialVersionUID = 1L;

    /**
     * UUID v4。
     */
    @TableId
    private String id;
    /**
     * 关联areas.code。
     */
    private String areaCode;
    /**
     * 球场名。
     */
    private String name;
    /**
     * 地址。
     */
    private String address;
    /**
     * 纬度。
     */
    private Double latitude;
    /**
     * 经度。
     */
    private Double longitude;
    /**
     * 地面类型，可多选: hard=硬地, clay=红土, grass=草地, sand_grass=沙草, carpet=地毯。
     */
    private String surfaceType;
    /**
     * 室内外历史字段: indoor=室内, outdoor=室外, both=都有。
     */
    private String indoorOutdoor;
    /**
     * 是否有室内场地。
     */
    private Boolean hasIndoor;
    /**
     * 是否有室外场地。
     */
    private Boolean hasOutdoor;
    /**
     * 球场总片数。
     */
    private Integer totalCourtCount;
    /**
     * 室内球场片数。
     */
    private Integer indoorCourtCount;
    /**
     * 室外球场片数。
     */
    private Integer outdoorCourtCount;
    /**
     * 每日营业开始时间。
     */
    private LocalTime openingTime;
    /**
     * 每日营业结束时间。
     */
    private LocalTime closingTime;
    /**
     * 联系电话。
     */
    private String contactPhone;
    /**
     * 设施如["parking","locker_room"]。
     */
    private String amenities;
    /**
     * 场地运营状态。
     */
    private String venueStatus;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
    /**
     * 国家/地区。
     */
    private String country;
    /**
     * 城市。
     */
    private String city;
    /**
     * 微信小程序名称。
     */
    private String wechatMiniProgramName;
    /**
     * 球场照片URL数组，建议3-5张。
     */
    private String photoUrls;
    /**
     * 球场补充信息描述。
     */
    private String description;
    /**
     * 提交该球场的用户ID。
     */
    private String createdBy;
    /**
     * 平台审核状态。
     */
    private String approvalStatus;
    /**
     * 是否场馆主动运营。
     */
    private Boolean operatorManaged;
    /**
     * 审核人ID。
     */
    private String reviewedBy;
    /**
     * 审核时间。
     */
    private LocalDateTime reviewedAt;
    /**
     * 拒绝原因。
     */
    private String rejectedReason;
}

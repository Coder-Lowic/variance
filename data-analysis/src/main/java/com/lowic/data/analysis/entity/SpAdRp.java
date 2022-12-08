package com.lowic.data.analysis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author lowic
 * @since 2022-12-08
 */
@Data
@TableName("sp_ad_rp")
@ApiModel(value = "SpAdRp对象", description = "SpAdRp对象")
public class SpAdRp implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime date;

    private String portfolioName;

    private String currency;

    private String campaignName;

    private String adGroupName;

    private String advertisedSku;

    private String advertisedAsin;

    private Long impressions;

    private Long clicks;

    private BigDecimal ctr;

    private BigDecimal cpc;

    private BigDecimal spend;

    private BigDecimal sevenDayTotalSales;

    private BigDecimal acos;

    private BigDecimal roas;

    private Integer sevenDayTotalOrders;

    private Integer sevenDayTotalUnits;

    private BigDecimal sevenDayConversionRate;

    private Integer sevenDayAdvertisedSkuUnits;

    private Integer sevenDayOtherSkuUnits;

    private BigDecimal sevenDayAdvertisedSkuSales;

    private BigDecimal sevenDayOtherSkuSales;

}

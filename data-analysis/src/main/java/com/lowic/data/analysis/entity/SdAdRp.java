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
 * @since 2022-12-05
 */
@Data
@TableName("sd_ad_rp")
@ApiModel(value = "SdAdRp对象", description = "SdAdRp对象")
public class SdAdRp implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime date;

    private String currency;

    private String campaignName;

    private String portfolioName;

    private String costType;

    private String adGroupName;

    private String bidOptimization;

    private String advertisedSku;

    private String advertisedAsin;

    private Long impressions;

    private Long viewableImpressions;

    private Long clicks;

    private BigDecimal ctr;

    private Integer fourteenDayDetailPageViews;

    private BigDecimal spend;

    private BigDecimal cpc;

    private BigDecimal vcpm;

    private BigDecimal acos;

    private BigDecimal roas;

    private Integer fourteenDayTotalOrders;

    private Integer fourteenDayTotalUnits;

    private BigDecimal fourteenDayTotalSales;

    private Integer fourteenDayNewToBrandOrders;

    private BigDecimal fourteenDayNewToBrandSales;

    private Integer fourteenDayNewToBrandUnits;

    private BigDecimal totalAdvertisingCostOfSalesAcosClick;

    private BigDecimal totalReturnOnAdvertisingSpendRoasClick;

    private Integer fourteenDayTotalOrdersClick;

    private Integer fourteenDayTotalUnitsClick;

    private BigDecimal fourteenDayTotalSalesClick;

    private Integer fourteenDayNewToBrandOrdersClick;

    private BigDecimal fourteenDayNewToBrandSalesClick;

    private Integer fourteenDayNewToBrandUnitsClick;

}

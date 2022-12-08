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
@TableName("sb_camp_rp")
@ApiModel(value = "SbCampRp对象", description = "")
public class SbCampRp implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime date;

    private String portfolioName;

    private String currency;

    private String campaignName;

    private Long impressions;

    private Integer clicks;

    private BigDecimal ctr;

    private BigDecimal cpc;

    private BigDecimal spend;

    private BigDecimal acos;

    private BigDecimal roas;

    private BigDecimal fourteenDayTotalSales;

    private Integer fourteenDayTotalOrders;

    private Integer fourteenDayTotalUnits;

    private BigDecimal fourteenDayConversionRate;

    private Long viewableImpressions;

    private BigDecimal viewThroughRate;

    private BigDecimal clickThroughRateForViews;

    private Integer videoFirstQuartileViews;

    private Integer videoMidpointViews;

    private Integer videoThirdQuartileViews;

    private Integer videoCompleteViews;

    private Integer videoUnmutes;

    private Integer fiveSecondViews;

    private BigDecimal fiveSecondViewRate;

    private Integer fourteenDayBrandedSearches;

    private Integer fourteenDayDetailPageViewsDpv;

    private Integer fourteenDayNewToBrandOrders;

    private BigDecimal fourteenDayOfOrdersNewToBrand;

    private BigDecimal fourteenDayNewToBrandSales;

    private BigDecimal fourteenDayOfSalesNewToBrand;

    private Integer fourteenDayNewToBrandUnits;

    private BigDecimal fourteenDayOfUnitsNewToBrand;

    private BigDecimal fourteenDayNewToBrandOrderRate;

}

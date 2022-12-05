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

    private Integer Day14DetailPageViews;

    private BigDecimal spend;

    private BigDecimal cpc;

    private BigDecimal vcpm;

    private BigDecimal acos;

    private BigDecimal roas;

    private Integer Day14TotalOrders;

    private Integer Day14TotalUnits;

    private BigDecimal Day14TotalSales;

    private Integer Day14NewToBrandOrders;

    private BigDecimal Day14NewToBrandSales;

    private Integer Day14NewToBrandUnits;

    private BigDecimal totalAdvertisingCostOfSalesAcosClick;

    private BigDecimal totalReturnOnAdvertisingSpendRoasClick;

    private Integer Day14TotalOrdersClick;

    private Integer Day14TotalUnitsClick;

    private BigDecimal Day14TotalSalesClick;

    private Integer Day14NewToBrandOrdersClick;

    private BigDecimal Day14NewToBrandSalesClick;

    private Integer Day14NewToBrandUnitsClick;

}

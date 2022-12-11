package com.lowic.data.analysis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author lowic
 * @since 2022-12-11
 */
@Data
@TableName("business_report")
@ApiModel(value = "BusinessReport对象", description = "")
public class BusinessReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String parentAsin;

    private String childAsin;

    private String title;

    private String sku;

    private Integer sessionsTotal;

    private BigDecimal sessionPercentageTotal;

    private Integer pageViewsTotal;

    private BigDecimal pageViewsPercentageTotal;

    private BigDecimal buyBox;

    private Integer unitsOrder;

    private Integer orderB2b;

    private BigDecimal unitSessionPercentage;

    private BigDecimal unitSessionPercentageB2b;

    private BigDecimal sales;

    private BigDecimal salesB2b;

    private Integer totalOrderItems;

    private Integer totalOrderItemsB2b;

}

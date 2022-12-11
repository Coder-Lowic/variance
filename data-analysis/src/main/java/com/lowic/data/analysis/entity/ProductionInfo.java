package com.lowic.data.analysis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author lowic
 * @since 2022-12-11
 */
@Data
@TableName("production_info")
@ApiModel(value = "ProductionInfo对象", description = "")
public class ProductionInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String amazonSku;

    private String erpSku;

    private String asin;

    private String productName;

    private String productAbbreviation;

    private String categoryOne;

    private String categoryTwo;

    private String shop;

    private String productOwner;

    private String productGrade;

    private String salesStatus;

    private String brand;

}

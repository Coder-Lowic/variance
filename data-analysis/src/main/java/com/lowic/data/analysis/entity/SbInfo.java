package com.lowic.data.analysis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("sb_info")
@ApiModel(value = "SbInfo对象", description = "")
public class SbInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String campaignName;

    private String sbType;

    private String categoryOne;

    private String categoryTwo;

    private String productName;

    private String productSku;

    private String brand;

}

package com.lowic.data.analysis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 导入操作记录表
 * </p>
 *
 * @author lowic
 * @since 2022-12-06
 */
@Data
@Builder
@TableName("import_operate_record")
@ApiModel(value = "ImportOperateRecord对象", description = "导入操作记录表")
public class ImportOperateRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String targetTable;

    private Integer importCounts;

    private Long costTime;

    private Integer createId;

    private LocalDateTime createTime;

}

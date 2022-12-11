package com.lowic.data.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowic.data.analysis.entity.BusinessReport;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lowic
 * @since 2022-12-11
 */
public interface BusinessReportMapper extends BaseMapper<BusinessReport> {
    /**
     * 批量插入
     *
     * @param businessReportList 源数据
     */
    void batchInsert(List<BusinessReport> businessReportList);
}

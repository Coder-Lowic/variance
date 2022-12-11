package com.lowic.data.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowic.data.analysis.entity.ProductionInfo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lowic
 * @since 2022-12-11
 */
public interface ProductionInfoMapper extends BaseMapper<ProductionInfo> {
    /**
     * 批量插入
     *
     * @param productionInfoList 源数据
     */
    void batchInsert(List<ProductionInfo> productionInfoList);
}

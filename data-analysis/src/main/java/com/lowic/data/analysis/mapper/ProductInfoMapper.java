package com.lowic.data.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowic.data.analysis.entity.ProductInfo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lowic
 * @since 2022-12-11
 */
public interface ProductInfoMapper extends BaseMapper<ProductInfo> {
    /**
     * 批量插入
     *
     * @param productInfoList 源数据
     */
    void batchInsert(List<ProductInfo> productInfoList);
}

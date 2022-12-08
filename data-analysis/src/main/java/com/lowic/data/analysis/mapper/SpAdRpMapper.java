package com.lowic.data.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowic.data.analysis.entity.SpAdRp;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lowic
 * @since 2022-12-08
 */
public interface SpAdRpMapper extends BaseMapper<SpAdRp> {
    /**
     * 批量插入
     *
     * @param spAdRpList 源数据
     */
    void batchInsert(List<SpAdRp> spAdRpList);
}

package com.lowic.data.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowic.data.analysis.entity.SbCampRp;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lowic
 * @since 2022-12-08
 */
public interface SbCampRpMapper extends BaseMapper<SbCampRp> {
    /**
     * 批量插入
     *
     * @param sbCampRpList 源数据
     */
    void batchInsert(List<SbCampRp> sbCampRpList);
}

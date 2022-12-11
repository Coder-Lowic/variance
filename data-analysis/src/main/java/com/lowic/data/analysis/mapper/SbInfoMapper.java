package com.lowic.data.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowic.data.analysis.entity.SbInfo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lowic
 * @since 2022-12-11
 */
public interface SbInfoMapper extends BaseMapper<SbInfo> {
    /**
     * 批量插入
     *
     * @param sbInfoList 源数据
     */
    void batchInsert(List<SbInfo> sbInfoList);
}

package com.lowic.data.analysis.mapper;

import com.lowic.data.analysis.entity.SdAdRp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lowic
 * @since 2022-12-05
 */
public interface SdAdRpMapper extends BaseMapper<SdAdRp> {

    /**
     * 批量插入
     *
     * @param sdAdRpList 源数据
     */
    void batchInsert(List<SdAdRp> sdAdRpList);

}

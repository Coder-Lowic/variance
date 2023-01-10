package com.lowic.data.analysis.mapper;

import com.lowic.data.analysis.export.vo.SpCombineBr;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lowic
 * @since 2022-12-11
 */
public interface AnalysisMapper {
    /**
     * 关联查询 sp_ad_rp 和 business_report 表
     *
     * @return 按天和asin汇总的数据
     */
    List<SpCombineBr> listSpAdRpAndSpAdRpContactBuRp();

    /**
     * 关联查询 sb_info 和 business_report 表
     *
     * @return 按天和一级类目汇总的数据
     */
    List<T> listSbInfoContactBuRp();

}

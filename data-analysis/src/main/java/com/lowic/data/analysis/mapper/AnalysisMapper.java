package com.lowic.data.analysis.mapper;

import com.lowic.data.analysis.export.vo.SbCampAndSpOrSdCombineBr;
import com.lowic.data.analysis.export.vo.SpOrSdCombineBr;

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
     * (sp_ad_rp + business_report) + (sd_ad_rp + business_report)
     *
     * @return 按天和asin汇总的数据
     */
    List<SpOrSdCombineBr> listSpAdRpAndSpAdRpCombineBuRp();

    /**
     * (sb_camp_rp + business_report) + ((sp_ad_rp + business_report) + (sd_ad_rp + business_report))
     *
     * @return 按天和一级类目汇总的数据
     */
    List<SbCampAndSpOrSdCombineBr> listSbCampAndSpAndSdCombineBuRp();

}

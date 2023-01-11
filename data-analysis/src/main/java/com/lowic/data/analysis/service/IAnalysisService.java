package com.lowic.data.analysis.service;

import com.lowic.data.analysis.export.vo.SbCampAndSpOrSdCombineBr;
import com.lowic.data.analysis.export.vo.SpOrSdCombineBr;

import java.util.List;

/**
 * @author Lowic
 */
public interface IAnalysisService {
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

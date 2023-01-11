package com.lowic.data.analysis.export.service.impl;

import com.lowic.data.analysis.export.service.IExportService;
import com.lowic.data.analysis.export.vo.SbCampAndSpOrSdCombineBr;
import com.lowic.data.analysis.export.vo.SpOrSdCombineBr;
import com.lowic.data.analysis.service.IAnalysisService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Lowic
 */
@Service
public class IExportServiceImpl implements IExportService {
    @Resource
    private IAnalysisService iAnalysisService;

    @Override
    public List<SpOrSdCombineBr> assembleSpAndSdCombineBuRpExportList() {

        return iAnalysisService.listSpAdRpAndSpAdRpCombineBuRp();
    }

    @Override
    public List<SbCampAndSpOrSdCombineBr> assembleSbCampAndSpAndSdCombineBuRpExportList() {
        return iAnalysisService.listSbCampAndSpAndSdCombineBuRp();
    }
}

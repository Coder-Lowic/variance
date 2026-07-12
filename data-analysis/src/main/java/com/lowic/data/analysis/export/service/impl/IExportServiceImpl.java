package com.lowic.data.analysis.export.service.impl;

import com.lowic.data.analysis.export.service.IExportService;
import com.lowic.data.analysis.export.vo.SbCampAndSpOrSdCombineBr;
import com.lowic.data.analysis.export.vo.SpOrSdCombineBr;
import com.lowic.data.analysis.service.IAnalysisService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lowic
 */
@Service
public class IExportServiceImpl implements IExportService {
    private final IAnalysisService iAnalysisService;

    public IExportServiceImpl(IAnalysisService iAnalysisService) {
        this.iAnalysisService = iAnalysisService;
    }

    @Override
    public List<SpOrSdCombineBr> assembleSpAndSdCombineBuRpExportList() {

        return iAnalysisService.listSpAdRpAndSpAdRpCombineBuRp();
    }

    @Override
    public List<SbCampAndSpOrSdCombineBr> assembleSbCampAndSpAndSdCombineBuRpExportList() {
        return iAnalysisService.listSbCampAndSpAndSdCombineBuRp();
    }
}

package com.lowic.data.analysis.export.service.impl;

import com.lowic.data.analysis.export.service.IExportService;
import com.lowic.data.analysis.export.vo.SpCombineBr;
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
    public List<SpCombineBr> assembleExportList() {

        return iAnalysisService.listSpAdRpContactBuRp();
    }
}

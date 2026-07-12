package com.lowic.data.analysis.service.impl;

import com.lowic.data.analysis.export.vo.SbCampAndSpOrSdCombineBr;
import com.lowic.data.analysis.export.vo.SpOrSdCombineBr;
import com.lowic.data.analysis.mapper.AnalysisMapper;
import com.lowic.data.analysis.service.IAnalysisService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lowic
 */
@Service
public class AnalysisServiceImpl implements IAnalysisService {
    private final AnalysisMapper analysisMapper;

    public AnalysisServiceImpl(AnalysisMapper analysisMapper) {
        this.analysisMapper = analysisMapper;
    }

    @Override
    public List<SpOrSdCombineBr> listSpAdRpAndSpAdRpCombineBuRp() {
        return analysisMapper.listSpAdRpAndSpAdRpCombineBuRp();
    }

    @Override
    public List<SbCampAndSpOrSdCombineBr> listSbCampAndSpAndSdCombineBuRp() {
        return analysisMapper.listSbCampAndSpAndSdCombineBuRp();
    }
}

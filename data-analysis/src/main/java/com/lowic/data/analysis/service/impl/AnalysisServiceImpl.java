package com.lowic.data.analysis.service.impl;

import com.lowic.data.analysis.export.vo.SbCampAndSpOrSdCombineBr;
import com.lowic.data.analysis.export.vo.SpOrSdCombineBr;
import com.lowic.data.analysis.mapper.AnalysisMapper;
import com.lowic.data.analysis.service.IAnalysisService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Lowic
 */
@Service
public class AnalysisServiceImpl implements IAnalysisService {
    @Resource
    private AnalysisMapper analysisMapper;

    @Override
    public List<SpOrSdCombineBr> listSpAdRpAndSpAdRpCombineBuRp() {
        return analysisMapper.listSpAdRpAndSpAdRpCombineBuRp();
    }

    @Override
    public List<SbCampAndSpOrSdCombineBr> listSbCampAndSpAndSdCombineBuRp() {
        return analysisMapper.listSbCampAndSpAndSdCombineBuRp();
    }
}

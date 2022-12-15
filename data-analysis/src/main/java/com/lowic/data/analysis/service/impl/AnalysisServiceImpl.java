package com.lowic.data.analysis.service.impl;

import com.lowic.data.analysis.mapper.AnalysisMapper;
import com.lowic.data.analysis.service.IAnalysisService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Lowic
 */
@Service
public class AnalysisServiceImpl implements IAnalysisService {
    @Resource
    private AnalysisMapper analysisMapper;
}

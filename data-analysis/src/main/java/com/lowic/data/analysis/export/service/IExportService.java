package com.lowic.data.analysis.export.service;

import com.lowic.data.analysis.export.vo.SpCombineBr;

import java.util.List;

/**
 * @author Lowic
 */
public interface IExportService {
    List<SpCombineBr> assembleExportList();
}

package com.lowic.data.analysis.export.service;

import com.lowic.data.analysis.export.vo.SbCampAndSpOrSdCombineBr;
import com.lowic.data.analysis.export.vo.SpOrSdCombineBr;

import java.util.List;

/**
 * @author Lowic
 */
public interface IExportService {
    /**
     * 组装待导出的数据
     *
     * @return 组装好的数据
     */
    List<SpOrSdCombineBr> assembleSpAndSdCombineBuRpExportList();

    /**
     * 组装待导出的数据
     *
     * @return 组装好的数据
     */
    List<SbCampAndSpOrSdCombineBr> assembleSbCampAndSpAndSdCombineBuRpExportList();
}

package com.lowic.data.analysis.export.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.lowic.data.analysis.export.service.IExportService;
import com.lowic.data.analysis.export.vo.SbCampAndSpOrSdCombineBr;
import com.lowic.data.analysis.export.vo.SpOrSdCombineBr;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Lowic
 */
@RequestMapping("export")
@RestController
public class ExportController {
    @Resource
    private IExportService iExportService;


    @PostMapping("exportSpAdRpCombineBuRp")
    public void listSpAdRpCombineBuRp(HttpServletResponse response) {
        // 通过工具类创建writer，xlsx格式
        ExcelWriter writer = ExcelUtil.getWriter(true);
        List<SpOrSdCombineBr> spOrSdCombineBrList = iExportService.assembleSpAndSdCombineBuRpExportList();
        // 一次性写出内容，使用默认样式，强制输出标题
        exportToResponse(response, writer, spOrSdCombineBrList);
    }

    @PostMapping("exportSbCampAndSpAndSdCombineBuRp")
    public void exportSbCampAndSpAndSdCombineBuRp(HttpServletResponse response) {
        // 通过工具类创建writer，xlsx格式
        ExcelWriter writer = ExcelUtil.getWriter(true);
        List<SbCampAndSpOrSdCombineBr> sbCampAndSpOrSdCombineBrList = iExportService.assembleSbCampAndSpAndSdCombineBuRpExportList();
        // 一次性写出内容，使用默认样式，强制输出标题
        exportToResponse(response, writer, sbCampAndSpOrSdCombineBrList);
    }

    private void exportToResponse(HttpServletResponse response, ExcelWriter writer, List<?> exportList) {
        writer.write(exportList, true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=export.xlsx");

        ServletOutputStream out;
        try {
            // OutputStream 需要写出到的目标流
            out = response.getOutputStream();
            writer.flush(out, true);
            // 关闭writer，释放内存
            writer.close();
            // 此处记得关闭输出Servlet流
            IoUtil.close(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

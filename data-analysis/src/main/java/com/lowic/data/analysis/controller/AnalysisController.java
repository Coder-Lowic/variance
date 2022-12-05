package com.lowic.data.analysis.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.lowic.data.analysis.entity.SdAdRp;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author Lowic
 */
@RestController
@RequestMapping("analysis")
public class AnalysisController {
    @RequestMapping("uploadExcel")
    public String uploadExcel(@RequestParam(value = "file") MultipartFile multipartFile, String name) {
        try (ExcelReader excelReader = ExcelUtil.getReader(multipartFile.getInputStream())) {
            List<SdAdRp> sdAdRpList = excelReader.read(0, 1, SdAdRp.class);
            sdAdRpList.forEach(o -> System.out.println(o.toString()));
            System.out.println(sdAdRpList.size());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "上传成功";
    }
}

package com.lowic.data.analysis.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lowic.data.analysis.entity.BusinessReport;
import com.lowic.data.analysis.entity.ImportOperateRecord;
import com.lowic.data.analysis.entity.ProductInfo;
import com.lowic.data.analysis.entity.SbCampRp;
import com.lowic.data.analysis.entity.SbInfo;
import com.lowic.data.analysis.entity.SdAdRp;
import com.lowic.data.analysis.entity.SpAdRp;
import com.lowic.data.analysis.mapper.BusinessReportMapper;
import com.lowic.data.analysis.mapper.ProductInfoMapper;
import com.lowic.data.analysis.mapper.SbCampRpMapper;
import com.lowic.data.analysis.mapper.SbInfoMapper;
import com.lowic.data.analysis.mapper.SdAdRpMapper;
import com.lowic.data.analysis.mapper.SpAdRpMapper;
import com.lowic.data.analysis.service.IAnalysisService;
import com.lowic.data.analysis.service.IImportOperateRecordService;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("analysis")
public class AnalysisController {
    @Resource
    private IAnalysisService iAnalysisService;
    @Resource
    private IImportOperateRecordService iImportOperateRecordService;
    @Resource
    private SqlSessionTemplate sqlSessionTemplate;

    private static final int BATCH_COUNT = 5000;

    @RequestMapping("uploadExcelForSdAdRp")
    public String uploadExcelForSdAdRp(@RequestParam(value = "file") MultipartFile multipartFile, String name) {
        return batchInsert(multipartFile, SdAdRp.class, SdAdRpMapper.class, 0, 1);
    }

    @RequestMapping("uploadExcelForSpAdRp")
    public String uploadExcelForSpAdRp(@RequestParam(value = "file") MultipartFile multipartFile, String name) {
        return batchInsert(multipartFile, SpAdRp.class, SpAdRpMapper.class, 0, 1);
    }

    @RequestMapping("uploadExcelForSbCampRp")
    public String uploadExcelForSbCampRp(@RequestParam(value = "file") MultipartFile multipartFile, String name) {
        return batchInsert(multipartFile, SbCampRp.class, SbCampRpMapper.class, 0, 1);
    }

    @RequestMapping("uploadExcelForBusinessReport")
    public String uploadExcelForBusinessReport(@RequestParam(value = "file") MultipartFile multipartFile, String name) {
        return batchInsert(multipartFile, BusinessReport.class, BusinessReportMapper.class, 0, 1);
    }

    @RequestMapping("uploadExcelForSbInfo")
    public String uploadExcelForSbInfo(@RequestParam(value = "file") MultipartFile multipartFile, String name) {
        return batchInsert(multipartFile, SbInfo.class, SbInfoMapper.class, 0, 1);
    }

    @RequestMapping("uploadExcelForProductionInfo")
    public String uploadExcelForProductionInfo(@RequestParam(value = "file") MultipartFile multipartFile, String name) {
        return batchInsert(multipartFile, ProductInfo.class, ProductInfoMapper.class, 1, 2);
    }

    private <T> String batchInsert(MultipartFile multipartFile, Class<T> entityClass, Class<?> mapperClass, int headerRowIndex, int dataStartRowIndex) {
        LocalDateTime startTime = LocalDateTime.now();
        try (ExcelReader excelReader = ExcelUtil.getReader(multipartFile.getInputStream())) {
            List<T> dataList = excelReader.read(headerRowIndex, dataStartRowIndex, entityClass);

            try (SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false)) {
                Object mapper = sqlSession.getMapper(mapperClass);
                int batchLastIndex = BATCH_COUNT;

                for (int index = 0; index < dataList.size(); ) {
                    if (dataList.size() < batchLastIndex) {
                        batchLastIndex = dataList.size();
                        invokeBatchInsert(mapper, dataList.subList(index, batchLastIndex));
                        break;
                    } else {
                        invokeBatchInsert(mapper, dataList.subList(index, batchLastIndex));
                        index = batchLastIndex;
                        batchLastIndex = index + (BATCH_COUNT - 1);
                    }
                }
                sqlSession.commit();
            }

            LocalDateTime endTime = LocalDateTime.now();
            ImportOperateRecord importOperateRecord = ImportOperateRecord.builder()
                    .targetTable(entityClass.getAnnotation(TableName.class).value()).importCounts(dataList.size())
                    .costTime(Duration.between(startTime, endTime).toSeconds())
                    .createTime(LocalDateTime.now()).createId(1004).build();
            iImportOperateRecordService.save(importOperateRecord);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "上传成功";
    }

    private void invokeBatchInsert(Object mapper, List<?> subList) {
        try {
            mapper.getClass().getMethod("batchInsert", List.class).invoke(mapper, subList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
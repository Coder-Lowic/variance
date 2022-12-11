package com.lowic.data.analysis.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lowic.data.analysis.entity.BusinessReport;
import com.lowic.data.analysis.entity.ImportOperateRecord;
import com.lowic.data.analysis.entity.ProductionInfo;
import com.lowic.data.analysis.entity.SbCampRp;
import com.lowic.data.analysis.entity.SbInfo;
import com.lowic.data.analysis.entity.SdAdRp;
import com.lowic.data.analysis.entity.SpAdRp;
import com.lowic.data.analysis.mapper.BusinessReportMapper;
import com.lowic.data.analysis.mapper.ProductionInfoMapper;
import com.lowic.data.analysis.mapper.SbCampRpMapper;
import com.lowic.data.analysis.mapper.SbInfoMapper;
import com.lowic.data.analysis.mapper.SdAdRpMapper;
import com.lowic.data.analysis.mapper.SpAdRpMapper;
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

/**
 * @author Lowic
 */
@RestController
@RequestMapping("analysis")
public class AnalysisController {
    @Resource
    private IImportOperateRecordService iImportOperateRecordService;
    @Resource
    private SqlSessionTemplate sqlSessionTemplate;

    @RequestMapping("uploadExcelForSdAdRp")
    public String uploadExcelForSdAdRp(@RequestParam(value = "file") MultipartFile multipartFile, String name) {
        LocalDateTime startTime = LocalDateTime.now();
        try (ExcelReader excelReader = ExcelUtil.getReader(multipartFile.getInputStream())) {
            List<SdAdRp> sdAdRpList = excelReader.read(0, 1, SdAdRp.class);

            SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
            SdAdRpMapper mapper = sqlSession.getMapper(SdAdRpMapper.class);
            // 每批次导入的最大数据
            int batchCount = 5000;
            // 每批最后一条数据下标等于批量大小
            int batchLastIndex = batchCount;
            // 批量插入   index 为 下标
            for (int index = 0; index < sdAdRpList.size(); ) {
                // 如果读取的数量大小 小于 批量大小
                if (sdAdRpList.size() < batchLastIndex) {
                    batchLastIndex = sdAdRpList.size();
                    mapper.batchInsert(sdAdRpList.subList(index, batchLastIndex));
                    // 清除缓存
                    sqlSession.clearCache();
                    break;
                } else {
                    mapper.batchInsert(sdAdRpList.subList(index, batchLastIndex));
                    // 清除缓存 防止溢出
                    sqlSession.clearCache();
                    index = batchLastIndex;
                    batchLastIndex = index + (batchCount - 1);
                }
            }
            // 将数据提交到数据库，否则的话只是执行，但是并没有提交数据到数据库
            sqlSession.commit();
            // 关闭
            sqlSession.close();

            LocalDateTime endTime = LocalDateTime.now();
            ImportOperateRecord importOperateRecord = ImportOperateRecord.builder()
                    .targetTable(SdAdRp.class.getAnnotation(TableName.class).value()).importCounts(sdAdRpList.size())
                    .costTime(Duration.between(startTime,endTime).toSeconds())
                    .createTime(LocalDateTime.now()).createId(1004).build();
            iImportOperateRecordService.save(importOperateRecord);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "上传成功";
    }

    @RequestMapping("uploadExcelForSpAdRp")
    public String uploadExcelForSpAdRp(@RequestParam(value = "file") MultipartFile multipartFile, String name) {
        try (ExcelReader excelReader = ExcelUtil.getReader(multipartFile.getInputStream())) {
            List<SpAdRp> spAdRpList = excelReader.read(0, 1, SpAdRp.class);

            SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
            SpAdRpMapper mapper = sqlSession.getMapper(SpAdRpMapper.class);
            // 每批次导入的最大数据
            int batchCount = 5000;
            // 每批最后一条数据下标等于批量大小
            int batchLastIndex = batchCount;
            // 批量插入   index 为 下标
            for (int index = 0; index < spAdRpList.size(); ) {
                // 如果读取的数量大小 小于 批量大小
                if (spAdRpList.size() < batchLastIndex) {
                    batchLastIndex = spAdRpList.size();
                    mapper.batchInsert(spAdRpList.subList(index, batchLastIndex));
                    // 清除缓存
                    sqlSession.clearCache();
                    break;
                } else {
                    mapper.batchInsert(spAdRpList.subList(index, batchLastIndex));
                    // 清除缓存 防止溢出
                    sqlSession.clearCache();
                    index = batchLastIndex;
                    batchLastIndex = index + (batchCount - 1);
                }
            }
            // 将数据提交到数据库，否则的话只是执行，但是并没有提交数据到数据库
            sqlSession.commit();
            // 关闭
            sqlSession.close();

            ImportOperateRecord importOperateRecord = ImportOperateRecord.builder()
                    .targetTable(SpAdRp.class.getAnnotation(TableName.class).value()).importCounts(spAdRpList.size())
                    .createTime(LocalDateTime.now()).createId(1004).build();
            iImportOperateRecordService.save(importOperateRecord);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "上传成功";
    }

    @RequestMapping("uploadExcelForSbCampRp")
    public String uploadExcelForSbCampRp(@RequestParam(value = "file") MultipartFile multipartFile, String name) {
        try (ExcelReader excelReader = ExcelUtil.getReader(multipartFile.getInputStream())) {
            List<SbCampRp> sbCampRpList = excelReader.read(0, 1, SbCampRp.class);

            SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
            SbCampRpMapper mapper = sqlSession.getMapper(SbCampRpMapper.class);
            // 每批次导入的最大数据
            int batchCount = 5000;
            // 每批最后一条数据下标等于批量大小
            int batchLastIndex = batchCount;
            // 批量插入   index 为 下标
            for (int index = 0; index < sbCampRpList.size(); ) {
                // 如果读取的数量大小 小于 批量大小
                if (sbCampRpList.size() < batchLastIndex) {
                    batchLastIndex = sbCampRpList.size();
                    mapper.batchInsert(sbCampRpList.subList(index, batchLastIndex));
                    // 清除缓存
                    sqlSession.clearCache();
                    break;
                } else {
                    mapper.batchInsert(sbCampRpList.subList(index, batchLastIndex));
                    // 清除缓存 防止溢出
                    sqlSession.clearCache();
                    index = batchLastIndex;
                    batchLastIndex = index + (batchCount - 1);
                }
            }
            // 将数据提交到数据库，否则的话只是执行，但是并没有提交数据到数据库
            sqlSession.commit();
            // 关闭
            sqlSession.close();

            ImportOperateRecord importOperateRecord = ImportOperateRecord.builder()
                    .targetTable(SbCampRp.class.getAnnotation(TableName.class).value()).importCounts(sbCampRpList.size())
                    .createTime(LocalDateTime.now()).createId(1004).build();
            iImportOperateRecordService.save(importOperateRecord);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "上传成功";
    }

    @RequestMapping("uploadExcelForBusinessReport")
    public String uploadExcelForBusinessReport(@RequestParam(value = "file") MultipartFile multipartFile, String name) {
        try (ExcelReader excelReader = ExcelUtil.getReader(multipartFile.getInputStream())) {
            List<BusinessReport> businessReportList = excelReader.read(0, 1, BusinessReport.class);

            SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
            BusinessReportMapper mapper = sqlSession.getMapper(BusinessReportMapper.class);
            // 每批次导入的最大数据
            int batchCount = 5000;
            // 每批最后一条数据下标等于批量大小
            int batchLastIndex = batchCount;
            // 批量插入   index 为 下标
            for (int index = 0; index < businessReportList.size(); ) {
                // 如果读取的数量大小 小于 批量大小
                if (businessReportList.size() < batchLastIndex) {
                    batchLastIndex = businessReportList.size();
                    mapper.batchInsert(businessReportList.subList(index, batchLastIndex));
                    // 清除缓存
                    sqlSession.clearCache();
                    break;
                } else {
                    mapper.batchInsert(businessReportList.subList(index, batchLastIndex));
                    // 清除缓存 防止溢出
                    sqlSession.clearCache();
                    index = batchLastIndex;
                    batchLastIndex = index + (batchCount - 1);
                }
            }
            // 将数据提交到数据库，否则的话只是执行，但是并没有提交数据到数据库
            sqlSession.commit();
            // 关闭
            sqlSession.close();

            ImportOperateRecord importOperateRecord = ImportOperateRecord.builder()
                    .targetTable(BusinessReport.class.getAnnotation(TableName.class).value()).importCounts(businessReportList.size())
                    .createTime(LocalDateTime.now()).createId(1004).build();
            iImportOperateRecordService.save(importOperateRecord);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "上传成功";
    }

    @RequestMapping("uploadExcelForSbInfo")
    public String uploadExcelForSbInfo(@RequestParam(value = "file") MultipartFile multipartFile, String name) {
        try (ExcelReader excelReader = ExcelUtil.getReader(multipartFile.getInputStream())) {
            List<SbInfo> sbInfoList = excelReader.read(0, 1, SbInfo.class);

            SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
            SbInfoMapper mapper = sqlSession.getMapper(SbInfoMapper.class);
            // 每批次导入的最大数据
            int batchCount = 5000;
            // 每批最后一条数据下标等于批量大小
            int batchLastIndex = batchCount;
            // 批量插入   index 为 下标
            for (int index = 0; index < sbInfoList.size(); ) {
                // 如果读取的数量大小 小于 批量大小
                if (sbInfoList.size() < batchLastIndex) {
                    batchLastIndex = sbInfoList.size();
                    mapper.batchInsert(sbInfoList.subList(index, batchLastIndex));
                    // 清除缓存
                    sqlSession.clearCache();
                    break;
                } else {
                    mapper.batchInsert(sbInfoList.subList(index, batchLastIndex));
                    // 清除缓存 防止溢出
                    sqlSession.clearCache();
                    index = batchLastIndex;
                    batchLastIndex = index + (batchCount - 1);
                }
            }
            // 将数据提交到数据库，否则的话只是执行，但是并没有提交数据到数据库
            sqlSession.commit();
            // 关闭
            sqlSession.close();

            ImportOperateRecord importOperateRecord = ImportOperateRecord.builder()
                    .targetTable(SbInfo.class.getAnnotation(TableName.class).value()).importCounts(sbInfoList.size())
                    .createTime(LocalDateTime.now()).createId(1004).build();
            iImportOperateRecordService.save(importOperateRecord);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "上传成功";
    }

    @RequestMapping("uploadExcelForProductionInfo")
    public String uploadExcelForProductionInfo(@RequestParam(value = "file") MultipartFile multipartFile, String name) {
        try (ExcelReader excelReader = ExcelUtil.getReader(multipartFile.getInputStream())) {
            List<ProductionInfo> productionInfoList = excelReader.read(0, 1, ProductionInfo.class);

            SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
            ProductionInfoMapper mapper = sqlSession.getMapper(ProductionInfoMapper.class);
            // 每批次导入的最大数据
            int batchCount = 5000;
            // 每批最后一条数据下标等于批量大小
            int batchLastIndex = batchCount;
            // 批量插入   index 为 下标
            for (int index = 0; index < productionInfoList.size(); ) {
                // 如果读取的数量大小 小于 批量大小
                if (productionInfoList.size() < batchLastIndex) {
                    batchLastIndex = productionInfoList.size();
                    mapper.batchInsert(productionInfoList.subList(index, batchLastIndex));
                    // 清除缓存
                    sqlSession.clearCache();
                    break;
                } else {
                    mapper.batchInsert(productionInfoList.subList(index, batchLastIndex));
                    // 清除缓存 防止溢出
                    sqlSession.clearCache();
                    index = batchLastIndex;
                    batchLastIndex = index + (batchCount - 1);
                }
            }
            // 将数据提交到数据库，否则的话只是执行，但是并没有提交数据到数据库
            sqlSession.commit();
            // 关闭
            sqlSession.close();

            ImportOperateRecord importOperateRecord = ImportOperateRecord.builder()
                    .targetTable(ProductionInfo.class.getAnnotation(TableName.class).value()).importCounts(productionInfoList.size())
                    .createTime(LocalDateTime.now()).createId(1004).build();
            iImportOperateRecordService.save(importOperateRecord);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "上传成功";
    }
}

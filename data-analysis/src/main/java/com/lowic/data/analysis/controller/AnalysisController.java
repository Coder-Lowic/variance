package com.lowic.data.analysis.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lowic.data.analysis.entity.ImportOperateRecord;
import com.lowic.data.analysis.entity.SdAdRp;
import com.lowic.data.analysis.mapper.SdAdRpMapper;
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

    @RequestMapping("uploadExcel")
    public String uploadExcel(@RequestParam(value = "file") MultipartFile multipartFile, String name) {
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

            ImportOperateRecord importOperateRecord = ImportOperateRecord.builder()
                    .targetTable(SdAdRp.class.getAnnotation(TableName.class).value()).importCounts(sdAdRpList.size())
                    .createTime(LocalDateTime.now()).createId(1004).build();
            iImportOperateRecordService.save(importOperateRecord);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "上传成功";
    }
}

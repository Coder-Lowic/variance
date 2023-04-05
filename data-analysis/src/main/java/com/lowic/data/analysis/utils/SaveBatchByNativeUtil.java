package com.lowic.data.analysis.utils;

import com.lowic.data.analysis.mapper.SaveBatchExtensionMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.List;

/**
 * @author Lowic
 */
@Log4j2
public class SaveBatchByNativeUtil {

    public static void saveBatchByNative(SqlSessionTemplate sqlSessionTemplate, List<?> list,
                                         Class<? extends SaveBatchExtensionMapper<?>> clazz) {
        if (list.isEmpty()) {
            return;
        }
        try {
            SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
            SaveBatchExtensionMapper<?> mapper = sqlSession.getMapper(clazz);
            // 每批次导入的最大数据
            int batchCount = 5000;
            // 每批最后一条数据下标等于批量大小
            int batchLastIndex = batchCount;
            // 批量插入 index 为下标
            for (int index = 0; index < list.size(); ) {
                if (list.size() < batchLastIndex) {
                    batchLastIndex = list.size();
                    mapper.saveBatchByNative(list.subList(index, batchLastIndex));
                    // 清除缓存
                    sqlSession.clearCache();
                    break;
                } else {
                    mapper.saveBatchByNative(list.subList(index, batchLastIndex));
                    // 清除缓存 防止溢出
                    sqlSession.clearCache();
                    index = batchLastIndex;
                    batchLastIndex = index + (batchCount - 1);
                }
            }
            sqlSession.commit();
            sqlSession.close();
        } catch (Exception e) {
            log.atInfo().log("Batch import excel by native error. Error message is : {}.",
                    e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

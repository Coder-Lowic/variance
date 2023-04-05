package com.lowic.data.analysis.mapper;

import java.util.List;

/**
 * @author Lowic
 */
public interface SaveBatchExtensionMapper<T> {
    /**
     * 利用原生SqlSession进行批量保存
     *
     * @param list 需要插入的源list
     */
    void saveBatchByNative(List<?> list);
}

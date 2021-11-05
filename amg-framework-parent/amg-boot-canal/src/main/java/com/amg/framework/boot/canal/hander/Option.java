package com.amg.framework.boot.canal.hander;

import com.amg.framework.boot.canal.entity.Canal;

/**
 * @author lyc
 * @date 2020/9/27 16:34
 * @describe
 */
public interface Option {

    /**
     * 删除之前
     * @param canal
     */
    void deleteBefore(Canal canal);

    /**
     * 更新之前
     * @param canal
     */
    void updateBefore(Canal canal);

    /**
     * 更新之后
     * @param canal
     */
    void updateAfter(Canal canal);

    /**
     * 插入之后
     * @param canal
     */
    void insertAfter(Canal canal);
}

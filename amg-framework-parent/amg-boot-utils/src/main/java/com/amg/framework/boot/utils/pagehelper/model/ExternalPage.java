package com.amg.framework.boot.utils.pagehelper.model;

import java.io.Serializable;
import java.util.List;


/**
 * 外部系统分页信息
 */
public final class ExternalPage<T> implements Serializable {

    private static final long serialVersionUID = 5206417242303858908L;

    private int currentPage; // 当前页

    private long totalCount; // 总条数

    private List<T> list; // 数据集

    public ExternalPage(Page page) {
        this.currentPage = page.getPageNum();
        this.totalCount = page.getTotal();
        this.list = page.getList();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

}

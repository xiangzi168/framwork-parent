package com.amg.framework.boot.utils.pagehelper.model;


import com.amg.framework.boot.utils.bean.BeanCopierUtils;
import org.springframework.cglib.core.Converter;

import java.io.Serializable;
import java.util.List;


/**
 * 内部分页信息
 * */
public final class Page<T> implements Serializable {

	private static final long serialVersionUID = 6294980014540127840L;

	private List list;

	private transient boolean isExternalPage;

	private PageInfo<T> pageInfo;

	private Page() {}

	public Page(PageInfo<T> pageInfo) {
		this.pageInfo = pageInfo;
	}

	public Page(List<T> list) {
		this(list, false);
	}

	public Page(List<T> list, Integer pageNum, Integer pageSize) {
		this(list, pageNum, pageSize, null, null, null, false);
	}

	public Page(List<T> list, Integer pageNum, Integer pageSize, Long count) {
		this(list, pageNum, pageSize, count, null, null, false);
	}

	public Page(List<T> list, Integer pageNum,Integer pageSize, Long count, boolean isExternalPage) {
		this(list, pageNum, pageSize, count, null, null, isExternalPage);
	}

	public Page(List<?> list, Integer pageNum,Integer pageSize, Long count, Class<T> tClass) {
		this(list, pageNum,pageSize, count, tClass, null, false);
	}

	public Page(List<?> list, Integer pageNum,Integer pageSize, Long count, Class<T> tClass, boolean isExternalPage) {
		this(list, pageNum,pageSize, count, tClass, null, isExternalPage);
	}

	public Page(List<T> list, boolean isExternalPage) {
		this(list, null, null, null, null,  isExternalPage);
	}

	public Page(List<?> list, Class<T> tClass, boolean isExternalPage) {
		this(list, null,null, null, tClass, null, isExternalPage);
	}

	public Page(List<?> list, Class<T> tClass, Converter converter) {
		this(list, null,null, null, tClass, converter,false);
	}

	public Page(List<?> list, Class<T> tClass) {
		this(list, tClass, false);
	}

	/**
	 *
	 * @param list 数据集
	 * @param pageNum 当前页码（针对非 mysql 分页使用此参数）
	 * @param count 总条数（针对非 mysql 分页使用此参数）
	 * @param tClass 待转换对象类
	 * @param converter 对象属性转换器
	 * @param isExternalPage 是否为外部系统分页
	 */
	public Page(List list, Integer pageNum, Integer pageSize, Long count, Class<T> tClass, Converter converter, boolean isExternalPage) {
		if (pageNum != null && count != null) {
			Pager pager = new Pager(pageNum, pageSize);
			pager.setTotal(count);
			pager.addAll(list);
			this.pageInfo = new PageInfo(pager);
		} else {
			this.pageInfo = new PageInfo(list);
		}
		if (tClass != null)
			this.pageInfo.setList(BeanCopierUtils.copyObjects(list, tClass, converter));
		this.isExternalPage = isExternalPage;
	}

	public List<T> getList() {
		return pageInfo.getList();
	}

	public void setList(List list) {
		pageInfo.setList(list);
	}

	public boolean isExternalPage() {
		return isExternalPage;
	}

	public void setExternalPage(boolean externalPage) {
		isExternalPage = externalPage;
	}

	public boolean isHasNextPage() {
		return pageInfo.isHasNextPage();
	}

	public boolean isHasPreviousPage() {
		return pageInfo.isHasPreviousPage();
	}

	public boolean isIsFirstPage() {
		return pageInfo.isIsFirstPage();
	}

	public boolean isIsLastPage() {
		return pageInfo.isIsLastPage();
	}

	public int getNextPage() {
		return pageInfo.getNextPage();
	}

	public int getPrePage() {
		return pageInfo.getPrePage();
	}

	public int getPageNum() {
		return pageInfo.getPageNum();
	}

	public int getPageSize() {
		return pageInfo.getPageSize();
	}

	public int getPages() {
		return pageInfo.getPages();
	}

	public long getTotal() {
		return pageInfo.getTotal();
	}
}

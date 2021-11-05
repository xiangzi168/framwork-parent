package com.amg.framework.boot.mongodb.base.service;

import com.amg.framework.boot.mongodb.model.UpdateBatchModel;
import com.amg.framework.boot.utils.pagehelper.model.Page;
import com.mongodb.bulk.BulkWriteResult;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.IndexOperationsProvider;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


/**
 * MongoDB基础服务类
 * */
public interface BaseService<T> extends MongoOperations, IndexOperationsProvider {

	/**
	 * 查询分页
	 * */
	Page<T> page(int page, int row, Query query);

	public Page page(int page, int row, Query query, Class<?> tclass);

	public Page page(int page, int row);

	public Page page(int page, int row, Class<?> tclass);

	/**
	 *
	 * @param page
	 * @param row
	 * @param tclass
	 * @param isExternalPage 是否为外部分页
	 * @return
	 */
	public Page page(int page, int row, Class<?> tclass, boolean isExternalPage);

	/**
	 *
	 * @param page
	 * @param row
	 * @param sourceClass 源对象
	 * @param targetClass 待转换目标对象
	 * @return
	 */
	public Page page(int page, int row, Class<?> sourceClass, Class<?> targetClass);

	public Page page(int page, int row, Query query, Class<?> tclass, Class<?> targetClass);

	public Page page(int page, int row, Class<?> sourceClass, Class<?> targetClass, boolean isExternalPage);

	public BulkWriteResult updateBatch(List<UpdateBatchModel> updateBatchModels);

	public BulkWriteResult updateBatch(List<UpdateBatchModel> updateBatchModels, String collectionName);

	public BulkWriteResult updateBatch(List<UpdateBatchModel> updateBatchModels, Class tclass);

}

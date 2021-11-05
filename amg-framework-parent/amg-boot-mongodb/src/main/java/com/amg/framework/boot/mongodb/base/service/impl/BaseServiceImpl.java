package com.amg.framework.boot.mongodb.base.service.impl;

import com.amg.framework.boot.mongodb.base.service.BaseService;
import com.amg.framework.boot.mongodb.model.UpdateBatchModel;
import com.amg.framework.boot.utils.pagehelper.model.Page;
import com.mongodb.bulk.BulkWriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.support.PageableExecutionUtils;
import java.lang.reflect.ParameterizedType;
import java.util.List;


/**
 * MongoDB基础服务实现类
 * */
@SuppressWarnings("unchecked")
public abstract class BaseServiceImpl<T> extends MongoTemplate implements BaseService<T> {

	private static Logger logger = LoggerFactory.getLogger(BaseServiceImpl.class);

	@Override
	public Page<T> page(int page, int row, Query query) {
		Pageable pageable = PageRequest.of(page - 1 , row);
		Class<T> tClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		List<T> list = find(query.with(pageable), tClass);
		org.springframework.data.domain.Page<T> result =  PageableExecutionUtils.getPage(list, pageable, () -> count(query, tClass));
		return new Page(result.getContent(), page, row, result.getTotalElements());
	}

	@Override
	public Page page(int page, int row, Query query, Class<?> tclass) {
		Pageable pageable = PageRequest.of(page - 1, row);
		List<?> list = find(query.with(pageable), tclass);
		org.springframework.data.domain.Page<?> result =  PageableExecutionUtils.getPage(list, pageable, () -> count(query, tclass));
		return new Page(result.getContent(), page, row, result.getTotalElements());
	}

	@Override
	public Page page(int page, int row, Query query, Class<?> sourceClass, Class<?> targetClass) {
		Pageable pageable = PageRequest.of(page - 1, row);
		List<?> list = find(query.with(pageable), sourceClass);
		org.springframework.data.domain.Page<?> result =  PageableExecutionUtils.getPage(list, pageable, () -> count(query, sourceClass));
		return new Page(result.getContent(), page, row, result.getTotalElements(), targetClass);
	}

	@Override
	public Page page(int page, int row) {
		Pageable pageable = PageRequest.of(page - 1, row);
		Class<T> tclass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Query query = new Query();
		List<T> list = find(query.with(pageable), tclass);
		org.springframework.data.domain.Page<T> result =  PageableExecutionUtils.getPage(list, pageable, () -> count(query, tclass));
		return new Page(result.getContent(), page, row, result.getTotalElements());
	}

	@Override
	public Page page(int page, int row, Class<?> tclass) {
		Pageable pageable = PageRequest.of(page - 1, row);
		Query query = new Query();
		List<?> list = find(query.with(pageable), tclass);
		org.springframework.data.domain.Page<?> result =  PageableExecutionUtils.getPage(list, pageable, () -> count(query, tclass));
		return new Page(result.getContent(), page, row, result.getTotalElements());
	}

	@Override
	public Page page(int page, int row, Class<?> tclass, boolean isExternalPage) {
		Pageable pageable = PageRequest.of(page - 1, row);
		Query query = new Query();
		List<?> list = find(query.with(pageable), tclass);
		org.springframework.data.domain.Page<?> result =  PageableExecutionUtils.getPage(list, pageable, () -> count(query, tclass));
		return new Page(result.getContent(), page, row, result.getTotalElements(), isExternalPage);
	}

	@Override
	public Page page(int page, int row, Class<?> sourceClass, Class<?> targetClass) {
		Pageable pageable = PageRequest.of(page - 1, row);
		Query query = new Query();
		List<?> list = find(query.with(pageable), sourceClass);
		org.springframework.data.domain.Page<?> result =  PageableExecutionUtils.getPage(list, pageable, () -> count(query, sourceClass));
		return new Page(result.getContent(), page, row, result.getTotalElements(), targetClass);
	}

	@Override
	public Page page(int page, int row, Class<?> sourceClass, Class<?> targetClass, boolean isExternalPage) {
		Pageable pageable = PageRequest.of(page - 1, row);
		Query query = new Query();
		List<?> list = find(query.with(pageable), sourceClass);
		org.springframework.data.domain.Page<?> result =  PageableExecutionUtils.getPage(list, pageable, () -> count(query, sourceClass));
		return new Page(result.getContent(), page, row, result.getTotalElements(), targetClass, isExternalPage);
	}

	@Override
	public BulkWriteResult updateBatch(List<UpdateBatchModel> updateBatchModels) {
		try {
			Class<T> tclass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			BulkOperations ops = this.bulkOps(BulkOperations.BulkMode.UNORDERED, tclass);
			for (UpdateBatchModel updateBatchModel : updateBatchModels) {
				ops.updateOne(updateBatchModel.getQuery(), updateBatchModel.getUpdate());
			}
			return ops.execute();
		} catch (Exception e) {
			logger.error("updateBatch error", e);
		}
		return null;
	}

	@Override
	public BulkWriteResult updateBatch(List<UpdateBatchModel> updateBatchModels, String collectionName) {
		try {
			BulkOperations ops = this.bulkOps(BulkOperations.BulkMode.UNORDERED, collectionName);
			for (UpdateBatchModel updateBatchModel : updateBatchModels) {
				ops.updateOne(updateBatchModel.getQuery(), updateBatchModel.getUpdate());
			}
			return ops.execute();
		} catch (Exception e) {
			logger.error("updateBatch error", e);
		}
		return null;
	}

	@Override
	public BulkWriteResult updateBatch(List<UpdateBatchModel> updateBatchModels, Class tclass) {
		try {
			BulkOperations ops = this.bulkOps(BulkOperations.BulkMode.UNORDERED, tclass);
			for (UpdateBatchModel updateBatchModel : updateBatchModels) {
				ops.updateOne(updateBatchModel.getQuery(), updateBatchModel.getUpdate());
			}
			return ops.execute();
		} catch (Exception e) {
			logger.error("updateBatch error", e);
		}
		return null;
	}

}

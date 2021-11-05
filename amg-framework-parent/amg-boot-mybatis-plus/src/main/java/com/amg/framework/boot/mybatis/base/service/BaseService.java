package com.amg.framework.boot.mybatis.base.service;


import com.amg.framework.boot.utils.pagehelper.model.Page;

/**
 * 基础服务类
 * */
public interface BaseService<T> {

	int deleteByPrimaryKey(Long primaryKey);

	int insert(T t);

	int insertSelective(T t);

	T selectByPrimaryKey(Long primaryKey);

	T selectOne(T t);

	int updateByPrimaryKeySelective(T t);

	int updateByPrimaryKey(T t);
	
	/**
	 * 查询分页
	 * */
	Page<T> page(T t);
	
}

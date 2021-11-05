package com.amg.framework.boot.mybatis.base.service.impl;

import com.amg.framework.boot.mybatis.base.mapper.BaseMapper;
import com.amg.framework.boot.mybatis.base.service.BaseService;
import com.amg.framework.boot.utils.pagehelper.model.Page;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.List;


/**
 * 基础服务实现类
 * */
public abstract class BaseServiceImpl<T> implements BaseService<T> {

	@Autowired
	private BaseMapper<T> baseMapper;


	@Override
	public int deleteByPrimaryKey(Long primaryKey) {
		return baseMapper.deleteByPrimaryKey(primaryKey);
	}

	@Override
	public int insert(T t) {
		return baseMapper.insert(t);
	}

	@Override
	public int insertSelective(T t) {
		return baseMapper.insertSelective(t);
	}

	@Override
	public T selectByPrimaryKey(Long primaryKey) {
		return baseMapper.selectByPrimaryKey(primaryKey);
	}

	@Override
	public T selectOne(T t) {
		List<T> list = baseMapper.page(t);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		if (list.size() > 1) {
			throw new TooManyResultsException("Expected one result (or null) to be returned by " +
					"selectOne(), but found: " + list.size());
		}
		return list.get(0);
	}

	@Override
	public int updateByPrimaryKeySelective(T t) {
		try {
			Class cl = t.getClass();
			//获取id
			Field f = cl.getDeclaredField("id");
			f.setAccessible(true);
			Long id = (Long)f.get(t);
			//获取版本号version
			Field f1 = cl.getDeclaredField("version");
			f1.setAccessible(true);
			T old = selectByPrimaryKey(id);
			if (old == null) return 0;
			Class c2 = old.getClass();
			Field f2 = c2.getDeclaredField("version");
			f2.setAccessible(true);
			Long version = (Long)f2.get(old);
			f1.set(t, version);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return baseMapper.updateByPrimaryKeySelective(t);
	}

	@Override
	public int updateByPrimaryKey(T t) {
		try {
			Class cl = t.getClass();
			//获取id
			Field f = cl.getDeclaredField("id");
			f.setAccessible(true);
			Long id = (Long)f.get(t);
			//获取版本号version
			Field f1 = cl.getDeclaredField("version");
			f1.setAccessible(true);
			T old = selectByPrimaryKey(id);
			if (old == null) return 0;
			Class c2 = old.getClass();
			Field f2 = c2.getDeclaredField("version");
			f2.setAccessible(true);
			Long version = (Long)f2.get(old);
			f1.set(t, version);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return baseMapper.updateByPrimaryKey(t);
	}

	@Override
	public Page<T> page(T t) {
		List<T> list = baseMapper.page(t);
		return new Page(list);
	}

}

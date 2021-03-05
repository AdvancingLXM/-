package com.pinyougou.service;

import com.pinyougou.vo.PageResult;

import java.io.Serializable;
import java.util.List;

public interface BaseService<T> {

    //根据主键查询；可以接收各种类型的主键，等效object
    T findOne(Serializable id);

    //查询所有
    List<T> findAll();

    //根据条件查询；属性值不为null则为查询条件
    List<T> findByWhere(T t);

    //根据分页查询
    PageResult findPage(Integer page, Integer rows);

    //根据条件分页查询
    PageResult findPage(Integer page, Integer rows, T t);

    //新增
    void add(T t);

    //更新
    void update(T t);

    //批量删除
    void deleteByIds(Serializable[] ids);
}

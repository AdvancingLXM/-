package com.pinyougou.sellergoods.service;


import com.pinyougou.pojo.TbBrand;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService extends BaseService<TbBrand> {
    /**
     * 查询所有品牌列表
     * @return 品牌列表
     */
    List<TbBrand> queryAll();

    /**
     * 根据分页页号、页大小分页查询品牌列表
     * @param page 页号
     * @param rows 页大小
     * @return 品牌列表
     */
    List<TbBrand> testPage(Integer page, Integer rows);

    /**
     * 根据条件分页模糊查询品牌数据
     * @param brand 查询条件
     * @param page 页号
     * @param rows 页大小
     * @return 分页对象
     */
    PageResult search(TbBrand brand, Integer page, Integer rows);

    /**
     * 构建select2的品牌下拉框的数据；数据结构形如：[{id:'1',text:'联想'},{id:'2',text:'华为'}]
     * @return 品牌数据
     */
    List<Map<String, String>> selectOptionList();
}

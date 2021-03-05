package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService extends BaseService<TbSpecification> {

    PageResult search(Integer page, Integer rows, TbSpecification specification);

    /**
     * 保存规格及其对应的规格选项到数据库中
     * @param specification 规格及其对应的规格选项
     */
    void add(Specification specification);

    /**
     * 根据规格id查询规格及其选项
     * @param id 规格id
     * @return 规格及其选项
     */
    Specification findOne(Long id);

    /**
     * 保存规格及其对应的规格选项到数据库中
     * @param specification 规格及其对应的规格选项
     */
    void update(Specification specification);

    /**
     * 删除规格及其选项
     * @param ids 规格id集合
     */
    void deleteSpecificationByIds(Long[] ids);

    List<Map<String, String>> selectOptionList();

}
package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SpecificationMapper;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.sellergoods.service.SpecificationService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SpecificationService.class)
@Transactional
public class SpecificationServiceImpl extends BaseServiceImpl<TbSpecification> implements SpecificationService {

    @Autowired
    private SpecificationMapper specificationMapper;

    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbSpecification specification) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSpecification.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(specification.getSpecName())){
            criteria.andLike("specName", "%" + specification.getSpecName() + "%");
        }

        List<TbSpecification> list = specificationMapper.selectByExample(example);
        PageInfo<TbSpecification> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void add(Specification specification) {
        //1、保存规格；通用Mapper会对实体类的主键进行回填
        add(specification.getSpecification());

        //2、保存规格选项
        if (specification.getSpecificationOptionList() != null && specification.getSpecificationOptionList().size() > 0) {
            for (TbSpecificationOption tbSpecificationOption : specification.getSpecificationOptionList()) {

                tbSpecificationOption.setSpecId(specification.getSpecification().getId());

                specificationOptionMapper.insertSelective(tbSpecificationOption);
            }
        }

    }

    @Override
    public Specification findOne(Long id) {
        Specification specification = new Specification();

        //1、查询规格 select * from tb_specification where id=?
        specification.setSpecification(specificationMapper.selectByPrimaryKey(id));

        //2、查询规格对应的选项列表 select * from tb_specification_option where spec_id=?
        TbSpecificationOption param = new TbSpecificationOption();
        param.setSpecId(id);
        List<TbSpecificationOption> specificationOptionList = specificationOptionMapper.select(param);

        specification.setSpecificationOptionList(specificationOptionList);

        return specification;
    }

    @Override
    public void update(Specification specification) {
        //1、更新规格；通用Mapper会对实体类的主键进行回填
        update(specification.getSpecification());

        //2、删除该规格对应的原有在数据库中的那些规格选项
        TbSpecificationOption param = new TbSpecificationOption();
        param.setSpecId(specification.getSpecification().getId());
        specificationOptionMapper.delete(param);

        //3、保存规格选项
        if (specification.getSpecificationOptionList() != null && specification.getSpecificationOptionList().size() > 0) {
            for (TbSpecificationOption tbSpecificationOption : specification.getSpecificationOptionList()) {

                tbSpecificationOption.setSpecId(specification.getSpecification().getId());

                specificationOptionMapper.insertSelective(tbSpecificationOption);
            }
        }
    }

    @Override
    public void deleteSpecificationByIds(Long[] ids) {
        //1、删除规格
        deleteByIds(ids);

        //2、删除规格选项； delete from tb_specifiction_option where spec_id in(?,?...)
        Example example = new Example(TbSpecificationOption.class);

        //创建查询条件对象
        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("specId", Arrays.asList(ids));
        specificationOptionMapper.deleteByExample(example);
    }

    @Override
    public List<Map<String, String>> selectOptionList() {
        return specificationMapper.selectOptionList();
    }
}

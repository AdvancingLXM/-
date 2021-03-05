package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Service(interfaceClass = ContentService.class)
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {

    //广告内容列表在redis中的key的名称
    private static final String REDIS_CONTENT_KEY = "CONTENT_LIST";
    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void add(TbContent tbContent) {
        super.add(tbContent);
        //更新缓存数据
        updateContentInRedisByCategoryId(tbContent.getCategoryId());
    }

    /**
     * 根据内容分类id删除在redis中的内容列表
     * @param categoryId 内容分类id
     */
    private void updateContentInRedisByCategoryId(Long categoryId) {
        redisTemplate.boundHashOps(REDIS_CONTENT_KEY).delete(categoryId);
    }

    @Override
    public void update(TbContent tbContent) {
        //要将内容对应的原来的内容分类的缓存数据删除；当下最新的内容对应的内容分类的缓存数据也需要删除
        TbContent oldContent = findOne(tbContent.getId());

        super.update(tbContent);

        if (!oldContent.getCategoryId().equals(tbContent.getCategoryId())) {
            //要将内容对应的原来的内容分类的缓存数据删除
            updateContentInRedisByCategoryId(oldContent.getCategoryId());
        }

        //更新缓存数据
        updateContentInRedisByCategoryId(tbContent.getCategoryId());
    }

    @Override
    public void deleteByIds(Serializable[] ids) {

        //需要更新这些内容（正要删除的内容id对应的内容）对应的那些内容分类id（刚刚删除的每一个内容对应的内容分类）所以缓存的数据
        //内容id集合 获取 这些内容id对应的所有内容分类id
        //select catagory_id from tb_content where id in(?,?)

        Example example = new Example(TbContent.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));

        List<TbContent> contentList = contentMapper.selectByExample(example);
        for (TbContent content : contentList) {
            updateContentInRedisByCategoryId(content.getCategoryId());
        }

        super.deleteByIds(ids);

    }

    @Override
    public PageResult search(Integer page, Integer rows, TbContent content) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(content.get***())){
            criteria.andLike("***", "%" + content.get***() + "%");
        }*/

        List<TbContent> list = contentMapper.selectByExample(example);
        PageInfo<TbContent> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public List<TbContent> findContentListByCategoryId(Long categoryId) {
        //根据内容分类id查询该分类下的所有有效的内容并且排序字段降序排序
        List<TbContent> contentList = null;

        try {
            //从redis中查询内容列表数据；如果存在则直接返回
            contentList = (List<TbContent>) redisTemplate.boundHashOps(REDIS_CONTENT_KEY).get(categoryId);
            if (contentList != null) {
                return contentList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Example example = new Example(TbContent.class);

        example.createCriteria()
                .andEqualTo("categoryId", categoryId)//内容分类id
                .andEqualTo("status", "1");//有效

        //根据排序字段降序排序
        example.orderBy("sortOrder").desc();

        contentList = contentMapper.selectByExample(example);

        try {
            //将内容列表存入到redis中
            redisTemplate.boundHashOps(REDIS_CONTENT_KEY).put(categoryId, contentList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contentList;
    }
}

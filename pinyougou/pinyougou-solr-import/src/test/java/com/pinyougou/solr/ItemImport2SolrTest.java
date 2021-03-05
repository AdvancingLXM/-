package com.pinyougou.solr;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext-*.xml")
public class ItemImport2SolrTest {
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private ItemMapper itemMapper;

    @Test
    public void test(){
        //1、查询：查询商品sku已经审核的数据
        TbItem param = new TbItem();
        param.setStatus("1");

        List<TbItem> itemList = itemMapper.select(param);

        for (TbItem item : itemList) {
            Map map = JSON.parseObject(item.getSpec(), Map.class);

            item.setSpecMap(map);

        }

        //2、导入
        solrTemplate.saveBeans(itemList);

        solrTemplate.commit();
    }
}

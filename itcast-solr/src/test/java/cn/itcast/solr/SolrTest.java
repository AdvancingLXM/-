package cn.itcast.solr;

import com.pinyougou.pojo.TbItem;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-solr.xml")
public class SolrTest {

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 多条件查询
     */
    @Test
    public void testMultiQuery(){

        //创建一个查询对象
        SimpleQuery query = new SimpleQuery();

        //创建一个查询条件对象；如果使用contains则不再会对搜索的句子进行分词
        Criteria criteria = new Criteria("item_title").contains("金色");
        query.addCriteria(criteria);

        Criteria itemPrice = new Criteria("item_price").greaterThan(90000);
        query.addCriteria(itemPrice);

        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);

        showPage(scoredPage);
    }

    /**
     * 查询
     */
    @Test
    public void testQuery(){

        //创建一个查询对象
        SimpleQuery query = new SimpleQuery();

        //创建一个查询条件对象；如果使用contains则不再会对搜索的句子进行分词
        Criteria criteria = new Criteria("item_title").contains("金色");
        query.addCriteria(criteria);

        //如果查询第3页，每页10条；起始索引号为：（3-1）*10 --》(页号-1)*页大小


        //起始索引号
        query.setOffset(0);
        //页大小
        query.setRows(10);

        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);

        showPage(scoredPage);
    }

    private void showPage(ScoredPage<TbItem> scoredPage) {
        System.out.println("总记录数为：" + scoredPage.getTotalElements());
        System.out.println("总页数为：" + scoredPage.getTotalPages());

        //输出每一个查询内容
        for (TbItem item : scoredPage.getContent()) {
            System.out.println("id为:" + item.getId());
            System.out.println("标题为:" + item.getTitle());
            System.out.println("价格为:" + item.getPrice());
            System.out.println("图片为:" + item.getImage());
        }
    }

    /**
     * 根据条件删除
     */
    @Test
    public void deleteByQuery(){

        //创建一个查询对象
        SimpleQuery simpleQuery = new SimpleQuery("item_title:apple");

        solrTemplate.delete(simpleQuery);

        //提交
        solrTemplate.commit();
    }

    /**
     * 根据id删除
     */
    @Test
    public void deleteById(){

        solrTemplate.deleteById("100000287145");

        //提交
        solrTemplate.commit();
    }

    /**
     * 测试新增、更新
     */
    @Test
    public void addOrUpdate(){

        //如果不是使用注解的话
       /* SolrInputDocument inputDocument = new SolrInputDocument();
        //域名（必须要在schema.xml中配置的），域值
        inputDocument.addField("item_title", "test");
        solrTemplate.saveDocument(inputDocument);*/

        TbItem item = new TbItem();
        item.setId(100000287145L);
        item.setTitle("Apple iPhone XS Max (A2104) 64GB 金色 移动联通电信4G手机 双卡双待");
        item.setPrice(new BigDecimal(9599));
        item.setImage("https://item.jd.com/100000287145.html");

        solrTemplate.saveBean(item);

        //提交
        solrTemplate.commit();
    }
}

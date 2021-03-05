package com.pinyougou.search.activemq.listener;

import com.alibaba.fastjson.JSONArray;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.*;
import java.util.Arrays;
import java.util.List;

/**
 * 接收来自ActiveMQ的消息（spu id集合），利用搜索服务对象的删除方法删除solr中商品数据
 */
public class ItemDeleteMessageListener extends AbstractAdaptableMessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        //1、接收消息
        ObjectMessage objectMessage = (ObjectMessage) message;
        //获取商品spu id数组
        Long[] ids = (Long[]) objectMessage.getObject();

        //2、删除搜索数据
        itemSearchService.deleteItemByGoodsIds(Arrays.asList(ids));
    }
}

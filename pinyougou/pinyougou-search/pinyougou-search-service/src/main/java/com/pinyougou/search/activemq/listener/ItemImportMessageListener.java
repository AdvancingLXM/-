package com.pinyougou.search.activemq.listener;

import com.alibaba.fastjson.JSONArray;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.List;

/**
 * 接收来自ActiveMQ的消息（sku列表json格式字符串），利用搜索服务对象的导入方法更新数据到solr中
 */
public class ItemImportMessageListener extends AbstractAdaptableMessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        //1、接收消息
        TextMessage textMessage = (TextMessage)message;
        //将商品sku列表json格式字符串转换成列表
        List<TbItem> itemList = JSONArray.parseArray(textMessage.getText(), TbItem.class);

        //2、更新搜索数据
        itemSearchService.importItemList(itemList);
    }
}

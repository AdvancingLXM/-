package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    /**
     * 根据搜索关键字到solr搜索对应的数据并返回
     * @param searchMap 搜索对象
     * @return 搜索结果
     */
    Map<String, Object> search(Map<String, Object> searchMap);

    /**
     * 将商品列表导入到solr中
     * @param itemList 商品列表
     */
    void importItemList(List<TbItem> itemList);

    /**
     * 根据商品spu id集合到solr中删除这些spu id集合对应的sku商品
     * @param goodsIds 商品spu id集合
     */
    void deleteItemByGoodsIds(List<Long> goodsIds);
}

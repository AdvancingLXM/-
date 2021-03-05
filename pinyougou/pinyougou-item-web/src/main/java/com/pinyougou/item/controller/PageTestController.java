package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/test")
@RestController
public class PageTestController {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;

    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;

    /**
     * 批量根据商品spu id生成对应的Html静态页面
     * @param goodsIds 商品spu id集合
     * @return 操作结果
     */
    @GetMapping("/audit")
    public String audit(Long[] goodsIds){
        if (goodsIds != null && goodsIds.length > 0) {
            for (Long goodsId : goodsIds) {
                genHtml(goodsId);
            }
        }
        return "success";
    }


    /**
     * 批量根据商品spu id删除指定路径下的Html静态页面
     * @param goodsIds 商品spu id集合
     * @return 操作结果
     */
    @GetMapping("/delete")
    public String delete(Long[] goodsIds){
        if (goodsIds != null && goodsIds.length > 0) {
            for (Long goodsId : goodsIds) {
                File file = new File(ITEM_HTML_PATH + goodsId + ".html");
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        return "success";
    }


    /**
     * 商品spu id生成对应的Html静态页面到指定路径
     * @param goodsId 商品spu id
     */
    private void genHtml(Long goodsId) {
        try {
            //1、获取模版
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");

            //2、获取商品基本、描述、sku列表、3级商品分类中文名称
            Map<String, Object> dataModel = new HashMap<String, Object>();
            Goods goods = goodsService.findGoodsByIdAndStatus(goodsId, "1");
            //goods 商品spu基本信息
            dataModel.put("goods", goods.getGoods());
            //goodsDesc 商品spu描述信息
            dataModel.put("goodsDesc", goods.getGoodsDesc());
            //itemCat1 商品spu 对应的第1级商品分类中文名称
            TbItemCat itemCat1 = itemCatService.findOne(goods.getGoods().getCategory1Id());
            dataModel.put("itemCat1", itemCat1.getName());
            //itemCat2 商品spu 对应的第2级商品分类中文名称
            TbItemCat itemCat2 = itemCatService.findOne(goods.getGoods().getCategory2Id());
            dataModel.put("itemCat2", itemCat2.getName());
            //itemCat3 商品spu 对应的第3级商品分类中文名称
            TbItemCat itemCat3 = itemCatService.findOne(goods.getGoods().getCategory3Id());
            dataModel.put("itemCat3", itemCat3.getName());
            //itemList 商品sku列表
            dataModel.put("itemList", goods.getItemList());

            //3、输出静态页面到指定路径
            FileWriter fileWriter = new FileWriter(ITEM_HTML_PATH + goodsId + ".html");

            template.process(dataModel, fileWriter);

            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

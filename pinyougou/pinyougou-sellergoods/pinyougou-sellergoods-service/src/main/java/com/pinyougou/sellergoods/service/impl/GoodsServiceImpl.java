package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.SellerService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service(interfaceClass = GoodsService.class)
@Transactional
public class GoodsServiceImpl extends BaseServiceImpl<TbGoods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;
    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private ItemCatMapper itemCatMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbGoods goods) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        //过滤掉删除状态的数据
        criteria.andNotEqualTo("isDelete", "1");

        //根据商家查询
        if(!StringUtils.isEmpty(goods.getSellerId())){
            criteria.andEqualTo("sellerId", goods.getSellerId());
        }
        //根据审核状态查询
        if(!StringUtils.isEmpty(goods.getAuditStatus())){
            criteria.andEqualTo("auditStatus", goods.getAuditStatus());
        }
        //根据商品名称模糊查询
        if(!StringUtils.isEmpty(goods.getGoodsName())){
            criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
        }

        List<TbGoods> list = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void addGoods(Goods goods) {
        //1、保存基本信息
        add(goods.getGoods());

        //int i = 1/0;

        //2、保存描述信息
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());

        goodsDescMapper.insertSelective(goods.getGoodsDesc());

        //3、保存商品sku列表
        saveItemList(goods);

    }

    @Override
    public Goods findGoodsById(Long id) {
        Goods goods = new Goods();

        //1、根据商品id 查询商品基本信息
        goods.setGoods(findOne(id));

        //2、根据商品id 查询商品描述信息
        goods.setGoodsDesc(goodsDescMapper.selectByPrimaryKey(id));

        //3、根据商品id 查询商品sku列表
        TbItem param = new TbItem();
        param.setGoodsId(id);
        List<TbItem> itemList = itemMapper.select(param);
        goods.setItemList(itemList);

        return goods;
    }

    @Override
    public void updateGoods(Goods goods) {
        //1、更新商品基本信息
        update(goods.getGoods());
        //2、更新商品描述信息
        goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());

        //3、删除该商品原有的sku列表
        TbItem param = new TbItem();
        param.setGoodsId(goods.getGoods().getId());
        itemMapper.delete(param);

        //4、保存最新的sku列表
        saveItemList(goods);
    }

    @Override
    public void updateStatus(Long[] ids, String status) {

        //如下的java代码执行的sql语句形如：update tb_goods set audit_status='1' where id in (?,?...)

        TbGoods goods = new TbGoods();
        goods.setAuditStatus(status);

        //创建一个更新的对象
        Example example = new Example(TbGoods.class);
        //创建一个更新条件对象
        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("id", Arrays.asList(ids));

        //参数1：更新的值
        //参数2：更新条件
        goodsMapper.updateByExampleSelective(goods, example);

        //如果是审核通过的话；那么要将该商品对应的所有sku列表的状态修改为已审核
        if ("2".equals(status)) {

            //根据商品spu id 批量修改sku的状态；sql形如：update tb_item set status='1' where goods_id in(?,?...)
            Example itemExample = new Example(TbItem.class);
            Example.Criteria criteria1 = itemExample.createCriteria();
            criteria1.andIn("goodsId", Arrays.asList(ids));

            TbItem item = new TbItem();
            item.setStatus("1");

            itemMapper.updateByExampleSelective(item, itemExample);
        }
    }

    @Override
    public void deleteGoodsByIds(Long[] ids) {
        //如下代码执行sql形如：update tb_goods set is_delete='1' where id in(?,?,...)
        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));

        TbGoods goods = new TbGoods();
        goods.setIsDelete("1");

        goodsMapper.updateByExampleSelective(goods, example);
    }

    @Override
    public List<TbItem> findItemListByGoodsIds(Long[] ids, String status) {
        //select * from tb_item where status=? and goods_id in(?,?....)
        Example example = new Example(TbItem.class);

        example.createCriteria().andEqualTo("status", status)
                .andIn("goodsId" , Arrays.asList(ids));

        return itemMapper.selectByExample(example);
    }

    @Override
    public Goods findGoodsByIdAndStatus(Long goodsId, String status) {
        Goods goods = new Goods();

        //1、根据商品id 查询商品基本信息
        goods.setGoods(findOne(goodsId));

        //2、根据商品id 查询商品描述信息
        goods.setGoodsDesc(goodsDescMapper.selectByPrimaryKey(goodsId));

        //3、根据商品id 查询商品sku列表
        Example example = new Example(TbItem.class);

        example.createCriteria().andEqualTo("goodsId", goodsId);

        example.orderBy("isDefault").desc();

        List<TbItem> itemList = itemMapper.selectByExample(example);
        goods.setItemList(itemList);

        return goods;
    }

    /**
     * 保存商品sku列表
     * @param goods 商品信息
     */
    private void saveItemList(Goods goods) {
        if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            if (goods.getItemList() != null && goods.getItemList().size() > 0) {
                for (TbItem item : goods.getItemList()) {

                    //sku的标题 = spu的商品名称 + 所有规格选项
                    String title = goods.getGoods().getGoodsName();

                    Map<String, String> map = JSONObject.parseObject(item.getSpec(), Map.class);
                    Set<Map.Entry<String, String>> entries = map.entrySet();
                    for (Map.Entry<String, String> entry : entries) {
                        title += " " + entry.getValue();
                    }

                    item.setTitle(title);

                    setItem(item, goods);

                    //保存sku
                    itemMapper.insertSelective(item);
                }
            }
        } else {
            //不启用规格；将spu转换为一个sku并保存
            TbItem tbItem = new TbItem();
            tbItem.setTitle(goods.getGoods().getGoodsName());

            tbItem.setIsDefault("1");
            //未审核
            tbItem.setStatus("0");
            tbItem.setNum(9999);
            tbItem.setPrice(goods.getGoods().getPrice());
            tbItem.setSpec("{}");

            setItem(tbItem, goods);

            itemMapper.insertSelective(tbItem);
        }
    }

    /**
     * 设置商品sku信息
     * @param item 商品sku
     * @param goods 商品信息（基本、描述、sku列表）
     */
    private void setItem(TbItem item, Goods goods) {
        if (!StringUtils.isEmpty(goods.getGoodsDesc().getItemImages())) {
            List<Map> list = JSONArray.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
            item.setImage(list.get(0).get("url").toString());
        }

        item.setSellerId(goods.getGoods().getSellerId());
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        item.setSeller(seller.getName());

        item.setCreateTime(new Date());
        item.setUpdateTime(item.getCreateTime());

        item.setGoodsId(goods.getGoods().getId());

        item.setCategoryid(goods.getGoods().getCategory3Id());
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(item.getCategoryid());
        item.setCategory(itemCat.getName());

        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());
    }
}

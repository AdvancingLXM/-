package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    //购物车列表在redis中的key的名称
    private static final String REDIS_CART_LIST = "CART_LIST";
    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addItemToCartList(Long itemId, Integer num, List<Cart> cartList) {

        TbItem item = itemMapper.selectByPrimaryKey(itemId);

        //1、判断商品是否存在
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        //2、判断商品是否是已经审核通过的
        if (!"1".equals(item.getStatus())) {
            throw new RuntimeException("商品非法");
        }
        //3、判断该商品对应的商家的购物车是否存在
        Cart cart = findCartInCartListBySellerId(cartList, item.getSellerId());
        if(cart == null) {
            if (num > 0) {
                //3.1、购物车列表中该商品对应的商家不存在；重新创建一个购物车对象cart并加入到购物车列表cartList
                cart = new Cart();
                cart.setSellerId(item.getSellerId());
                cart.setSeller(item.getSeller());

                //创建一个购物车商品列表
                List<TbOrderItem> orderItemList = new ArrayList<>();

                //创建一个购物车商品
                TbOrderItem orderItem = createOrderItem(item, num);

                orderItemList.add(orderItem);

                cart.setOrderItemList(orderItemList);

                //将新建的购物车对象加入购物车列表
                cartList.add(cart);
            } else {
                throw new RuntimeException("购买数量非法");
            }
        } else {
            //3.2、购物车列表中该商品对应的商家存在

            TbOrderItem orderItem = findOrderItemInCartByItemId(cart, itemId);
            if(orderItem != null) {
                //3.2.1、商家对应的商品列表中存在商品；购买数量叠加
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
                //如果在商品的购买数量为0的时候；应该将该商品从商品列表中删除
                if (orderItem.getNum() < 1) {
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果购物车中的商品列表为0的时候；应该将该购物车从购物车列表中删除
                if (cart.getOrderItemList().size() == 0) {
                    cartList.remove(cart);
                }

            } else {
                //3.2.2、商家对应的商品列表中不存在商品；重新创建一个购买车商品orderItem并加入到该商家的商品列表orderItemList
                if (num > 0) {
                    TbOrderItem orderItem1 = createOrderItem(item, num);
                    cart.getOrderItemList().add(orderItem1);
                } else {
                    throw new RuntimeException("购买数量非法");
                }
            }
        }
        return cartList;
    }

    @Override
    public List<Cart> findCartListByUserId(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(REDIS_CART_LIST).get(username);
        if (cartList != null) {
            return cartList;
        }
        return new ArrayList<>();
    }

    @Override
    public void saveCartListToRedis(List<Cart> cartList, String username) {
        redisTemplate.boundHashOps(REDIS_CART_LIST).put(username, cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        for (Cart cart : cartList1) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                addItemToCartList(orderItem.getItemId(), orderItem.getNum(), cartList2);
            }
        }
        return cartList2;
    }

    /**
     * 根据商品sku id到购物车对应的购物车商品列表中查询该购物车商品orderItem
     * @param cart 购物车
     * @param itemId 商品sku id
     * @return 购物车商品orderItem
     */
    private TbOrderItem findOrderItemInCartByItemId(Cart cart, Long itemId) {
        if (cart.getOrderItemList() != null && cart.getOrderItemList().size() > 0) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                if (itemId.equals(orderItem.getItemId())) {
                    return orderItem;
                }
            }
        }
        return null;
    }

    /**
     * 根据商品sku创建一个购物车商品对象orderItem
     * @param item 商品sku
     * @param num 购买数量
     * @return 购物车商品对象orderItem
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();

        orderItem.setTitle(item.getTitle());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setItemId(item.getId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setSellerId(item.getSellerId());
        //本商品的总计 = 单价*购买总数
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));

        return orderItem;
    }

    /**
     * 根据商家id在一个购物车列表中查询购物车对象
     * @param cartList 购物车列表
     * @param sellerId 商家id
     * @return 购物车对象Cart
     */
    private Cart findCartInCartListBySellerId(List<Cart> cartList, String sellerId) {
        if (cartList != null && cartList.size() > 0) {
            for (Cart cart : cartList) {
                if (sellerId.equals(cart.getSellerId())) {
                    return cart;
                }
            }
        }
        return null;
    }
}

package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.util.CookieUtils;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/cart")
@RestController
public class CartController {

    //购物车数据在浏览器的cookie的名称
    private static final String COOKIE_CART_LIST = "PYG_CART_LIST";
    //购物车数据在浏览器的cookie的最大生存时间；1天
    private static final int COOKIE_CART_LIST_MAX_AGE = 3600*24;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @Reference
    private CartService cartService;

    /**
     * 登录与未登录
     * 将itemId对应的商品和购买数量加入到购物车列表
     * @param itemId 商品sku id
     * @param num 购买数量
     * @return 操作结果
     */
    @GetMapping("/addItemToCartList")
    public Result addItemToCartList(Long itemId, Integer num){
        try {
            //1、查询cookie购物车列表
            List<Cart> cartList = findCartList();

            //2、将商品sku和购买数量加入到购物车列表
            cartList = cartService.addItemToCartList(itemId, num, cartList);

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if ("anonymousUser".equals(username)) {
                //3.1、将最新的购物车列表数据写回cookie
                String cartListJsonStr = JSONArray.toJSONString(cartList);
                CookieUtils.setCookie(request, response, COOKIE_CART_LIST, cartListJsonStr, COOKIE_CART_LIST_MAX_AGE, true);
            } else {
                //3.2、将最新的购物车列表数据写回redis
                cartService.saveCartListToRedis(cartList, username);
            }
            //4、返回操作结果
            return Result.ok("加入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
        }


        return Result.fail("加入购物车失败");
    }

    /**
     * 查询登录或者未登录情况下的购物车列表
     * @return 购物车列表
     */
    @GetMapping("/findCartList")
    public List<Cart> findCartList(){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Cart> cookie_cartList = new ArrayList<>();
        String cartListJsonStr = CookieUtils.getCookieValue(request, COOKIE_CART_LIST, true);
        if (!StringUtils.isEmpty(cartListJsonStr)) {
            cookie_cartList = JSONArray.parseArray(cartListJsonStr, Cart.class);
        }
        if ("anonymousUser".equals(username)) {
            //1、未登录；则从cookie查询数据

            return cookie_cartList;

        } else {
            //2、已登录；则从redis查询数据
            List<Cart> redis_cartList = cartService.findCartListByUserId(username);

            //合并购物车；如果cookie有数据才需要合并
            if (cookie_cartList.size() > 0) {
                redis_cartList = cartService.mergeCartList(cookie_cartList, redis_cartList);

                //将最新的购物车列表写回redis
                cartService.saveCartListToRedis(redis_cartList, username);

                //要删除cookie中的数据
                CookieUtils.deleteCookie(request, response, COOKIE_CART_LIST);
            }

            return redis_cartList;
        }
    }

    /**
     * 获取当前登录用户或者匿名用户名
     * @return 用户名
     */
    @GetMapping("/getUsername")
    public Map<String, Object> getUsername(){
        Map<String, Object> map = new HashMap<>();

        //如果是匿名登录则返回的名称为anonymousUser
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        map.put("username", username);

        return map;
    }
}

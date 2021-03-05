package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/goods")
@RestController
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return goodsService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            //获取当前登录用户
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.getGoods().setSellerId(sellerId);
            goods.getGoods().setAuditStatus("0");//未审核

            goodsService.addGoods(goods);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    /**
     * 根据商品id查询商品基本、描述、sku列表
     * @param id 商品spu id
     * @return 商品基本、描述、sku列表
     */
    @GetMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findGoodsById(id);
    }

    /**
     * 根据商品id修改原有商品信息{基本、描述、sku列表}
     * @param goods 商品信息{基本、描述、sku列表}
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            //查看原来这个商品的商家是否与现在的登录用户是同一个；再判断当前登录用户与现在要保存的商品的商家是否同一个人
            TbGoods oldGoods = goodsService.findOne(goods.getGoods().getId());

            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();

            if(!sellerId.equals(oldGoods.getSellerId()) && !sellerId.equals(goods.getGoods().getSellerId())){
                return Result.fail("不合法的操作");
            }

            goodsService.updateGoods(goods);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param goods 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbGoods goods, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        //获取当前登录用户的名称
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);
        return goodsService.search(page, rows, goods);
    }

    /**
     * 商品的审核状态改为要修改的状态
     * @param ids 商品spu id集合
     * @param status 要更新为的状态
     * @return 操作结果
     */
    @GetMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status){
        try {
            goodsService.updateStatus(ids, status);

            return Result.ok("更新商品状态成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("更新商品状态失败");
    }

}

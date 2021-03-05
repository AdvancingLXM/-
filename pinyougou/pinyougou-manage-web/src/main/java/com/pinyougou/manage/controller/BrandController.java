package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/brand")
//@Controller
@RestController //组合注解；@ResponseBody @Controller；对本来的所有方法生效
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 根据分页页号、页大小分页查询品牌列表
     * @param page 页号
     * @param rows 页大小
     * @return 品牌列表
     */
    @GetMapping("/testPage")
    public List<TbBrand> testPage(@RequestParam(value="page", defaultValue = "1")Integer page,
                                  @RequestParam(value="rows", defaultValue = "10")Integer rows){
        //return brandService.testPage(page, rows);
        return (List<TbBrand>) brandService.findPage(page, rows).getRows();
    }

    /**
     * 查询所有品牌列表
     * @return 品牌列表
     */
    //@RequestMapping(value = "/findAll", method = RequestMethod.GET)
    @GetMapping("/findAll")
    //@ResponseBody
    public List<TbBrand> queryAll(){
        //return brandService.queryAll();
        return brandService.findAll();
    }

    /**
     * 根据分页页号、页大小分页查询品牌列表
     * @param page 页号
     * @param rows 页大小
     * @return 品牌列表
     */
    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value="page", defaultValue = "1")Integer page,
                                  @RequestParam(value="rows", defaultValue = "10")Integer rows){
        return brandService.findPage(page, rows);
    }

    /**
     * 新增品牌
     * @param brand 品牌
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody TbBrand brand){
        try {
            brandService.add(brand);
            return Result.ok("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("新增失败");
    }

    /**
     * 根据品牌id查询品牌
     * @param id 品牌id
     * @return 品牌
     */
    @GetMapping("/findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }

    /**
     * 更新品牌
     * @param brand 品牌
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return Result.ok("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("更新失败");
    }

    /**
     * 根据品牌id集合批量删除品牌
     * @param ids 品牌id集合
     * @return 操作结果
     */
    @GetMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.deleteByIds(ids);
            return Result.ok("删除记录成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除记录失败");
    }

    /**
     * 根据条件分页模糊查询品牌数据
     * @param brand 查询条件
     * @param page 页号
     * @param rows 页大小
     * @return 分页对象
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody TbBrand brand,
                         @RequestParam(value="page", defaultValue = "1")Integer page,
                         @RequestParam(value="rows", defaultValue = "10")Integer rows){
        return brandService.search(brand, page, rows);
    }

    /**
     * 构建select2的品牌下拉框的数据；数据结构形如：[{id:'1',text:'联想'},{id:'2',text:'华为'}]
     * @return 品牌数据
     */
    @GetMapping("/selectOptionList")
    public List<Map<String, String>> selectOptionList(){
        return brandService.selectOptionList();
    }
}

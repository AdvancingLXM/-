package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.util.PhoneFormatCheckUtils;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

@RequestMapping("/user")
@RestController
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("/findAll")
    public List<TbUser> findAll() {
        return userService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return userService.findPage(page, rows);
    }

    /**
     * 在通过验证码验证之后，将用户信息保存到数据库中
     * @param user 用户信息
     * @param smsCode 验证码
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody TbUser user, String smsCode) {
        try {
            //校验验证码
            if(userService.checkSmsCode(user.getPhone(), smsCode)) {
                user.setCreated(new Date());
                user.setUpdated(user.getCreated());
                //md5加密密码
                user.setPassword(DigestUtils.md5Hex(user.getPassword()));
                userService.add(user);
                return Result.ok("注册成功");
            } else {
                return Result.fail("验证码输入不正确，注册失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("注册失败");
    }

    @GetMapping("/findOne")
    public TbUser findOne(Long id) {
        return userService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbUser user) {
        try {
            userService.update(user);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            userService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param user 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbUser user, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return userService.search(page, rows, user);
    }

    /**
     * 发送验证码到手机
     * @param phone 手机号
     * @return 发送结果
     */
    @GetMapping("/sendSmsCode")
    public Result sendSmsCode(String phone){
        try {
            if (PhoneFormatCheckUtils.isPhoneLegal(phone)) {
                //发送验证码
                userService.sendSmsCode(phone);
                return Result.ok("发送验证码成功");
            } else {
                return Result.fail("手机号码不正确");
            }
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
        }
        return Result.fail("发送验证码失败");
    }

    /**
     * 获取当前登录用户名称
     * @return
     */
    @GetMapping("/getUsername")
    public Map<String, Object> getUsername(){
        Map<String, Object> map = new HashMap<>();

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        map.put("username", username);

        return map;
    }

}

package com.pinyougou.shop.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    //根据输入的用户名到数据库中查询是否存在该审核通过的用户；
    // 如果存在则查询该用户的密码构造一个User并返回；如果用户从前台输入的密码与构造的User对象中给定的密码一致则认证通过
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        TbSeller seller = sellerService.findOne(username);

        if (seller != null && "1".equals(seller.getStatus())) {
            //security将从前台输入的密码与如下返回的对象进行对比；如果一致则认证成功
            return new User(username, seller.getPassword(), authorities);
        }

        return null;
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }
}

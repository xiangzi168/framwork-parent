/*
* create by mybatis-plus-generator  https://github.com/xiweile
*/
package com.amg.fulfillment.cloud.logistics.controller;

import com.amg.fulfillment.cloud.logistics.mapper.UserMapper;
import com.amg.fulfillment.cloud.logistics.service.IUserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 测试 前端控制器
 * </p>
 *
 * @author zzx
 * @since 2021-03-24
 */
@RestController
@RequestMapping
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private UserMapper userMapper;



    @GetMapping("mybatisplus")
    public Page getBasicListInfo1(String page, String row) {
        Page p = new Page(1, 10);
//        p = userService.page(p, null);
        return p;
    }


}

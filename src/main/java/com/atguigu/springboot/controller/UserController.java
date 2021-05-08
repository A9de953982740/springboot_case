package com.atguigu.springboot.controller;

import com.atguigu.springboot.pojo.User;
import com.atguigu.springboot.service.UserService;
import com.atguigu.springboot.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * @author cjhirom
 * @create 2021-05-06 10:03
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 查询所有用户信息
     * controller方法返回一个集合是可以的，但对于所有的controller的方法需要有一个统一的结果，不管返回的是集合或bean等等，都要使用Result来表示
     */
    @GetMapping("/user/findAll")
    public Result findAll() {
        try {
            List<User> userList = userService.findAll();
            return Result.ok(true, "成功", userList);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return Result.error(false, "失败");
        }
    }


}

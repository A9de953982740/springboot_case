package com.atguigu.springboot.service;

import com.atguigu.springboot.pojo.User;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * @author cjhirom
 * @create 2021-05-05 22:20
 */
public interface UserService {

    /**
     * 查询所有用户信息
     */
    public List<User> findAll() throws FileNotFoundException;


}

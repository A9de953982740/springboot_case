package com.atguigu.springboot;

import com.atguigu.springboot.dao.UserMapper;
import com.atguigu.springboot.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cjhirom
 * @create 2021-05-05 21:54
 */
@RunWith(SpringRunner.class)    //以Spring方式进行启动，可直接取IOC容器中的bean对象
@SpringBootTest
public class UserMapperTest {

    //@Autowired
    @Resource
    UserMapper userMapper;

    @Test   //注意，导包，导入org.junit.Test（JUnit4版本）
    public void testSelectAll(){
        List<User> users = userMapper.selectAll();
        System.out.println(users);
    }

}

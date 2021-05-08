package com.atguigu.springboot.dao;

import com.atguigu.springboot.pojo.User;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

// 通用Mapper简化MyBatis开发，继承Mapper<t>后有一些简单的增删改查方法
@Repository //分层注解，以前使用在实现类上，不在接口上，标记后意味着这个接口的对象将来要在IOC容器中生成出来，此时业务层@Autowired注解就能识别出来
public interface UserMapper extends Mapper<User> {
}

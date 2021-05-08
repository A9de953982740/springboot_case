package com.atguigu.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

// 主启动，作用：它是一个配置类，启用了自动配置，增加了默认扫描包
// 设置扫描dao层的包，否则，无法创建Dao的对象，因为定义的Mapper接口没有定义实现类，得去扫描XxxMapper让容器去创建对象
@MapperScan("com.atguigu.springboot.dao")   //一定要导入tk.mybatis通用Mapper组件包。
@EnableTransactionManagement    //开启声明式事务，是Spring提供的
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        //args指程序运行参数。
        SpringApplication.run(Application.class, args);
    }
}

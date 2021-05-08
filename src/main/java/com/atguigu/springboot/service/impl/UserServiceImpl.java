package com.atguigu.springboot.service.impl;

import com.atguigu.springboot.dao.UserMapper;
import com.atguigu.springboot.pojo.User;
import com.atguigu.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * @author cjhirom
 * @create 2021-05-05 22:20
 */
@Service
@Transactional(readOnly = true) //表示所有方法都是只读的
public class UserServiceImpl implements UserService {

    //@Resource   //方式一
    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisTemplate redisTemplate;

    /**
        对于业务层代码来讲，查询希望增加一个只读操作，如果是增删改则增加一个增删改的事务操作，因为一个业务方法中可能会调用多个dao方法，
        即执行多条sql，希望做成一个原则为不可分割，遵循事务的四个特性。此时便需要集成事务。
        1、在主程序上加入注解@EnableTransactionManagement，表示开启声明式事务
        2、在Impl类的方法上加入@Transactional进行一些属性配置；在Impl类上加入该注解则表示所有方法都是只读的。（方法上的注解优先于类上的注解）

        @Transactional
        readOnly = true表示只读，效率高，但只读的数据在同一个连接上执行操作是不可以修改本数据的；如果不是只读的，是事务型的，即要做增删改，
                       此时数据库在生成连接时要做事务的隔离，占用一些资源，如将数据做备份，回滚时数据还能还原

        findAll()是业务层方法，交给Controller层方法调用，在开发过程中，尽量不让一个业务方法调用另外一个业务方法，目的是不进行事务传播，类似于事务嵌套，
        如A业务方法已经有事务，再去调用另外一个B业务方法也有事务，不好控制。所以Controller中一个方法只调用一个service方法，完成所有业务逻辑。
        controller方法一定没有事务的，它来调用业务层方法，那么是否要开启事务就要看传播行为怎么设置的。

        propagation 指传播行为，一共七种
        propagation = Propagation.REQUIRED_NEW 即：有事务或没有事务的方法来调用findAll()，总会开启一个新事务；
        propagation = Propagation.REQUIRED 即：有事务的方法来调用findAll()则加入到该事务，没有事务的方法findAll()来调用则自身开启一个事务；
        propagation = Propagation.MANDATORY 即：findAll()必须要在有事务的方法中运行，但自身不会创建事务，若在没有事务的方法中运行则报错；
        propagation = Propagation.NEVER 即：findAll()永远不能在事务中运行，只要有事务的方法来调用则报错；
        propagation = Propagation.SUPPORTS 即：有事务的方法来调用findAll()则加入该事务，没有事务的方法来调用findAll()则不需要事务正常运行；
        propagation = Propagation.NOT_SUPPORTS 即：没有事务的方法来调用findAll()则正常运行，有事务的方法来调用则把该方法的事务挂起，
                                               findAll()运行完再开始被挂起的事务；
        propagation = Propagation.NESTED 即：嵌套事务，需要特定的事务管理器进行使用

        isolation 指事务的隔离级别，一共四种
        1、读未提交（READ_UNCOMMITTED）：如A事务刚执行update语句但还没有提交，B事务得到A事务未提交的结果。可以解决数据丢失，但无法解决脏读。
        2、读已提交（READ_COMMITTED）（Oracle默认隔离级别）：如B事务读取A事务执行update语句后提交了数据的结果。可以解决数据丢失，脏读，但出现不可重复读。
        3、可重复读（REPEATABLE_READ）（MySql默认隔离级别）：如A事务修改了C数据，B事务再次得到C数据仍是第一次读取C数据的结果，即B事务与其他事务是隔离开的，
        只能看见本事务内的数据结果，无法得知其他事务是否对C数据进行了修改。解决了数据丢失，脏读，不可重复读问题。
        4、串行化（SERIALIZABLE）：如A事务端操作test、test1表时会锁定该数据，如果B事务想要操作test.test1就需要等待A事务释放锁

        没有事务隔离级别的情况下出现的问题：
        数据丢失：指在没有事务的情况下造成，即两个事务在并发修改同一个数据，把数据弄没了；
        脏读：如A事务刚执行update语句但还没有提交，B事务得到A事务未提交的结果，因为A事务可能不提交了；
        不可重复读：如B事务再次读取该数据发现数据被修改了，即被多次修改，在进行业务逻辑时无法准确使用该值；
        幻读：如数据库中某表有两条数据，A事务统计是两条数据，B事务统计是两条数据，C事务执行了insert语句，D事务统计是三条数据，出现混乱。
             一般是通过搭建另一个服务器解决的，因为幻读问题在业务上往往是允许的，但个别业务不允许，比如：统计在线人数，第一次统计100W，第二次统计100W+3人，
             对业务不会有太大影响，但对于财务统计营业额，第一次1000w，第二次10001W，别的事务又增加了金额，无法控制别的事务不去增删改数据。
             可以把整个库导出备份到新搭建的一个Mysql数据库服务器T2，那么这个T2服务器只做统计，不对外开放，只让专门统计财务的一个人使用，
             T1服务器交给其他的业务人员使用。即可解决

        rollbackFor = Exception.class 表示出现任何异常都会回滚
        noRollbackFor = FileNotFoundException.class 表示出现FileNotFoundException异常时不回滚
    */
    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.SERIALIZABLE,
            rollbackFor = Exception.class,
            noRollbackFor = FileNotFoundException.class)
    @Override
    public List<User> findAll() throws FileNotFoundException {
        //int i = 1 / 0;  //运行时异常，Spring AOP 声明式事务，默认对于运行时异常进行回滚。
        //FileInputStream xxx = new FileInputStream("xxx");   //编译器异常，若读取不到该文件，默认不回滚

        //从Redis读取数据，绑定一个key为userkey，通过get()获取数据，这个数据是一个list，因为findAll方法返回值为list
        List<User> users = (List<User>) redisTemplate.boundValueOps("userkey").get();
        if (users == null){ //缓存没有
            users = userMapper.selectAll();
            System.out.println("从数据库users = " + users);
            //从Redis中绑定一个key为userkey，通过set()保存数据到Redis
            redisTemplate.boundValueOps("userkey").set(users);
        } else {    //缓存中有
            System.out.println("从缓存中users = " + users);
        }
        //如果缓存中有数据，直接返回
        return users;
    }
}

package com.sky.mybatis.common;

import com.sky.mybatis.mapper.PersonMapper;
import com.sky.mybatis.model.Person;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.function.Function;
import java.util.logging.Logger;

public class DBUtils {

    private static Logger logger = Logger.getLogger(DBUtils.class.getName());

    public static <T, R> R invoke(Function<T, R> function,Class<T> clz,Boolean autoCommit) throws Exception {
        // 1.根据XML文件(全局配置文件)构建SqlSessionFactory对象
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        // 2.获取SqlSession实例(默认会话不会自动提交事务),能直接执行已经映射的SQL语句
        try (SqlSession session = sqlSessionFactory.openSession(autoCommit)) {
            // 3.获取接口的实现类对象(会为接口自动的创建一个代理对象,由代理对象执行相应操作)
            T mapper = session.getMapper(clz);
            logger.info("Mapper类型:"+mapper);
            R result = function.apply(mapper);
            logger.info("结果:"+mapper);
            return result;
        }
    }


    public static <T, R> R invoke(Function<T, R> function,Class<T> clz) throws Exception {
        return invoke(function,clz,false);
    }

}

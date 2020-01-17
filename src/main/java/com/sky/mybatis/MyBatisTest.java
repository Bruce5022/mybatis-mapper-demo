package com.sky.mybatis;

import com.sky.mybatis.common.DBUtils;
import com.sky.mybatis.mapper.PersonMapper;
import com.sky.mybatis.model.Person;

import java.sql.Date;

/**
 * 1.mybatis允许增,删,改,直接定义以下类型的返回值:Integer,Long,Boolean,这些直接在接口方法上定义写返回值类型就行,影响多少行就会返回
 * 2.手工提交事务
 */

public class MyBatisTest {


    public static void main(String[] args) throws Exception {
        testDelete();
    }


    public static void testSelect() throws Exception {
        Person person = DBUtils.invoke((t) -> t.findById(2), PersonMapper.class);
        System.err.println("执行结果:" + person);
    }



    public static void testAdd() throws Exception {

        Integer result = DBUtils.invoke((t) -> {
            Person person = new Person();
            person.setEmail("test01@aliyun.com");
            person.setBirthDay(new Date(1990, 2, 16));
            person.setName("test01");
            person.setSex(0);

            return t.addPerson(person);
        }, PersonMapper.class, true);

        System.err.println("执行结果:" + result);
    }

    public static void testUpdate() throws Exception {

        Long result = DBUtils.invoke((t) -> {
            Person person = t.findById(13);
            person.setEmail("test01@aliyun.com");
            person.setBirthDay(new Date(200,2,16));
            person.setName("test01");
            person.setSex(1);
            return t.updatePerson(person);
        }, PersonMapper.class,true);

        System.err.println("执行结果:" + result);
    }


    public static void testDelete() throws Exception {

        Boolean result = DBUtils.invoke((t) -> t.deleteById(13) , PersonMapper.class,true);
        System.err.println("执行结果:" + result);
    }


}

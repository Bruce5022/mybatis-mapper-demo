package com.sky.mybatis;

import com.sky.mybatis.common.DBUtils;
import com.sky.mybatis.mapper.PersonMapper;
import com.sky.mybatis.model.Person;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 1.mybatis允许增,删,改,直接定义以下类型的返回值:Integer,Long,Boolean,这些直接在接口方法上定义写返回值类型就行,影响多少行就会返回
 * 2.手工提交事务
 */

public class MyBatisTest {


    public static void main(String[] args) throws Exception {
        testSelectByCondition();
    }


    public static void testSelect() throws Exception {
        Person person = DBUtils.invoke((t) -> t.findById(2), PersonMapper.class);
        System.err.println("执行结果:" + person);
    }


    public static void testSelectByCondition() throws Exception {
        Person person = DBUtils.invoke((t) -> t.findByNameAndSex("test01",0), PersonMapper.class);
        System.err.println("执行结果:" + person);
    }


    public static void testSelectByMap() throws Exception {
        Map<String,Object> map = new HashMap<>();
        map.put("name","test01");
        map.put("sex",0);
        Person person = DBUtils.invoke((t) -> t.findByMap(map), PersonMapper.class);
        System.err.println("执行结果:" + person);
    }



    public static void testAdd() throws Exception {

        Integer result = DBUtils.invoke((t) -> {
            Person person = new Person();
            person.setEmail("test01@aliyun.com");
            person.setBirthDay(new Date(1990, 2, 16));
            person.setName("test01");
            person.setSex(0);

            System.out.println("执行前:"+person);
            Integer integer = t.addPerson(person);
            System.out.println("执行后:"+person);
            return integer;
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


    /**
     * 把生成的主键赋值给返回对象中
     * @throws Exception
     */
    public static void testInsert() throws Exception {

        Integer result = DBUtils.invoke((t) -> {
            Person person = new Person();
            person.setEmail("test01@aliyun.com");
            person.setBirthDay(new Date(1990, 2, 16));
            person.setName("test01");
            person.setSex(0);
            System.out.println("执行前:"+person);
            Integer integer = t.insertPerson(person);
            System.out.println("执行后:"+person);
            return integer;
        }, PersonMapper.class, true);

        System.err.println("执行结果:" + result);
    }
}

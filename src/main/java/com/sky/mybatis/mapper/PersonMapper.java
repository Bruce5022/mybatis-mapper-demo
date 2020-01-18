package com.sky.mybatis.mapper;

import com.sky.mybatis.model.Person;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface PersonMapper {

    Person findById(Integer id);

    Integer addPerson(Person person);


    /**
     * mysql支持主键自增,自增主键的获取,mybatis也是通过statement.getGeneratedKeys()
     * 1.useGeneratedKeys:使用自增主键获取主键值策略
     * 2.keyProperty:指定对应的主键属性,也就是mybatis获取到主键值后,将这个值封装给java bean的哪个属性
     */
    Integer insertPerson(Person person);

    Long updatePerson(Person person);

    Boolean deleteById(Integer id);


    Person findByNameAndSex(@Param("name") String name, @Param("sex") Integer sex);


    Person findByMap(Map<String,Object> map);


}

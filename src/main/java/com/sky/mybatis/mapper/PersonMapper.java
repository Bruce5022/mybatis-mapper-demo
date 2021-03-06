package com.sky.mybatis.mapper;

import com.sky.mybatis.model.Person;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
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


    List<Person> findAllByName(@Param("name") String name);


    Person findByMap(Map<String,Object> map);


    /**
     *
     * 特别注意:如果是Collection(List,Set)类型或者是数组, 也会特殊处理.也是把传入的list或者数组封装在map中:
     *      key为:
     *          Collection--->collection
     *          List---->list或array
     * 取值:取出第一个id的值:#{list[0]}
     */
    Person findPerson(List<Integer> ids);



    // 返回map,key是列名,值为值
    Map<String,Object> findPersonToMap(Integer id);


    // 返回map,key是id,值为对象
    @MapKey("id")
    Map<String,Person> findPerson2Map(Integer id);
}

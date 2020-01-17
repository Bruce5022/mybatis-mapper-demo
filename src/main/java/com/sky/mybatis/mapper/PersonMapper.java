package com.sky.mybatis.mapper;

import com.sky.mybatis.model.Person;

public interface PersonMapper {

    Person findById(Integer id);

    Integer addPerson(Person person);

    Long updatePerson(Person person);

    Boolean deleteById(Integer id);



}

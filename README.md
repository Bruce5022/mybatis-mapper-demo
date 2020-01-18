# MyBatis 参数处理
##1.单个参数不做特殊处理
```
{参数名}:取出参数值.
```
##2.多个参数会做特殊处理
```
多个参数被封装成一个map:
    key:param1...paramN,
      或arg0...argN
    value:传入的参数值
#{}就是从map中获取指定的key的值
```
测试代码:
```
1.mapper文件
<select id="findByNameAndSex" resultType="person">
  select * from test_user a where a.name = #{name} and a.sex = #{sex}
</select>

2.mapper接口
Person findByNameAndSex(String name, Integer sex);

3.测试代码
public static void testSelectByCondition() throws Exception {
    Person person = DBUtils.invoke((t) -> t.findByNameAndSex("test01",0), PersonMapper.class);
    System.err.println("执行结果:" + person);
}

4.异常信息
Cause: org.apache.ibatis.binding.BindingException: 
Parameter 'name' not found. 
Available parameters are [arg1, arg0, param1, param2]
```
改造方案1:
```  
1.取param
<select id="findByNameAndSex" resultType="person">
  select * from test_user a where a.name = #{param1} and a.sex = #{param2}
</select>

2.取arg
<select id="findByNameAndSex" resultType="person">
  select * from test_user a where a.name = #{arg0} and a.sex = #{arg1}
</select>
```
改造方案2:
```  
1.用@Param注解指定参数名(这样替换掉集合中的arg0...argN,还能用param1...paramN)
Person findByNameAndSex(@Param("name") String name, @Param("sex") Integer sex);

<select id="findByNameAndSex" resultType="person">
  select * from test_user a where a.name = #{name} and a.sex = #{sex}
</select>
```
##3.POJO
```
如果多个参数正好是我们业务逻辑的数据模型,我们就可以直接传入pojo:
    #{属性名}:取出传入的pojo的属性值
```

##4.Map
```
如果多个参数不是业务模型中的数据,没有对应的pojo,也不经常使用,为了方便,我们也可以传入map:
    #{key}:取出map中对应的值
```

##5.TO
```
如果多个参数不是业务模型中的数据,但是经常要使用,推荐来编写一个TO(Transfer Object)
page{
int index;
int size;
}
```

##6.实例练习
```
/**
 * 取值:name==>#{name/param1}
 *      sex==>#{param2}
 */
Person findPerson(@Param("name") String name,Integer sex);

/**
 * 取值:name==>#{param1}
 *      sex==>#{param2.sex / e.sex}
 */
Person findPerson(String name,@Param("e") Person person);

/**
 *
 * 特别注意:如果是Collection(List,Set)类型或者是数组, 也会特殊处理.也是把传入的list或者数组封装在map中:
 *      key为:
 *          Collection--->collection
 *          List---->list或array
 * 取值:取出第一个id的值:#{list[0]}
 */
Person findPerson(List<Integer> ids);
```


##7.源码分析
```


```
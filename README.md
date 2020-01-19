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
Person findByNameAndSex(@Param("name") String name, @Param("sex") Integer sex);

ParamNameResolver解析参数封装map的:
1.names:{0=name,1=sex}
    a.获取每个标了@Param注解的参数的@Param值:name,sex;赋值给names;
    b.每次解析一个参数给map中保存信息:key-参数索引,value-name的值'
        name的值:
            标注了param注解:注解的值
            没有标注:
                全局配置:useActualParamName(jdk1.8使用)
                name=map.size():相当于当前元素的索引
   如果还有第三个参数没有用@Param,那么names:{0=name,1=sex,2=2}
                
 public ParamNameResolver(Configuration config, Method method) {
     final Class<?>[] paramTypes = method.getParameterTypes();
     final Annotation[][] paramAnnotations = method.getParameterAnnotations();
     final SortedMap<Integer, String> map = new TreeMap<>();
     int paramCount = paramAnnotations.length;
     // get names from @Param annotations
     for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
       if (isSpecialParameter(paramTypes[paramIndex])) {
         // skip special parameters
         continue;
       }
       String name = null;
       for (Annotation annotation : paramAnnotations[paramIndex]) {
         if (annotation instanceof Param) {
           hasParamAnnotation = true;
           name = ((Param) annotation).value();
           break;
         }
       }
       if (name == null) {
         // @Param was not specified.
         if (config.isUseActualParamName()) {
           name = getActualParamName(method, paramIndex);
         }
         if (name == null) {
           // use the parameter index as the name ("0", "1", ...)
           // gcode issue #71
           name = String.valueOf(map.size());
         }
       }
       map.put(paramIndex, name);
     }
     names = Collections.unmodifiableSortedMap(map);
   }
    

  public Object getNamedParams(Object[] args) {
    final int paramCount = names.size();
    if (args == null || paramCount == 0) {
      return null;
    } else if (!hasParamAnnotation && paramCount == 1) {
      return args[names.firstKey()];
    } else {
      final Map<String, Object> param = new ParamMap<>();
      int i = 0;
      for (Map.Entry<Integer, String> entry : names.entrySet()) {
        param.put(entry.getValue(), args[entry.getKey()]);
        // add generic param names (param1, param2, ...)
        final String genericParamName = GENERIC_NAME_PREFIX + String.valueOf(i + 1);
        // ensure not to overwrite parameter named with @Param
        if (!names.containsValue(genericParamName)) {
          param.put(genericParamName, args[entry.getKey()]);
        }
        i++;
      }
      return param;
    }
  }

```
##8.取值方式差别
```
#{}:可以获取map中的值或者pojo对象属性的值;
${}:同上;
区别:
#{}是以预编译的形式,将参数设置到sql语句中,PreparedStatement;防止sql注入;
${}:取值的值直接拼装在sql语句中;会有安全问题;
大多情况下,我们去取参数的值,都应该去使用#{};比如分库分表,按照年份分表拆分,原
生jdbc不支持占位符的地方,我们就可以使用${}进行取值:
select * from ${year}_salary where ***;
select * from test_user order by ${name} ${desc} 







1.修改映射文件语句
<select id="findByMap" resultType="person">
    select * from test_user a where a.name = ${name } and a.sex = #{sex}
</select>

运行结果:
06:54:05,351 DEBUG PooledDataSource:334 - PooledDataSource forcefully closed/removed all connections.
一月 19, 2020 6:54:05 上午 com.sky.mybatis.common.DBUtils invoke
信息: Mapper类型:org.apache.ibatis.binding.MapperProxy@1fffcd7
06:54:05,433 DEBUG JdbcTransaction:136 - Opening JDBC Connection
06:54:07,037 DEBUG PooledDataSource:405 - Created connection 24231603.
06:54:07,037 DEBUG JdbcTransaction:100 - Setting autocommit to false on JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@171beb3]
06:54:07,086 DEBUG findByMap:143 - ==>  Preparing: select * from test_user a where a.name = test01 and a.sex = ? 
06:54:07,112 DEBUG findByMap:143 - ==> Parameters: 0(Integer)
06:54:07,176 DEBUG JdbcTransaction:122 - Resetting autocommit to true on JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@171beb3]
Exception in thread "main" org.apache.ibatis.exceptions.PersistenceException: 
06:54:07,218 DEBUG JdbcTransaction:90 - Closing JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@171beb3]
06:54:07,219 DEBUG PooledDataSource:362 - Returned connection 24231603 to pool.
### Error querying database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column 'test01' in 'where clause'
### The error may exist in mapper/PersonMapper.xml
### The error may involve defaultParameterMap
### The error occurred while setting parameters
### SQL: select * from test_user a where a.name = test01 and a.sex = ?
### Cause: java.sql.SQLSyntaxErrorException: Unknown column 'test01' in 'where clause'

解决办法: map.put("name","'test01'");

```

##9.#{}取值进一步说明
```
参数可以指定特殊的类型:
#{property,javaType=int,jdbcType=numeric}
#{property,javaType=double,jdbcType=numeric,numericScale=2}

1.javaType通常可以从java对象中来确定;
2.如果null被当作值来传递,对于所有可能为空的列,jdbcType需要被设置;
3.对于数值类型的值,还可以指定小数点后的位数;
4.mode属性允许指定in,out或inout参数,如果参数为out或inout,参数对象的真实值将会被改变,就像在获取输出参数时所期望的那样;
5.参数位置支持的属性:javaType,jdbcType,mode(存储过程用到),numericScale,resultMap,typeHandler,jdbcTypeName,expression(未来支持的功能)
6.实际上通被设置的是:可能为空的列名指定jdbcType,在我们数据为null的时候,有些数据库可能不能识别mybatis对null的默认处理,比如oracle数据库,如果传null并没指定jdbcType,oracle会报错

```
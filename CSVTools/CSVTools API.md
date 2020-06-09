# CSVTools API

### Class CSVFileDatabase

#### Constructor

+ ```java
  public CSVFileDatabase(String filePath, int recordsBufSize) throws Exception
  ```

  + @Params

    + **String filePath**： csv数据文件的路径 (CSV文件第一行为列名，显式列出属性名称)

    + **int recordsBufSize**: Database缓存容量，单位为csv数据的行数（最大32768行）

  + @Exceptions

    + **IllegalArgumentException**: csv文件路径为空；缓存容量设置为0或负数； 缓存设置过大
    + **FileNotFoundException**: csv文件路径不对，找不到文件
    + **其他**： csv文件内容为空，格式不合法

+ ```java
  public CSVFileDatabase(String filePath) throws Exception
  ```

  + @Params
  
    + **String filePath**： csv数据文件的路径
  
      使用默认大小缓冲区（1024行）
  
  + @Exceptions 同上

#### InnerClass

```java
public abstract class CSVDbSearchCondition {
    public static final int MAP_RECORD_COND = 2;
    public static final int ARRAY_RECORD_COND = 1;
    private int condType = 0;

    public boolean mapCondImplemented() { return (condType & MAP_RECORD_COND) != 0; }
    public boolean arrayCondImplemented() { return (condType & ARRAY_RECORD_COND) != 0; }

    abstract boolean meetCondition(Map<String, String> record);

    abstract boolean meetCondition(String[] record);

    public CSVDbSearchCondition(int condType) { this.condType = condType; }
}
```

这个内部抽象类是用于筛选符合条件的行（记录）的，核心方法是boolean meetCondition(Map<String, String> record) 和 boolean meetCondition(String[] record)，当返回true时说明符合条件否则不符合。使用该类对象的时候这两个抽象方法首先需要被实现。

+ boolean meetCondition(Map<String, String> record) 

  这个方法传入参数是Map<String, String>类型，实际上就是csv的一行，Map内部完成了列名和实际列属性之间的映射关系，这样在方法内部取得某一列的值的时候就可以直接使用Map的get方法使用属性名获得value。

+ boolean meetCondition(String[] record)

  传入参数是String[]，就是将csv中的一行中的各列值(String)顺序保存在一个数组内，传递至方法内。实现的时候要注意数组中的存放顺序。

这个抽象类除了实现这两个方法以外，还需要在构造时设定Field: private int condType，这个值说明了两个meetCondition方法中的哪一个是被正确实现的：

+ 当被设定为MAP_RECORD_COND时，说明boolean meetCondition(Map<String, String> record) 被正确实现
+ 当被设定为ARRAY_RECORD_COND时，说明boolean meetCondition(String[] record)被正确实现
+ 当然也可以将两个方法都正确实现，这时将condType值设置为MAP_RECORD_COND | ARRAY_RECORD_COND，也就是3

#### Methods

+ ```java
  public String[] getHeader()
  ```

  + 功能：取csv文件头（第一行），即所有列名称
  + @Return
    + **String[]** 返回的String数组为对应csv文件的属性名列表（按照文件中的先后顺序）

+ ```java
  public List<Map<String, String>> searchRecord (CSVDbSearchCondition cond) throws IllegalArgumentException
  ```

  + 功能：查找符合筛选条件的csv行（记录）

  + @Params

    + **CSVDbSearchCondition cond**： 即上文所述内部类，需要至少有一个meetCondition方法被正确实现，如果两个均被实现（condType被正确设置为MAP_RECORD_COND | ARRAY_RECORD_COND），则优先使用boolean meetCondition(String[] record) 。当某一行使用meetCondition方法返回结果为true时则说明该行符合筛选条件

  + @Return

    + **List<Map<String, String>>**: 返回所有符合条件的CSV行

  + @Exception

    + **IllegalArgumentException**: 当CSVDbSearchCondition cond为null时；当CSVDbSearchCondition中的condType未被正确设置（该类未被正确设置）

      当出现读到的csv行格式不正确时程序会结束：值的数量不等于定义的列数；行中的某一值为null

+ ```java
  public List<Map<String, String>> searchRecord (Map<String, String> attrValues) throws IllegalArgumentException
  ```
  + @Params

    + **Map<String, String> attrValues**: 指定符合要求的csv行的属性值，Map中存储的键值对为<Attribute name, Value>，举例：想要查找名字(name)为Tom，性别(gender)为male，年龄(age)为24的记录，Map中的键值对应为<"name", "Tom">, <"gender", "male">, <"age", "24">。

  + @Return

    + **List<Map<String, String>>**: 返回所有符合条件的CSV行

  + @Exception

    + **IllegalArgumentException**: 当Map<String, String> attrValues为null时；当Map<String, String> attrValues中未包含任何键值对时；当Map<String, String> attrValues中包含不存在的列名或列名为null时或要求的属性为null时

      当出现读到的csv行格式不正确时程序会结束：值的数量不等于定义的列数；行中的某一值为null

+ ```java
  public List<Map<String, String>> searchRecord (String attr, String value) throws IllegalArgumentException
  ```

  + @Params

    + **String attr**: 需要被筛选的列名
    + **String value**: 该列所要求的值

    实际上就是简化版的public List<Map<String, String>> searchRecord (Map<String, String> attrValues) ，只支持对某一列的值进行控制

  + @Return

    + **List<Map<String, String>>**: 返回所有符合条件的CSV行

  + @Exception

    + **IllegalArgumentException**: 当String attr为null时；当String value为null时；当String attr指定的列名不存在时

      当出现读到的csv行格式不正确时程序会结束：值的数量不等于定义的列数；行中的某一值为null

+ ```java
  public int addRecord(Map<String, String> newRecord) throws Exception
  ```

  + 功能：csv文件增加新的一行
  + @Params
    + Map<String, String> newRecord: 需要加入csv文件末尾的新的csv行，以Map<String, String>形式存储
  + @Return
    + **int**: 正常插入返回0 (目前只要不throw exception就返回0)
  + @Exception
    + **Exception**: 当Map中缺少某一列时；当Map中某键值对的value为null时
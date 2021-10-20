# Table of Contents

* [xyz-support](#xyz-support)
  * [介绍](#介绍)
    * [引入](#引入)
    * [当前引用Spring Boot版本](#当前引用spring-boot版本)
    * [当前引用外部依赖的版本](#当前引用外部依赖的版本)
    * [当前已支持的服务](#当前已支持的服务)
      * [文件（对象存储）服务](#文件（对象存储）服务)
      * [文档服务](#文档服务)
  * [手册](#手册)
    * [文件（对象存储）服务](#文件（对象存储）服务-1)
      * [配置](#配置)
      * [使用例子](#使用例子)
      * [api介绍](#api介绍)
    * [文档服务](#文档服务-1)
      * [excel服务](#excel服务)
        * [配置](#配置-1)
        * [使用例子](#使用例子-1)
        * [api介绍](#api介绍-1)


# xyz-support

## 介绍

对Spring Boot项目进行相关统合的支持

### 引入

确保当前仓库有该包，暂未录入公共仓库，自行放入。

```
<dependency>
    <groupId>com.xyz</groupId>
    <artifactId>xyz-support</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 当前引用Spring Boot版本

2.1.3.RELEASE

### 当前引用外部依赖的版本

- 谷歌guava：28.0-jre
- 谷歌gson：2.8.5
- 七牛云sdk：7.7.0
- okhttp：3.14.2
- commons-net：3.4
- commons-io：2.11.0
- poi：3.9

**提示**：maven仓库方式引用的话，上述外部依赖的版本若与本地不一致，以自身情况考量并存还是存其一； 若以普通jar本地引入，请注意引入上述依赖。或可对源码进行修改后运行无误后进行打包使用。

### 当前已支持的服务

#### 文件（对象存储）服务

- 本地存储已支持
- Ftp存储已支持
- 七牛云存储已支持
- ...

#### 文档服务

- excel服务
  - 使用poi（支持xls和xlsx操作）
- ...

## 手册

### 文件（对象存储）服务

#### 配置

**注意**：目前只支持Spring Boot的配置方式，即application.properties（application.yml）， 讲解将以application.properties进行（application.yml同理）。

```
# 文件服务的启动标志 true/false
xyz.support.file.enable=true

# local文件服务配置 list形式 严格从0下标递增，否则不识别，application.yml中直接-替代（推荐）
xyz.support.file.local...
# 举例local
# local服务名 必传（没有服务名配啥呢）
xyz.support.file.local[0].serviceName=xxx
# local指定的根路径 必传
xyz.support.file.local[0].localPath=xxx


# ftp文件服务配置 list形式 严格从0下标递增，否则不识别，application.yml中直接-替代（推荐）
xyz.support.file.ftp...
# 举例ftp
# ftp服务名 必传
xyz.support.file.ftp[0].serviceName=xxx
# ftp指定的host 不传默认为127.0.0.1
xyz.support.file.ftp[0].host=127.0.0.1
# ftp指定的port 不传默认为21
xyz.support.file.ftp[0].port=21
# ftp指定的timeout 不传默认为5000
xyz.support.file.ftp[0].timeout=5000
# ftp指定的username 必传
xyz.support.file.ftp[0].username=xxx
# ftp指定的password 必传
xyz.support.file.ftp[0].password=xxx


# 七牛云文件服务配置 list形式 严格从0下标递增，否则不识别，application.yml中直接-替代（推荐）
xyz.support.file.qiniu...
# 举例七牛云
# 七牛云服务名 必传
xyz.support.file.qiniu[0].serviceName=xxx
# 七牛云accessKey 必传
xyz.support.file.qiniu[0].accessKey=xxx
# 七牛云secretKey 必传
xyz.support.file.qiniu[0].secretKey=xxx
# 七牛云bucket（空间名） 必传
xyz.support.file.qiniu[0].bucket=xxx
# 七牛云domain（访问域名） 必传
xyz.support.file.qiniu[0].domain=xxx
# 七牛云publicFlag（空间公开标志 true/false） 默认为false
xyz.support.file.qiniu[0].publicFlag=false
```

#### 使用例子

假定进行了如下配置，以application.yml形式:

```
xyz:
  support:
    file:
      enable: true
      local:
        - serviceName: localFileService
          localPath: /localFile
```

若要对服务进行调用，业务服务中（按正常注入bean的方式即可）：

```
@Resource
private FileInterface localFileService;
```


#### api介绍

**更详细的解释见`FileInterface`**

| method | description|
| --- | --- |
| String upload(File file) | File的方式上传，文件名取本名，文件路径在当前服务根路径 |
| String upload(File file, String fileName) | File的方式上传，文件名取自定义，文件路径在当前服务根路径 |
| String upload(File file, String fileName, String filePath) | File的方式上传，文件名取自定义，文件路径也取自定义（相对于当前服务根路径） |
| String upload(MultipartFile file) | MultipartFile的方式上传，文件名取本名，文件路径在当前服务根路径 |
| String upload(MultipartFile file, String fileName) | MultipartFile的方式上传，文件名取自定义，文件路径在当前服务根路径 |
| String upload(MultipartFile file, String fileName, String filePath) | MultipartFile的方式上传，文件名取自定义，文件路径也取自定义（相对于当前服务根路径） |
| String upload(InputStream is, String fileName) | 流的方式上传，文件名取自定义，文件路径在当前服务根路径 |
| String upload(InputStream is, String fileName, String filePath) | 流的方式上传，文件名取自定义，文件路径也取自定义（相对于当前服务根路径） |
| File download(String fileName, String filePath) | 文件下载，提供File |
| byte[] download(String fileName) | 文件下载，提供字节数组 |



### 文档服务


#### excel服务

提供的服务包含`解析excel成对象`和`导出对象成excel`，支持常规性的excel内容识别（标准的一行行数据，不要出现隐藏行，使用时请注意），支持常规类型数据的导出（包括Date，专属注解支持）。

##### 配置

**注意**：目前只支持Spring Boot的配置方式，即application.properties（application.yml）， 讲解将以application.properties进行（application.yml同理）。

```
# excel服务的启动标志 true/false
xyz.support.document.excel.enable=true

# excel服务bean配置 list形式 严格从0下标递增，否则不识别，application.yml中直接-替代（推荐）
xyz.support.document.excel.bean...
# 服务名 必传
xyz.support.document.excel.bean[0].serviceName=xxx
# 服务的类型  poi、custom 除了custom自定义，其他类型将选用默认的对应实现 不传默认为poi，但不能传错
xyz.support.document.excel.bean[0].type=poi
# 自定义服务的全限定类名 type选custom时需指定（若无法加载，将会报错）
xyz.support.document.excel.bean[0].clazz=xxx
```

##### 使用例子


假定进行了如下配置，以application.yml形式:

```
xyz:
  support:
    document:
      excel:
        enable: true
        bean:
          - serviceName: excelService
            type: custom
            clazz: com.xyz.support.document.excel.poi.DefaultPoiExcelOperation
```

若要对服务进行调用，业务服务中（按正常注入bean的方式即可）：

```
@Resource
private ExcelOperation excelService;
```



#### api介绍

**更详细的解释见`ExcelOperation`**

| method | description|
| --- | --- |
| List<T> parse(File file, Class<T> resultType) | 解析excel到对应bean，忽略第一行数据（认为是头） |
| List<T> parse(File file, boolean filterFirstRow, Class<T> resultType) | 解析excel到对应bean，忽不忽略第一行由调用方决定 |
| List<T> parse(MultiFile file, Class<T> resultType) | 解析excel(MultipartFile)到对应bean，忽略第一行数据（认为是头） |
| List<T> parse(MultiFile file, boolean filterFirstRow, Class<T> resultType) | 解析excel(MultipartFile)到对应bean，忽不忽略第一行由调用方决定 |
| void export(File target, Class<T> dataType, List<T> dataList) | 导出数据生成excel，数据按bean并由对应规则解析出来，导出到本地文件 |
| void export(HttpServletResponse response, String fileName, Class<T> dataType, List<T> dataList) | 导出数据生成excel，数据按bean并由对应规则解析出来，通过网络导出 |
| void export(File target, SheetItem sheetItem) | 导出数据生成excel，数据通过通用赋值对象传递，导出到本地文件 |
| void export(HttpServletResponse response, String fileName, SheetItem sheetItem) | 导出数据生成excel，数据通过通用赋值对象传递，通过网络导出 |
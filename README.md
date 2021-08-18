# Table of Contents

* [xyz-support](#xyz-support)
  * [介绍](#介绍)
    * [当前引用Spring Boot版本](#当前引用spring-boot版本)
    * [当前引用外部依赖的版本](#当前引用外部依赖的版本)
    * [当前已支持的服务](#当前已支持的服务)
      * [文件（对象存储）服务](#文件（对象存储）服务)
  * [手册](#手册)
    * [文件（对象存储）服务](#文件（对象存储）服务-1)
      * [配置](#配置)
      * [使用例子](#使用例子)
      * [api介绍](#api介绍)


# xyz-support

## 介绍

对Spring Boot项目进行相关统合的支持

### 当前引用Spring Boot版本

2.1.3.RELEASE

### 当前引用外部依赖的版本

- 谷歌guava：28.0-jre
- 谷歌gson：2.8.5
- 七牛云sdk：7.7.0
- okhttp：3.14.2
- commons-net：3.4
- commons-io：2.11.0

**提示**：以starter方式引用的话，上述外部依赖的版本若与本地不一致，以自身情况考量并存还是存其一； 若以普通jar引入，请注意引入上述依赖。或可对源码进行修改后运行无误后进行打包使用。

### 当前已支持的服务

#### 文件（对象存储）服务

- 本地存储已支持
- Ftp存储已支持
- 七牛云存储已支持
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
| File download(String fileName, String filePath) | 文件下载，提供File |
| byte[] download(String fileName) | 文件下载，提供字节数组 |

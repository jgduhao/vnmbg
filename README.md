# vnmbg 仿nmb
- 非骂人
- 为学习和熟悉 ~~groovy~~ vertx mongodb RESTful api
- 完成进度：
    - 完成板块部分crud api 以及发串人部分crud的service
    - 已改为完全使用java，修改了build.gradle文件
    - 增加了service部分的junit测试

## api
- 查询所有板块 GET /nmb/boards
- 新增板块 POST /nmb/boards
- 维护板块信息 PATCH /nmb/boards/:boardSign
- 删除板块 DELETE /nmb/boards/:boardSign

## 数据存储结构
- 数据集合：board 板块
- 文档字段:

    字段英文|字段中文|类型|备注  
    -|-|-|- 
    name|板块名称|String|例:综合
    boardSign|板块英文标志|String|例:comp 不可重复
    order|板块排序|String|例:0010
    available|是否开放|Boolean|
    readonly|是否只读|Boolean|
    createTime|创建时间|String|格式:yyyyMMddHHssmm
    updateTime|修改时间|String|格式:yyyyMMddHHssmm
    operator|修改人|String|
    
    
 - 数据集合: poster 发串人
 - 文档字段
 
     字段英文|字段中文|类型|备注  
     -|-|-|- 
     posterSign|发串人标志（饼干）|String|由大小写字母和数字随机生成的 七位
     available|是否可用|Boolean|
     createTime|创建时间|String|格式:yyyyMMddHHssmm
     expiredTime|失效时间|String|格式:yyyyMMddHHssmm 默认为创建后三个月
     
     
     
# vnmbg 仿nmb
- 非骂人
- 为学习和熟悉groovy vertx mongodb RESTful api
- 完成进度：仅完成板块部分crud api

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
    available|是否开放|boolean|
    readonly|是否只读|boolean|
    createTime|创建时间|String|格式:yyyyMMddHHssmm
    updateTime|修改时间|String|格式:yyyyMMddHHssmm
    operator|修改人|String|
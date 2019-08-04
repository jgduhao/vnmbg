# vnmbg 仿nmb
- 非骂人
- 为学习和熟悉 ~~groovy~~ vertx mongodb RESTful api
- 完成进度：
    - 完成板块部分crud api 以及发串人部分crud的service
    - 已改为完全使用java，修改了build.gradle文件
    - 增加了service部分的junit测试
    - 8/4 20:04 完成了串部分Service以及部分单元测试，完成了发串、回复、分页查询串以及分页查询回复的api

## api
- 查询所有板块 GET /nmb/boards
- 新增板块 POST /nmb/boards
- 维护板块信息 PATCH /nmb/boards/:boardSign
- 删除板块 DELETE /nmb/boards/:boardSign
- 发新串 POST /nmb/posts
- 分页查询串 GET /nmb/posts
- 新回复 POST /nmb/replys
- 分页查询回复 POST /nmb/replys

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
     

- 数据集合: post 串
- 文档字段

     字段英文|字段中文|类型|备注  
     -|-|-|- 
     postId|串ID|String|mongodb _id
     postNo|串号|String|自增，8位
     boardSign|串所属板块|String|
     posterSign|发串人标志|String|
     replyPostNo|回复所属主串|String|回复的串
     content|内容|String|
     available|是否可见|Boolean|
     sage|是否SAGE|Boolean|SAGE后回复不被顶至板块顶部
     createTime|创建时间|String|格式:yyyyMMddHHssmmSSS
     updateTime|修改时间(最近回复时间)|String|格式:yyyyMMddHHssmmSSS
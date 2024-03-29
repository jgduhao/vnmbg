package consts;

public enum Error {

    BoardNotFound("目前没有可用板块",404),
    PostNotFound("暂无内容",404),
    ReplyNotFound("暂无回复",404),
    BoardExists("板块已经存在",400),
    FieldEmpty("字段{0}不能为空",400),
    CannotChange("不能修改{0}",400),
    SystemError("系统暂不可用",503),
    noPosterSign("没有可用饼干，请先获取饼干",401),
    noBoard("无此板块",400),
    noPost("无此串",400);

    private String errMsg;
    private int errCode;

    Error(String errMsg, int errCode){
        this.errMsg = errMsg;
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public int getErrCode() {
        return errCode;
    }
}

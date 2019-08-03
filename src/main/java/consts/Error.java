package consts;

public enum Error {

    BoardNotFound("目前没有可用板块",404),
    BoardExists("板块已经存在",400),
    FieldEmpty("字段{0}不能为空",400),
    CannotChangeBoardSign("不能修改boardSign",400),
    SystemError("系统暂不可用",503);

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

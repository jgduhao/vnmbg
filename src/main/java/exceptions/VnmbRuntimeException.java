package exceptions;

import consts.Error;
import utils.StringUtil;

public class VnmbRuntimeException extends RuntimeException {

    Error err;

    public VnmbRuntimeException(Error e){
        super(e.getErrMsg());
        this.err = e;
    }

    public VnmbRuntimeException(Error e,String...args){
        super(StringUtil.getMessage(e.getErrMsg(),args));
        this.err = e;
    }

    public int getErrCode(){
        return this.err.getErrCode();
    }

}

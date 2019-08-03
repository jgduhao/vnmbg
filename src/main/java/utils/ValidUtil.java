package utils;

import consts.Error;
import exceptions.VnmbRuntimeException;

public class ValidUtil {

    public static void validEmpty(String name,Object object){
        if(object == null){
            throw new VnmbRuntimeException(Error.FieldEmpty,name);
        }
        if(object instanceof String){
            if("".equals((String)object)){
                throw new VnmbRuntimeException(Error.FieldEmpty,name);
            }
        }
    }

}

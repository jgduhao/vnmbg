package utils;

import consts.Error;
import exceptions.VnmbRuntimeException;

public class ValidUtil {

    public static void validEmpty(String name,Object object){
        if(object == null){
            throw new VnmbRuntimeException(Error.FieldEmpty,name);
        }
        if(object instanceof String){
            if("".equals(object)){
                throw new VnmbRuntimeException(Error.FieldEmpty,name);
            }
        }
    }

    public static <T> T getValueOrDefault(T object,T def){
        if(object == null){
            return def;
        }
        if(object instanceof String){
            if("".equals(object)){
                return def;
            }
        }
        return object;
    }

}

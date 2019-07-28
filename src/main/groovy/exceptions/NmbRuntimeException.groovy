package exceptions

class NmbRuntimeException extends RuntimeException{

    String errType

    NmbRuntimeException(String errType,String errMsg){
        super(errMsg)
        this.errType = errType
    }
}

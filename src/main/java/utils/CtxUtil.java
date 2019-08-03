package utils;

import consts.Error;
import exceptions.VnmbRuntimeException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class CtxUtil {

    public static void showResult(RoutingContext ctx, int httpCode, String body){
        ctx.response().setStatusCode(httpCode)
                      .putHeader("content-type", "application/json")
                      .end(body);
    }

    public static void showErrorResult(RoutingContext ctx, Throwable e){
        String message = e.getMessage();
        if(e instanceof VnmbRuntimeException){
            int httpCode = ((VnmbRuntimeException) e).getErrCode();
            showResult(ctx, httpCode, new JsonObject().put("message",message).encode());
        } else {
            showErrorResult(ctx, new VnmbRuntimeException(Error.SystemError));
        }
    }

}

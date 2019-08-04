package verticle;

import consts.API;
import consts.Error;
import entity.Board;
import exceptions.VnmbRuntimeException;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import service.VnmbBoardService;
import utils.CtxUtil;

import java.util.HashSet;
import java.util.Set;

public class VnmbVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(VnmbVerticle.class);

    private VnmbBoardService boardService;

    @Override
    public void start() throws Exception {
        System.out.println(config());
        int port = Integer.parseInt(config().getString("nmb_http_port"));
        Router router = Router.router(vertx);

        boardService = new VnmbBoardService(vertx, config());

        Set<String> allowHeaders = new HashSet<>();
        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");

        Set<HttpMethod> allowMethods = new HashSet<>();
        allowMethods.add(HttpMethod.GET);
        allowMethods.add(HttpMethod.POST);
        allowMethods.add(HttpMethod.PATCH);
        allowMethods.add(HttpMethod.DELETE);

        router.route().handler(
                CorsHandler.create("*")
                .allowedHeaders(allowHeaders)
                .allowedMethods(allowMethods))
                .handler(BodyHandler.create());

        router.get(API.API_QUERY_BOARDS).handler(this::queryAllBoards);
        router.post(API.API_ADD_BOARDS).handler(this::addBoard);
        router.patch(API.API_UPDATE_BOARDS).handler(this::updateBoard);
        router.delete(API.API_DELETE_BOARDS).handler(this::deleteBoard);

        vertx.createHttpServer().requestHandler(router).listen(port);

        logger.info("Server started on port "+port);

    }

    private void queryAllBoards(RoutingContext ctx){
        try{
            Boolean available = ctx.getBodyAsJson().getBoolean(Board.Fields.available);
            boardService.getAll(available).setHandler(res -> {
                if(res.succeeded()){
                    JsonArray rst = new JsonArray();
                    for(Board board : res.result()){
                        rst.add(board.toJsonObject());
                    }
                    if(rst.size() > 0){
                        CtxUtil.showResult(ctx,200,rst.encode());
                    } else {
                        CtxUtil.showErrorResult(ctx, new VnmbRuntimeException(Error.BoardNotFound));
                    }
                } else {
                    logger.error("板块查询异常",res.cause());
                    CtxUtil.showErrorResult(ctx, res.cause());
                }
            });
        } catch (Exception e){
            logger.error("板块查询异常",e);
            CtxUtil.showErrorResult(ctx, e);
        }
    }

    private void addBoard(RoutingContext ctx){
        try{
            Board board = new Board(ctx.getBodyAsJson());
            boardService.getOne(board.getBoardSign()).compose(getOneResult -> {
                if(getOneResult == null){
                    return boardService.addOne(board);
                } else {
                    return Future.failedFuture(new VnmbRuntimeException(Error.BoardExists));
                }
            }).setHandler(res -> {
                if(res.succeeded()){
                    CtxUtil.showResult(ctx, 201, res.result().toJsonObject().encode());
                } else {
                    logger.error("板块添加异常", res.cause());
                    CtxUtil.showErrorResult(ctx, res.cause());
                }
            });
        } catch (Exception e){
            logger.error("板块添加异常",e);
            CtxUtil.showErrorResult(ctx, e);
        }
    }

    private void updateBoard(RoutingContext ctx){
        try{
            String boardSign = ctx.request().getParam(Board.Fields.boardSign);
            Board board = new Board(ctx.getBodyAsJson());
            boardService.updateOne(boardSign,board).setHandler(res -> {
                if(res.succeeded()){
                    CtxUtil.showResult(ctx, 200,res.result().toJsonObject().encode());
                } else {
                    logger.error("板块更新异常",res.cause());
                    CtxUtil.showErrorResult(ctx, res.cause());
                }
            });
        } catch (Exception e){
            logger.error("板块更新异常",e);
            CtxUtil.showErrorResult(ctx, e);
        }
    }

    private void deleteBoard(RoutingContext ctx){
        try{
            String boardSign = ctx.request().getParam(Board.Fields.boardSign);
            boardService.deleteOne(boardSign).setHandler(res -> {
                if(res.succeeded()){
                    CtxUtil.showResult(ctx, 204, new JsonObject().encode());
                } else {
                    logger.error("板块删除异常",res.cause());
                    CtxUtil.showErrorResult(ctx, res.cause());
                }
            });
        } catch (Exception e){
            logger.error("板块删除异常",e);
            CtxUtil.showErrorResult(ctx, e);
        }
    }
}

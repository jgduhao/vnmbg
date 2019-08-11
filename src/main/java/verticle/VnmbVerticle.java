package verticle;

import consts.API;
import consts.Error;
import consts.Mongo;
import entity.Board;
import entity.Post;
import exceptions.VnmbRuntimeException;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.CorsHandler;
import service.VnmbBoardService;
import service.VnmbPostService;
import service.VnmbPosterService;
import utils.CtxUtil;
import utils.ValidUtil;

import java.util.HashSet;
import java.util.Set;

public class VnmbVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(VnmbVerticle.class);

    private VnmbBoardService boardService;
    private VnmbPostService postService;
    private VnmbPosterService posterService;

    @Override
    public void start() throws Exception {
        System.out.println(config());
        int port = Integer.parseInt(config().getString("nmb_http_port"));
        Router router = Router.router(vertx);

        boardService = new VnmbBoardService(vertx, config());
        postService = new VnmbPostService(vertx, config());
        posterService = new VnmbPosterService(vertx, config());

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
                .handler(BodyHandler.create())
                .handler(CookieHandler.create());

        router.get(API.API_QUERY_BOARDS).handler(this::queryAllBoards);
        router.post(API.API_ADD_BOARDS).handler(this::addBoard);
        router.patch(API.API_UPDATE_BOARDS).handler(this::updateBoard);
        router.delete(API.API_DELETE_BOARDS).handler(this::deleteBoard);
        router.get(API.API_QUERY_POSTS_IN_BOARD).handler(this::queryPostsInBoard);
        router.get(API.API_QUERY_REPLY_IN_POST).handler(this::queryReplysInPost);
        router.post(API.API_ADD_POSTS).handler(this::addPost);
        router.post(API.API_REPLY_POSTS).handler(this::replyPost);

        vertx.createHttpServer().requestHandler(router).listen(port);

        logger.info("Server started on port "+port);

    }

    private void addPost(RoutingContext ctx){
        try{
            String boardSign = ctx.request().getParam(Post.Fields.boardSign);
            Cookie cookie = ctx.getCookie(API.POSTER_COOKIE_NAME);
            String posterSign = cookie == null ? "" : ValidUtil.getValueOrDefault(cookie.getValue(),"");
            JsonObject reqJson = ctx.getBodyAsJson();
            String content = reqJson.getString(Post.Fields.content);
            if(posterSign == null || posterSign.equals("")){
                CtxUtil.showErrorResult(ctx, new VnmbRuntimeException(Error.noPosterSign));
            } else {
                posterService.getOne(posterSign).compose(poster -> {
                    if(poster == null){
                        return Future.failedFuture(new VnmbRuntimeException(Error.noPosterSign));
                    } else {
                        return boardService.getOne(boardSign);
                    }
                }).compose(board -> {
                    if(board == null){
                        return Future.failedFuture(new VnmbRuntimeException(Error.noBoard));
                    } else {
                        return postService.addPost(boardSign,posterSign,content);
                    }
                }).setHandler(res -> {
                    if(res.succeeded()){
                        CtxUtil.showResult(ctx,201,res.result().toJsonObject().encode());
                    } else {
                        logger.error("发新串异常",res.cause());
                        CtxUtil.showErrorResult(ctx, res.cause());
                    }
                });
            }
        } catch (Exception e){
            logger.error("发新串异常",e);
            CtxUtil.showErrorResult(ctx, e);
        }
    }

    private void replyPost(RoutingContext ctx){
        try{
            String postNo = ctx.request().getParam(Post.Fields.postNo);
            Cookie cookie = ctx.getCookie(API.POSTER_COOKIE_NAME);
            String posterSign = cookie == null ? "" : ValidUtil.getValueOrDefault(cookie.getValue(),"");
            JsonObject reqJson = ctx.getBodyAsJson();
            String content = reqJson.getString(Post.Fields.content);
            posterService.getOrCreateOne(posterSign).compose(poster ->{
                ctx.addCookie(Cookie.cookie(API.POSTER_COOKIE_NAME,poster.getPosterSign()));
                return postService.getOne(postNo);
            }).compose(post -> {
                if(post == null){
                    return Future.failedFuture(new VnmbRuntimeException(Error.noPost));
                } else {
                    String newPosterSign = ctx.getCookie(API.POSTER_COOKIE_NAME).getValue();
                    return postService.addPostReply(post,newPosterSign,content);
                }
            }).setHandler(res -> {
                if(res.succeeded()){
                    CtxUtil.showResult(ctx,201,res.result().toJsonObject().encode());
                } else {
                    logger.error("回复异常", res.cause());
                    CtxUtil.showErrorResult(ctx, res.cause());
                }
            });
        } catch (Exception e){
            logger.error("回复异常",e);
            CtxUtil.showErrorResult(ctx, e);
        }
    }

    private void queryPostsInBoard(RoutingContext ctx){
        try {
            String boardSign = ctx.request().getParam(Post.Fields.boardSign);
            JsonObject reqJson = ctx.getBodyAsJson();
            Integer pageSize = reqJson.getInteger(API.FIELD_PAGE_SIZE,20);
            String lasPostUpdTime = reqJson.getString(API.FIELD_LAST_POST_UPD_TIME,"");
            Integer sort = reqJson.getInteger(API.FIELD_SORT, Mongo.DESC);
            postService.postPageQuery(boardSign,pageSize,lasPostUpdTime ,sort,true).setHandler(res -> {
                if(res.succeeded()){
                    JsonArray rst = new JsonArray();
                    for(Post post : res.result()){
                        rst.add(post.toJsonObject());
                    }
                    if(rst.size() > 0){
                        CtxUtil.showResult(ctx,200,rst.encode());
                    } else {
                        CtxUtil.showErrorResult(ctx, new VnmbRuntimeException(Error.PostNotFound));
                    }
                } else {
                    logger.error("串查询异常", res.cause());
                    CtxUtil.showErrorResult(ctx, res.cause());
                }
            });
        } catch (Exception e){
            logger.error("串查询异常",e);
            CtxUtil.showErrorResult(ctx, e);
        }
    }

    private void queryReplysInPost(RoutingContext ctx){
        try{
            String postNo = ctx.request().getParam(Post.Fields.postNo);
            JsonObject reqJson = ctx.getBodyAsJson();
            Integer pageSize = reqJson.getInteger(API.FIELD_PAGE_SIZE,20);
            String lastPostNo = reqJson.getString(API.FIELD_LAST_POST_NO,"");
            Integer sort = reqJson.getInteger(API.FIELD_SORT, Mongo.ASC);
            postService.postReplyPageQuery(postNo,pageSize,lastPostNo,sort,true).setHandler(res -> {
                if(res.succeeded()){
                    JsonArray rst = new JsonArray();
                    for(Post post : res.result()){
                        rst.add(post.toJsonObject());
                    }
                    if(rst.size() > 0){
                        CtxUtil.showResult(ctx,200,rst.encode());
                    } else {
                        CtxUtil.showErrorResult(ctx, new VnmbRuntimeException(Error.ReplyNotFound));
                    }
                } else {
                    logger.error("回复查询异常", res.cause());
                    CtxUtil.showErrorResult(ctx, res.cause());
                }
            });
        } catch (Exception e){
            logger.error("回复查询异常",e);
            CtxUtil.showErrorResult(ctx, e);
        }
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

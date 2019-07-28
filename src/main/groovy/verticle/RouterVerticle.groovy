package verticle

import consts.ErrTypes
import consts.Routes
import exceptions.NmbRuntimeException
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import service.BoardService

class RouterVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(RouterVerticle.class);

    BoardService boardService;

    void start(){
        boardService = new BoardService(vertx,config())
        def router = Router.router(vertx)
        Set<String> allowHeaders = ['x-requested-with',
                                    'Access-Control-Allow-Origin',
                                    'origin',
                                    'Content-Type',
                                    'accept']
        Set<HttpMethod> allowMethods = [HttpMethod.GET,
                                        HttpMethod.POST,
                                        HttpMethod.DELETE,
                                        HttpMethod.PATCH]

        router.route().handler(CorsHandler.create("*")
                .allowedHeaders(allowHeaders).allowedMethods(allowMethods))
                .handler(BodyHandler.create())

        router.get(Routes.QUERY_BOARDS).handler{ctx -> queryAllBoards(ctx)}
        router.post(Routes.ADD_BOARDS).handler{ctx -> addBoard(ctx)}
        router.patch(Routes.UPDATE_BOARDS).handler{ctx -> updateBoard(ctx)}
        router.delete(Routes.DELETE_BOARDS).handler{ctx -> deleteBoard(ctx)}

        def port = 8090
        vertx.createHttpServer().requestHandler(router).listen(port)
        logger.info "server started on port $port"
    }

    def queryAllBoards(RoutingContext ctx){
        def query = new JsonObject(ctx.getBodyAsString())
        boardService.getAll(query).setHandler{ res ->
            if(res.succeeded()){
                if(res.result() == null){
                    showErr(ctx,new NmbRuntimeException('resNotFound','no board now'))
                } else {
                    showRst(ctx,res.result(),200)
                }
            } else {
                showErr(ctx,res.cause())
            }
        }

    }

    def addBoard(RoutingContext ctx){
        try{
            def board = new JsonObject(ctx.getBodyAsString())
            boardService.getOne(board.getString('boardSign')).compose{ getOneRst ->
                if(getOneRst == null){
                    return boardService.add(board)
                } else {
                    return Future.failedFuture(new NmbRuntimeException('resExists','board already exists!'))
                }
            }.setHandler{ res ->
                if(res.succeeded()){
                    showRst(ctx,res.result(),201)
                } else {
                    showErr(ctx,res.cause())
                }
            }
        } catch(e){
            showErr(ctx,e)
        }
    }

    def updateBoard(RoutingContext ctx){
        try{
            def boardSign = ctx.request().getParam('boardSign')
            def board = new JsonObject(ctx.getBodyAsString())
            board.remove('_id')
            boardService.updateOne(boardSign,board).setHandler{res ->
                if(res.succeeded()){
                    showRst(ctx,res.result(),200)
                } else {
                    showErr(ctx,res.cause())
                }
            }
        } catch(e){
            showErr(ctx,e)
        }

    }

    def deleteBoard(RoutingContext ctx){
        try{
            def boardSign = ctx.request().getParam('boardSign')
            boardService.deleteOne(boardSign).setHandler{ res ->
                if(res.succeeded()){
                    showRst(ctx,'',204)
                } else {
                    showErr(ctx,res.cause())
                }
            }
        } catch(e){
            showErr(ctx,e)
        }
    }

    def showRst(ctx,rst,succCode){
        ctx.response().setStatusCode(succCode)
                .putHeader("content-type", "application/json")
                .end(rst)
    }

    def showErr(ctx,err){
        def httpCode = 503
        if(err in NmbRuntimeException){
           httpCode = ErrTypes.errWithCode[err.errType]
           logger.info err
        } else {
            logger.error err
        }
        ctx.response().setStatusCode(httpCode)
                .putHeader("content-type", "application/json")
                .end(new JsonObject(['message':err.message]).encode())
    }

}

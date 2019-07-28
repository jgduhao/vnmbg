package service

import exceptions.NmbRuntimeException
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.mongo.FindOptions
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.mongo.UpdateOptions

class BoardService {

    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);

    private final Vertx vertx;
    private final MongoClient mongo;

    BoardService(Vertx vertx,JsonObject config){
        this.vertx = vertx;
        this.mongo = MongoClient.createShared(vertx,config)
    }

    def getAll(JsonObject query){
        return Future.future{ promise ->
            if(query.getBoolean('available') == null){
                query.put('available',true)
            }
            logger.debug "query: $query"
            def options = new FindOptions()
            options.fields = new JsonObject(['name':true,'boardSign':true,])
            options.sort = new JsonObject(['order':1])
            mongo.findWithOptions('board',query,options){ res ->
                if(res.succeeded()){
                    if(res.result() != null && res.result() != []){
                        def jsonArr = new JsonArray()
                        res.result().each { it -> jsonArr.add it}
                        promise.complete(jsonArr.encode())
                    } else {
                        promise.complete(null)
                    }
                } else {
                    promise.fail(res.cause())
                }
            }
        }
    }

    def add(JsonObject board){
        return Future.future{ promise ->
            checkJsonObjFields(board,['name','boardSign','order','available','readonly',],['_id'])
            board.put('createTime',getDateTime())
            board.put('updateTime',getDateTime())
            logger.info "insert: $board"
            mongo.save('board',board){ res ->
                if(res.succeeded()){
                    promise.complete(board.put("_id",res.result()).encode())
                } else {
                    promise.fail(res.cause())
                }
            }
        }
    }

    def getOne(String boardSign){
        return Future.future{ promise ->
            def query = new JsonObject(['boardSign' : boardSign])
            checkJsonObjFields(query,['boardSign'],['_id'])
            mongo.findOne('board',query,null){ res ->
                if(res.succeeded()){
                    if(res.result() == null || res.result().isEmpty()){
                        promise.tryComplete(null)
                    }
                    promise.tryComplete(res.result())
                } else {
                    promise.tryFail(res.cause())
                }
            }
        }
    }

    def updateOne(String boardSign,JsonObject board){
        return Future.future{ promise ->
            def query = new JsonObject(['boardSign' : boardSign])
            checkJsonObjFields(query,['boardSign'],['_id'])
            board.put('updateTime',getDateTime())
            def update = new JsonObject(['$set':board])
            def option = new UpdateOptions();
            option.returningNewDocument = true;
            logger.info "update: $query by $board"
            mongo.findOneAndUpdateWithOptions('board',query,update,new FindOptions(),option){ res ->
                if(res.succeeded()){
                    promise.tryComplete(new JsonObject(res.result()).encode())
                } else {
                    promise.tryFail(res.cause())
                }
            }
        }
    }

    def deleteOne(String boardSign){
        return Future.future{ promise ->
            def query = new JsonObject(['boardSign' : boardSign])
            checkJsonObjFields(query,['boardSign'],['_id'])
            logger.info "delete: $query"
            mongo.findOneAndDelete('board',query){ res ->
                if(res.succeeded()){
                    promise.tryComplete()
                } else {
                    promise.tryFail(res.cause())
                }
            }
        }
    }

    def checkJsonObjFields(json,needFields,removeFields){
        def jsonMap = json.getMap()
        needFields.each { name ->
            if(!jsonMap.containsKey(name) || jsonMap[name] == null || (jsonMap[name] in String && jsonMap[name] == '')){
                logger.info "field ${name} is empty"
                throw new NmbRuntimeException('fieldEmpty',"field ${name} is empty")
            }
        }
        removeFields.each { name ->
            json.remove(name)
        }
    }

    def getDateTime(){
        return new Date().format('yyyyMMddHHmmss')
    }



}

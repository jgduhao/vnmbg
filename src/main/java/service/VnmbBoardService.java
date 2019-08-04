package service;

import consts.Error;
import consts.Mongo;
import entity.Board;
import exceptions.VnmbRuntimeException;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import utils.DateUtil;
import utils.ValidUtil;

import java.util.ArrayList;
import java.util.List;

public class VnmbBoardService {

    private static final Logger logger = LoggerFactory.getLogger(VnmbBoardService.class);

    private final MongoClient mongo;
    private static final String collection = Mongo.BOARD_COLLECTION;

    public VnmbBoardService(Vertx vertx, JsonObject config){
        this.mongo = MongoClient.createShared(vertx,config);
    }

    public Future<List<Board>> getAll(Boolean available){
        return Future.future(promise -> {
            JsonObject query = new JsonObject();
            query.put(Board.Fields.available,ValidUtil.getValueOrDefault(available,true));
            logger.info("query:"+query);
            FindOptions options = new FindOptions();
            options.setFields(new JsonObject().put(Board.Fields.name, true)
                                              .put(Board.Fields.boardSign, true));
            options.setSort(new JsonObject().put(Board.Fields.order, Mongo.ASC));
            mongo.findWithOptions(collection,query,options,res -> {
                if(res.succeeded()){
                    List<Board> boards = new ArrayList<>();
                    logger.info("rst:"+res.result());
                    if(res.result() != null && res.result().size() > 0){
                        for(JsonObject obj : res.result()){
                            boards.add(new Board(obj));
                        }
                    }
                    promise.tryComplete(boards);
                } else {
                    promise.tryFail(res.cause());
                }
            });
        });
    }

    public Future<Board> getOne(String boardSign){
        return Future.future(promise -> {
            ValidUtil.validEmpty(Board.Fields.boardSign,boardSign);
            JsonObject query = new JsonObject().put(Board.Fields.boardSign,boardSign);
            logger.info("queryOne:"+query);
            mongo.findOne(collection, query,null, res ->{
                if(res.succeeded()){
                    logger.info("rst:"+res.result());
                    if(res.result() == null || res.result().isEmpty()){
                        promise.tryComplete(null);
                    } else {
                        promise.tryComplete(new Board(res.result()));
                    }
                } else {
                    promise.tryFail(res.cause());
                }
            });
        });
    }

    public Future<Board> addOne(Board board){
        return Future.future(promise -> {
            ValidUtil.validEmpty(Board.Fields.name,board.getName());
            ValidUtil.validEmpty(Board.Fields.boardSign,board.getBoardSign());
            ValidUtil.validEmpty(Board.Fields.order,board.getOrder());
            ValidUtil.validEmpty(Board.Fields.available,board.getAvailable());
            ValidUtil.validEmpty(Board.Fields.readonly,board.getReadonly());
            JsonObject saveBoard = board.toJsonObject()
                                          .put(Board.Fields.createTime, DateUtil.getDateTime())
                                          .put(Board.Fields.updateTime, DateUtil.getDateTime());
            logger.info("save:"+saveBoard);
            mongo.save(collection, saveBoard, res -> {
                if(res.succeeded()){
                    promise.tryComplete(new Board(saveBoard));
                } else {
                    promise.tryFail(res.cause());
                }
            });
        });
    }

    public Future<Board> updateOne(String boardSign,Board board){
        return Future.future(promise -> {
            ValidUtil.validEmpty(Board.Fields.boardSign,boardSign);
            if(board.getBoardSign() != null && !boardSign.equals(board.getBoardSign())){
                throw new VnmbRuntimeException(Error.CannotChange,Board.Fields.boardSign);
            }
            JsonObject query = new JsonObject().put(Board.Fields.boardSign,boardSign);
            JsonObject update = new JsonObject()
                    .put(Mongo.SET, board.toJsonObject()
                                      .put(Board.Fields.updateTime,DateUtil.getDateTime()));
            UpdateOptions options = new UpdateOptions();
            options.setReturningNewDocument(true);
            logger.info("update: "+query+" by: "+update);
            mongo.findOneAndUpdateWithOptions(collection,query,update,new FindOptions(),options,res -> {
                if(res.succeeded()){
                    promise.tryComplete(new Board(res.result()));
                } else {
                    promise.tryFail(res.cause());
                }
            });
        });
    }

    public Future<Void> deleteOne(String boardSign){
        return Future.future(promise -> {
            ValidUtil.validEmpty(Board.Fields.boardSign,boardSign);
            JsonObject query = new JsonObject().put(Board.Fields.boardSign,boardSign);
            logger.info("delete:"+query);
            mongo.findOneAndDelete(collection,query,res -> {
                if(res.succeeded()){
                    promise.tryComplete();
                } else {
                    promise.tryFail(res.cause());
                }
            });
        });
    }
}

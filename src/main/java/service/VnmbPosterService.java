package service;

import entity.Poster;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import utils.DateUtil;
import utils.StringUtil;
import utils.ValidUtil;

import java.util.Date;

public class VnmbPosterService {

    private static final Logger logger = LoggerFactory.getLogger(VnmbPosterService.class);

    private final MongoClient mongo;
    private static final String collection = "poster";

    public VnmbPosterService(Vertx vertx, JsonObject config){
        this.mongo = MongoClient.createShared(vertx,config);
    }

    public Future<Poster> getOne(String posterSign){
        return Future.future(promise -> {
            ValidUtil.validEmpty(Poster.Fields.posterSign,posterSign);
            JsonObject query = new JsonObject().put(Poster.Fields.posterSign,posterSign)
                                               .put(Poster.Fields.expiredTime,new JsonObject()
                                                       .put("$gt", DateUtil.getDateTime()));
            logger.info("queryOne:"+query);
            mongo.findOne(collection, query,null, res ->{
                if(res.succeeded()){
                    logger.info("rst:"+res.result());
                    if(res.result() == null || res.result().isEmpty()){
                        promise.tryComplete(null);
                    } else {
                        promise.tryComplete(new Poster(res.result()));
                    }
                } else {
                    promise.tryFail(res.cause());
                }
            });
        });
    }

    public Future<Poster> addOne(){
        return Future.future(promise -> {
            Poster poster = new Poster();
            poster.setPosterSign(StringUtil.randomPosterSign());
            poster.setAvailable(true);
            poster.setCreateTime(DateUtil.getDateTime());
            poster.setExpiredTime(DateUtil.getDateAfterMonths(new Date(),3));
            JsonObject savePoster = poster.toJsonObject();
            logger.info("save:"+savePoster);
            mongo.save(collection,savePoster,res -> {
                if(res.succeeded()){
                    promise.tryComplete(poster);
                } else {
                    promise.tryFail(res.cause());
                }
            });
        });
    }

    public Future<Poster> updateOne(String posterSign,Boolean available){
        return Future.future(promise -> {
            ValidUtil.validEmpty(Poster.Fields.posterSign,posterSign);
            ValidUtil.validEmpty(Poster.Fields.available,available);
            JsonObject query = new JsonObject().put(Poster.Fields.posterSign,posterSign);
            JsonObject update = new JsonObject().put("$set",new JsonObject().put(Poster.Fields.available,available));
            UpdateOptions options = new UpdateOptions();
            options.setReturningNewDocument(true);
            logger.info("update: "+query+" by: "+update);
            mongo.findOneAndUpdateWithOptions(collection,query,update,new FindOptions(),options, res -> {
                if(res.succeeded()){
                    promise.tryComplete(new Poster(res.result()));
                } else {
                    promise.tryFail(res.cause());
                }
            });
        });
    }

    public Future<Void> deleteOne(String posterSign){
        return Future.future(promise -> {
            ValidUtil.validEmpty(Poster.Fields.posterSign,posterSign);
            JsonObject query = new JsonObject().put(Poster.Fields.posterSign,posterSign);
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

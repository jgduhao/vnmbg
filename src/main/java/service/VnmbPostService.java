package service;

import consts.API;
import consts.Error;
import consts.Mongo;
import entity.Post;
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
import utils.StringUtil;
import utils.ValidUtil;

import java.util.ArrayList;
import java.util.List;

public class VnmbPostService {

    private static final Logger logger = LoggerFactory.getLogger(VnmbPostService.class);

    private final MongoClient mongo;
    private static final String collection = Mongo.POST_COLLECTION;
    private static final String seq_collection = Mongo.SEQ_COLLECTION;
    private static final String post_seq_key = Mongo.POST_SEQ_KEY;

    public VnmbPostService(Vertx vertx, JsonObject config){
        this.mongo = MongoClient.createShared(vertx,config);
    }

    public Future<String> getSeq(){
        return Future.future(promise -> {
            JsonObject update = new JsonObject().put(Mongo.INCREASE,new JsonObject().put(post_seq_key,1));
            UpdateOptions options = new UpdateOptions();
            options.setReturningNewDocument(true);
            mongo.findOneAndUpdateWithOptions(seq_collection,new JsonObject(),update,new FindOptions(),options, res -> {
                if(res.succeeded() && res.result() != null){
                    Integer seq = res.result().getInteger(post_seq_key);
                    logger.info("seq:"+seq);
                    if(seq == null){
                        promise.tryFail(new VnmbRuntimeException(Error.SystemError));
                    } else {
                        promise.tryComplete(StringUtil.leftPaddingZeroToEight(seq));
                    }
                } else {
                    promise.tryFail(res.cause());
                }
            });
        });
    }

    public Future<Post> getOne(String postNo){
        return Future.future(promise -> {
            ValidUtil.validEmpty(Post.Fields.postNo,postNo);
            JsonObject query = new JsonObject().put(Post.Fields.postNo,postNo);
            logger.info("queryOne:"+query);
            mongo.findOne(collection,query,null,res -> {
                if(res.succeeded()){
                    logger.info("rst:"+res.result());
                    if(res.result() == null || res.result().isEmpty()){
                        promise.tryComplete(null);
                    } else {
                        Post rstPost = new Post(res.result());
                        rstPost.setPostId(res.result().getString(Mongo.ID));
                        promise.tryComplete(rstPost);
                    }
                } else {
                    promise.tryFail(res.cause());
                }
            });
        });
    }

    public Future<Post> addPost(String boardSign, String posterSign, String content){
        return getSeq().compose(seq -> Future.future(promise -> {
            ValidUtil.validEmpty(Post.Fields.boardSign,boardSign);
            ValidUtil.validEmpty(Post.Fields.posterSign,posterSign);
            ValidUtil.validEmpty(Post.Fields.content,content);
            Post post = new Post();
            post.setPostNo(seq);
            post.setBoardSign(boardSign);
            post.setPosterSign(posterSign);
            post.setReplyPostNo("");
            post.setContent(content);
            post.setAvailable(true);
            post.setSage(false);
            post.setCreateTime(DateUtil.getDateTimeSSS());
            post.setUpdateTime(DateUtil.getDateTimeSSS());
            JsonObject savePost = post.toJsonObject();
            logger.info("save:"+savePost);
            mongo.save(collection,savePost,res -> {
                if(res.succeeded()){
                    promise.complete(post);
                } else {
                    promise.tryFail(res.cause());
                }
            });
        }));
    }

    public Future<Post> addPostReply(String boardSign,String replyPostNo,String posterSign,String content){
        ValidUtil.validEmpty(Post.Fields.boardSign,boardSign);
        ValidUtil.validEmpty(Post.Fields.replyPostNo,replyPostNo);
        ValidUtil.validEmpty(Post.Fields.posterSign,posterSign);
        ValidUtil.validEmpty(Post.Fields.content,content);
        Post mainPost = new Post();
        mainPost.setUpdateTime(DateUtil.getDateTimeSSS());
        return updateOne(replyPostNo,mainPost)
                .compose(mPost -> getSeq())
                .compose(seq -> Future.future(promise -> {
            Post replyPost = new Post();
            replyPost.setPostNo(seq);
            replyPost.setBoardSign(boardSign);
            replyPost.setPosterSign(posterSign);
            replyPost.setReplyPostNo(replyPostNo);
            replyPost.setContent(content);
            replyPost.setAvailable(true);
            replyPost.setSage(false);
            replyPost.setCreateTime(DateUtil.getDateTimeSSS());
            replyPost.setUpdateTime(DateUtil.getDateTimeSSS());
            JsonObject saveReplyPost = replyPost.toJsonObject();
            logger.info("save:"+saveReplyPost);
            mongo.save(collection,saveReplyPost,res -> {
                if(res.succeeded()){
                    promise.complete(replyPost);
                } else {
                    promise.tryFail(res.cause());
                }
            });
        }));
    }

    public Future<List<Post>> postPageQuery(String boardSign,Integer pageSize,String lastPostId,Integer sort,Boolean available){
        return Future.future(promise -> {
            ValidUtil.validEmpty(Post.Fields.boardSign,boardSign);
            ValidUtil.validEmpty(API.FIELD_PAGE_SIZE,pageSize);
            JsonObject query = new JsonObject()
                    .put(Post.Fields.boardSign,boardSign)
                    .put(Post.Fields.replyPostNo,"")
                    .put(Post.Fields.available,ValidUtil.getValueOrDefault(available,true));
            int pageSort = ValidUtil.getValueOrDefault(sort,Mongo.ASC);
            if(lastPostId != null && !lastPostId.equals("")){
                if(pageSort == Mongo.ASC){
                    query.put(Mongo.GREATER_THAN, new JsonObject().put(Mongo.ID,lastPostId));
                } else {
                    query.put(Mongo.LITTER_THAN, new JsonObject().put(Mongo.ID,lastPostId));
                }
            }
            FindOptions options = new FindOptions();
            options.setSort(new JsonObject().put(Post.Fields.updateTime,pageSort));
            options.setLimit(pageSize);
            mongo.findWithOptions(collection,query,options,res -> {
                if(res.succeeded()){
                    List<Post> posts = new ArrayList<>();
                    logger.info("rst:"+res.result());
                    if(res.result() != null && res.result().size() > 0){
                        for(JsonObject obj : res.result()){
                            Post onePost = new Post(obj);
                            onePost.setPostId(obj.getString(Mongo.ID));
                            posts.add(onePost);
                        }
                    }
                    promise.tryComplete(posts);
                } else {
                    promise.tryFail(res.cause());
                }
            });
        });
    }

    public Future<List<Post>> postReplyPageQuery(String postNo,Integer pageSize,String lastPostNo,Integer sort,Boolean available){
        return Future.future(promise -> {
            ValidUtil.validEmpty(Post.Fields.postNo,postNo);
            ValidUtil.validEmpty(API.FIELD_PAGE_SIZE,pageSize);
            JsonObject query = new JsonObject()
                    .put(Post.Fields.replyPostNo,postNo)
                    .put(Post.Fields.available,ValidUtil.getValueOrDefault(available,true));
            int pageSort = ValidUtil.getValueOrDefault(sort,Mongo.ASC);
            if(lastPostNo != null && !lastPostNo.equals("")){
                if(pageSort == Mongo.ASC){
                    query.put(Mongo.GREATER_THAN, new JsonObject().put(Post.Fields.postNo,lastPostNo));
                } else {
                    query.put(Mongo.LITTER_THAN, new JsonObject().put(Post.Fields.postNo,lastPostNo));
                }
            }
            FindOptions options = new FindOptions();
            options.setSort(new JsonObject().put(Post.Fields.postNo,pageSort));
            options.setLimit(pageSize);
            mongo.findWithOptions(collection,query,options,res -> {
                if(res.succeeded()){
                    List<Post> posts = new ArrayList<>();
                    logger.info("rst:"+res.result());
                    if(res.result() != null && res.result().size() > 0){
                        for(JsonObject obj : res.result()){
                            posts.add(new Post(obj));
                        }
                    }
                    promise.tryComplete(posts);
                } else {
                    promise.tryFail(res.cause());
                }
            });
        });
    }

    public Future<Post> updateOne(String postNo,Post post){
        return Future.future(promise -> {
            ValidUtil.validEmpty(Post.Fields.postNo,postNo);
            if(post.getPostNo() != null && !postNo.equals(post.getPostNo())){
                throw new VnmbRuntimeException(Error.CannotChange,Post.Fields.postNo);
            }
            JsonObject query = new JsonObject().put(Post.Fields.postNo,postNo);
            JsonObject update = new JsonObject().put(Mongo.SET, post.toJsonObject());
            UpdateOptions options = new UpdateOptions();
            options.setReturningNewDocument(true);
            mongo.findOneAndUpdateWithOptions(collection,query,update,new FindOptions(),options,res -> {
                if(res.succeeded()){
                    promise.tryComplete(new Post(res.result()));
                } else {
                    promise.tryFail(res.cause());
                }
            });
        });
    }

    public Future<Post> deleteOne(String postNo){
        return Future.future(promise -> {
            ValidUtil.validEmpty(Post.Fields.postNo,postNo);
            JsonObject query = new JsonObject().put(Post.Fields.postNo,postNo);
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
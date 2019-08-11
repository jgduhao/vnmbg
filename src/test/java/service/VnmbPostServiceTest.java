package service;

import consts.Mongo;
import entity.Post;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(VertxUnitRunner.class)
public class VnmbPostServiceTest {

    @Rule
    public RunTestOnContext rule = new RunTestOnContext();

    private Vertx vertx;
    private VnmbPostService postService;

    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        JsonObject options = new JsonObject()
                .put("connection_string","mongodb://localhost:27017")
                .put("db_name","nmb");
        postService = new VnmbPostService(vertx,options);
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void getSeq(TestContext context) {
        Async async = context.async();
        postService.getSeq().setHandler(context.asyncAssertSuccess(seq1 -> {
            context.assertNotNull(seq1,"seq1 is null");
            postService.getSeq().setHandler(context.asyncAssertSuccess(seq2 -> {
                context.assertNotNull(seq2,"seq2 is null");
                context.assertFalse(Integer.parseInt(seq1) >= Integer.parseInt(seq2),"seq 2 lager than seq1");
                async.complete();
            }));
        }));
        async.awaitSuccess(5000);
    }

    @Test
    public void addPost(TestContext context) {
        Async async = context.async();
        postService.addPost("comp1","WIW80OM","这是一个测试1，如果这个是测试，我希望能通过")
                .setHandler(context.asyncAssertSuccess(post -> {
                    context.assertNotNull(post,"post is null after addPost");
                    postService.getOne(post.getPostNo()).setHandler(context.asyncAssertSuccess(findPost -> {
                        context.assertNotNull(findPost,"pst is null after getOne");
                        context.assertEquals(post.getPostNo(),findPost.getPostNo(),"different post");
                        postService.deleteOne(findPost.getPostNo()).setHandler(context.asyncAssertSuccess(res -> {
                            async.complete();
                        }));
                    }));
                }));
        async.awaitSuccess(5000);
    }

    @Test
    public void addPostReply(TestContext context) {
        Async async = context.async();
        postService.addPost("comp1","WIW80OM","这是一个测试2，如果这个是测试，我希望能通过")
                .setHandler(context.asyncAssertSuccess(mainPost -> {
                    context.assertNotNull(mainPost,"post is null after addPost");
                    postService.addPostReply(mainPost,"WIW80OM","我回复我自己")
                            .setHandler(context.asyncAssertSuccess(reply -> {
                                context.assertNotNull(reply,"reply is null after reply");
                                context.assertEquals(mainPost.getPostNo(),reply.getReplyPostNo(),"reply error");
                                postService.addPostReply(mainPost,"WIW80OM","我回复我自己2")
                                        .setHandler(context.asyncAssertSuccess(reply2 -> {
                                            context.assertNotNull(reply2,"reply is null after reply");
                                            context.assertEquals(mainPost.getPostNo(),reply2.getReplyPostNo(),"reply error");
                                        }));
                                async.complete();
                            }));
                }));
        async.awaitSuccess(5000);
    }

    @Test
    public void addSagePostReply(TestContext context){
        Async async = context.async();
        postService.addPost("comp1","WIW80OM","这是一个测试，这个post将要被sage")
                .setHandler(context.asyncAssertSuccess(mainPost -> {
                    context.assertNotNull(mainPost,"post is null after addPost");
                    Post updatePost = new Post();
                    updatePost.setSage(true);
                    postService.updateOne(mainPost.getPostNo(),updatePost).setHandler(context.asyncAssertSuccess(newMainPost -> {
                        context.assertNotNull(newMainPost,"post is null after updateOne");
                        postService.addPostReply(newMainPost,"WIW80OM","回复后updatetime应该不变")
                                .setHandler(context.asyncAssertSuccess(replyPost -> {
                                    context.assertNotNull(replyPost,"replypost is null after reply");
                                    postService.getOne(replyPost.getReplyPostNo())
                                            .setHandler(context.asyncAssertSuccess(latestMainPost -> {
                                                context.assertEquals(latestMainPost.getUpdateTime(),mainPost.getUpdateTime(),"updatetime changed");
                                                async.complete();
                                            }));
                                }));
                    }));
                }));
        async.awaitSuccess(5000);
    }

    @Test
    public void postPageQuery(TestContext context) {
        Async async = context.async();
        postService.postPageQuery("comp1",20,"", Mongo.ASC,true)
                .setHandler(context.asyncAssertSuccess(list -> {
                    context.assertNotNull(list,"null after query");
                    async.complete();
                }));
        async.awaitSuccess(5000);
    }

    @Test
    public void postReplyPageQuery(TestContext context) {
        Async async = context.async();
        postService.postReplyPageQuery("00000009",20,"",Mongo.ASC,true)
                .setHandler(context.asyncAssertSuccess(list -> {
                    context.assertNotNull(list,"null after query");
                    async.complete();
                }));
        async.awaitSuccess(5000);
    }
}
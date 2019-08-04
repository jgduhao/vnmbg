package service;

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
public class VnmbPosterServiceTest {

    @Rule
    public RunTestOnContext rule = new RunTestOnContext();

    private Vertx vertx;
    private VnmbPosterService vnmbPosterService;

    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        JsonObject options = new JsonObject()
                .put("connection_string","mongodb://localhost:27017")
                .put("db_name","nmb");
        vnmbPosterService = new VnmbPosterService(vertx,options);
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testCrud(TestContext context) throws Exception {
        Async async = context.async();
        vnmbPosterService.addOne().setHandler(context.asyncAssertSuccess(addRes -> {
            context.assertNotNull(addRes,"after add poster is null");
            String posterSign = addRes.getPosterSign();
            vnmbPosterService.getOne(posterSign).setHandler(context.asyncAssertSuccess(getRes -> {
                context.assertNotNull(getRes,"after get is null");
                context.assertEquals(posterSign,getRes.getPosterSign(),"posterSing is wrong");
                vnmbPosterService.updateOne(posterSign,false).setHandler(context.asyncAssertSuccess(updateRes -> {
                    context.assertNotNull(updateRes,"after update poster is null");
                    context.assertFalse(updateRes.getAvailable(),"available is not false now");
                    vnmbPosterService.deleteOne(posterSign).setHandler(context.asyncAssertSuccess(deleteRes -> {
                        vnmbPosterService.getOne(posterSign).setHandler(context.asyncAssertSuccess(getRes2 -> {
                            context.assertNull(getRes2,"delete failed");
                            async.complete();
                        }));
                    }));
                }));
            }));
        }));
        async.awaitSuccess(5000);
    }

    @Test
    public void testGetOrCreateWhenGet(TestContext context){
        Async async = context.async();
        vnmbPosterService.addOne().setHandler(context.asyncAssertSuccess(addPoster -> {
            String posterSign = addPoster.getPosterSign();
            vnmbPosterService.getOrCreateOne(posterSign).setHandler(context.asyncAssertSuccess(getPoster -> {
                context.assertNotNull(getPoster,"get Poster is null");
                context.assertEquals(posterSign,getPoster.getPosterSign(),"get PosterSign error");
                vnmbPosterService.deleteOne(posterSign).setHandler(context.asyncAssertSuccess(delRes -> {
                    async.complete();
                }));
            }));
        }));
        async.awaitSuccess(5000);
    }

    @Test
    public void testGetOrCreateWhenCreate(TestContext context){
        Async async = context.async();
        vnmbPosterService.getOrCreateOne("00000000").setHandler(context.asyncAssertSuccess(newPoster -> {
            context.assertNotNull(newPoster,"poster is null after create");
            String posterSign = newPoster.getPosterSign();
            vnmbPosterService.getOne(posterSign).setHandler(context.asyncAssertSuccess(getPoster -> {
                context.assertNotNull(getPoster,"poster is null after get");
                context.assertEquals(posterSign,getPoster.getPosterSign(),"get poster is error");
                vnmbPosterService.deleteOne(posterSign).setHandler(context.asyncAssertSuccess(delRes -> {
                    async.complete();
                }));
            }));
        }));
        async.awaitSuccess(5000);
    }

}
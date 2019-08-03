package service;

import entity.Board;
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
import utils.DateUtil;

import static org.junit.Assert.*;

@RunWith(VertxUnitRunner.class)
public class VnmbBoardServiceTest {

    @Rule
    public RunTestOnContext rule = new RunTestOnContext();

    private Vertx vertx;
    private VnmbBoardService boardService;

    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        JsonObject options = new JsonObject()
                .put("connection_string","mongodb://localhost:27017")
                .put("db_name","nmb");
        boardService = new VnmbBoardService(vertx,options);
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testCrud(TestContext context) {
        Async async = context.async();
        Board board = new Board();
        board.setName("测试");
        board.setBoardSign("test");
        board.setAvailable(true);
        board.setReadonly(false);
        board.setOrder("9999");
        board.setOperator("tester");
        boardService.getAll(true).setHandler(context.asyncAssertSuccess(allRes -> {
            context.assertNotNull(allRes, "all list is null");
            int boardCount = allRes.size();
            boardService.addOne(board).setHandler(context.asyncAssertSuccess(addRes -> {
                context.assertNotNull(addRes, "after add is null");
                String boardSign = addRes.getBoardSign();
                context.assertNotNull(boardSign, "after add boardSign is null");
                boardService.getOne(boardSign).setHandler(context.asyncAssertSuccess(oneRes -> {
                    context.assertNotNull(oneRes,"after getOne res is null");
                    context.assertEquals(boardSign,oneRes.getBoardSign(),"boardSing is wrong");
                    Board updateBoard = new Board();
                    updateBoard.setReadonly(true);
                    boardService.updateOne(boardSign,updateBoard).setHandler(context.asyncAssertSuccess(updRes -> {
                        context.assertNotNull(updRes,"after update res is null");
                        context.assertTrue(updRes.getReadonly());
                        boardService.deleteOne(boardSign).setHandler(context.asyncAssertSuccess(delRes -> {
                            boardService.getAll(true).setHandler(context.asyncAssertSuccess(allRes2 -> {
                                context.assertEquals(boardCount,allRes2.size(),"delete failed");
                                async.complete();
                            }));
                        }));
                    }));
                }));
            }));
        }));
        async.awaitSuccess(5000);
    }
}
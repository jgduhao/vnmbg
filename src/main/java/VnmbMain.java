import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import verticle.VnmbVerticle;

public class VnmbMain {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        DeploymentOptions options = new DeploymentOptions().setConfig(
                new JsonObject().put("connection_string","mongodb://localhost:27017")
                                .put("db_name","nmb")
                                .put("nmb_http_port","8091"));
        vertx.deployVerticle(VnmbVerticle.class.getName(),options);
    }

}

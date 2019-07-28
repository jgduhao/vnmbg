import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import verticle.RouterVerticle

class Main {

    def static main(args) {
        def vertx = Vertx.vertx()
        def config = new DeploymentOptions()
                .setConfig(new JsonObject(['connection_string':'mongodb://localhost:27017',
                                            'db_name':'nmb']))
        vertx.deployVerticle(RouterVerticle.class.name,config)
    }
}

package me.jiangew.boruto.web.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;

/**
 * Author: Jiangew
 * Date: 03/07/2017
 */
public class HelloWorldVerticle extends AbstractVerticle {

    // Convenience method so you can run it in your IDE
//    public static void main(String[] args) {
//        Runner.runExample(HelloWorldVerticle.class);
//    }

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);

        String name = config().getString("name", "Hello World !");
        router.route().handler(routingContext -> {
            routingContext.response().putHeader("content-type", "text/html").end(name);
        });

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }
}

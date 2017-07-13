package io.github.jiangew.ts.web.verticle;

import io.github.jiangew.ts.util.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;

/**
 * Let's say we have to call a blocking API (e.g. JDBC) to execute a query for each request.
 * We can't do this directly or it will block the event loop, But you can do this using executeBlocking.
 * <p>
 * Author: Jiangew
 * Date: 06/07/2017
 */
public class ExecBlockingVerticle extends AbstractVerticle {

    public static void main(String[] args) {
//        Runner.runExample(ExecBlockingVerticle.class);

        // Execute Blocking Dedicated Pool
        Runner.runExample(ExecBlockingVerticle.class, new DeploymentOptions()
                .setWorkerPoolName("dedicated-pool")
                .setWorkerPoolSize(5)
                .setMaxWorkerExecuteTime(120000)
        );
    }

    @Override
    public void start() throws Exception {
        vertx.createHttpServer().requestHandler(request -> vertx.<String>executeBlocking(future -> {
            // do blocking operation
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // ignore
            }

            String result = "jamesiworks";
            future.complete(result);
        }, res -> {
            if (res.succeeded()) {
                request.response().putHeader("content-type", "text/plain").end(res.result());
            } else {
                res.cause().printStackTrace();
            }
        })).listen(8080);
    }

}
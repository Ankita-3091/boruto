package me.jiangew.boruto.web.verticle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.TimeoutHandler;

/**
 * Author: Jiangew
 * Date: 10/07/2017
 */
public class DownloadVerticle extends AbstractVerticle {

//    public static void main(String[] args) {
//        Runner.runExample(DownloadVerticle.class);
//    }

    private static Gson gson = new GsonBuilder().create();

    @Override
    public void start() {
        System.out.println("DownloadVerticle deployed and started ...");

        // set work pool size
//        vertx.createSharedWorkerExecutor("vertx-worker-pool", VertxOptions.DEFAULT_WORKER_POOL_SIZE * 5, VertxOptions.DEFAULT_MAX_WORKER_EXECUTE_TIME);

        Router router = Router.router(vertx);

        router.route().handler(TimeoutHandler.create(2000));
        router.route().handler(BodyHandler.create());

        // blockingHandler or executeBlocking 的替代方案是 work verticle
        // 每一个阻塞的耗时操作单独 deploy 一个 work verticle 处理，一个 work verticle 一直被线程池中的一个线程执行

        // vert.x blocking handler && ordered false
        router.get("/down").blockingHandler(this::handleDown, false).failureHandler(this::handleWorkerTimeout);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

    @Override
    public void stop() {
        System.out.println("DownloadVerticle undeployed and stop ...");
    }

    /**
     * 测试
     *
     * @param routingContext
     */
    private void handleDown(RoutingContext routingContext) {
        routingContext.response().putHeader("content-type", "text/html").end("Hello World for Vert.x Web");
    }

    /**
     * handler timeout
     *
     * @param routingContext
     */
    private void handleWorkerTimeout(RoutingContext routingContext) {
        routingContext.response().setStatusCode(503).setStatusMessage("worker handler timeout ...");
    }

}

package me.jiangew.boruto.web.verticle;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.vertx.MetricsHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.dropwizard.MetricsService;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.TimeoutHandler;
import me.jiangew.boruto.common.util.Runner;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Author: Jiangew
 * Date: 10/07/2017
 */
public class MetricsVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        Runner.runExample(MetricsVerticle.class);
    }

    /**
     * 接受一个类型为 Future<Void> 的参数，异步初始化方法，Future 代表着 Verticle 实例是否初始化完成，complete 和 fail 表示成功和失败。
     *
     * @param future
     */
    @Override
    public void start(Future<Void> future) {
        System.out.println("MetricsVerticle deployed and started ...");

        Router router = Router.router(vertx);

        // metrics service
        MetricsService metricsService = MetricsService.create(vertx);

        // metrics registry
        MetricRegistry metricRegistry = SharedMetricRegistries.getOrCreate("exported");
        CollectorRegistry.defaultRegistry.register(new DropwizardExports(metricRegistry));

        // cors support
        Set<String> allowHeaders = new HashSet<>();
        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");
        Set<HttpMethod> allowMethods = new HashSet<>();
        allowMethods.add(HttpMethod.GET);
        allowMethods.add(HttpMethod.POST);
        allowMethods.add(HttpMethod.DELETE);
        allowMethods.add(HttpMethod.PATCH);

        router.route().handler(CorsHandler.create("*").allowedHeaders(allowHeaders).allowedMethods(allowMethods));
        router.route().handler(TimeoutHandler.create(2000));
        router.route().handler(BodyHandler.create());

        router.get("/metrics").handler(new MetricsHandler());

        // blockingHandler or executeBlocking 的替代方案是 work verticle
        // 每一个阻塞的耗时操作单独 deploy 一个 work verticle 处理，一个 work verticle 一直被线程池中的一个线程执行

        // vert.x blocking handler && ordered false
        router.get("/test").blockingHandler(this::handleTest, false).failureHandler(this::handleWorkerTimeout);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080, result -> {
            if (result.succeeded()) {
                future.complete();
            } else {
                future.fail(result.cause());
            }
        });

        // Send a metrics events every second
        vertx.setPeriodic(1000, t -> {
            JsonObject metrics = metricsService.getMetricsSnapshot(vertx.eventBus());
            vertx.eventBus().publish("metrics", metrics);
        });

        // Send some messages
        Random random = new Random();
        vertx.eventBus().consumer("whatever", msg -> {
            vertx.setTimer(10 + random.nextInt(50), id -> {
                vertx.eventBus().send("whatever", "hello");
            });
        });
        vertx.eventBus().send("whatever", "hello");

        // Increase counter every second
        vertx.setPeriodic(1_000L, e -> metricRegistry.counter("testCounter").inc());

    }

    /**
     * stop
     */
    @Override
    public void stop() {
        System.out.println("MetricsVerticle undeployed and stop ...");
    }

    /**
     * Wrap the result handler with failure handler (503 Service Unavailable)
     */
    private <T> Handler<AsyncResult<T>> resultHandler(RoutingContext routingContext, Consumer<T> consumer) {
        return res -> {
            if (res.succeeded()) {
                consumer.accept(res.result());
            } else {
                serviceUnavailable(routingContext);
            }
        };
    }

    /**
     * 测试
     *
     * @param routingContext
     */
    private void handleTest(RoutingContext routingContext) {
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

    /**
     * error
     *
     * @param statusCode
     * @param response
     */
    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }

    /**
     * 400 error
     *
     * @param context
     */
    private void badRequest(RoutingContext context) {
        context.response().setStatusCode(400).end();
    }

    /**
     * 404 error
     *
     * @param context
     */
    private void notFound(RoutingContext context) {
        context.response().setStatusCode(404).end();
    }

    /**
     * 500 error
     *
     * @param context
     */
    private void serviceInternalError(RoutingContext context) {
        context.response().setStatusCode(500).end();
    }

    /**
     * 503 error
     *
     * @param context
     */
    private void serviceUnavailable(RoutingContext context) {
        context.response().setStatusCode(503).end();
    }

}

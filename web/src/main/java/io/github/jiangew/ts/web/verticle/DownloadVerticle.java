package io.github.jiangew.ts.web.verticle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.jiangew.ts.util.Runner;
import io.github.jiangew.ts.util.compress.CompressUtil;
import io.github.jiangew.ts.util.compress.bo.ContentBO;
import io.github.jiangew.ts.util.stream.AsyncInputStream;
import io.github.jiangew.ts.web.dto.ChapterContent;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.impl.BufferFactoryImpl;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.streams.Pump;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.TimeoutHandler;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Author: Jiangew
 * Date: 10/07/2017
 */
public class DownloadVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        Runner.runExample(DownloadVerticle.class);
    }

    private static Gson gson = new GsonBuilder().create();

    @Override
    public void start() {
        System.out.println("DownloadVerticle deployed and started ...");

        // set work pool size
//        vertx.createSharedWorkerExecutor("vertx-worker-pool", VertxOptions.DEFAULT_WORKER_POOL_SIZE * 5, VertxOptions.DEFAULT_MAX_WORKER_EXECUTE_TIME);

        Router router = Router.router(vertx);

        router.route().handler(TimeoutHandler.create(2000));
        router.route().handler(BodyHandler.create());

        // hello vert.x
        router.get("/down").handler(this::handleDown);

        // blocking event loop
        router.get("/chapters").blockingHandler(this::handleGetChapters, false).failureHandler(this::handleWorkerTimeout);

        // blocking handler && ordered false && completable future
        router.get("/chaptersAsync").blockingHandler(this::handleGetChaptersAsync, false).failureHandler(this::handleWorkerTimeout);

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
     * 章节下载 支持批量
     *
     * @param routingContext
     */
    private void handleGetChapters(RoutingContext routingContext) {
        String bookId = routingContext.request().getParam("bookId");
        String chapterIds = routingContext.request().getParam("chapterIds");
        HttpServerResponse response = routingContext.response();

        // blocking
        try {
            // 书 耗时2ms
            // 章节 耗时3ms
            // 非免费章节时，调 ReadAndPay TAF 接口鉴权，耗时52ms
            Thread.sleep(2 + 3 + 52 + 200);
        } catch (Exception e) {
            // ignore
        }

        List<ContentBO> contentList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ContentBO bo = new ContentBO(ChapterContent.content, "你的节操掉啦" + i);
            contentList.add(bo);
        }

        byte[] encrGzipToByteArr = null;
        try {
            encrGzipToByteArr = CompressUtil.tarEncryptGzipSyncToByteArr(contentList);
        } catch (Exception e) {
            // ignore
        }

        AsyncInputStream inputStream = new AsyncInputStream(vertx, Executors.newCachedThreadPool(), new ByteArrayInputStream(encrGzipToByteArr));
        response.setStatusCode(200);
        response.setChunked(true); // 流的长度不确定，请求使用chunk及分块传输
        response.putHeader("Content-type", "application/octet-stream");
        response.putHeader("Content-Length", String.valueOf(encrGzipToByteArr.length));
        response.putHeader("Accept-Ranges", "bytes");
        response.putHeader("Pragma", "no-cache");
//        response.putHeader("encoding", "gzip");

        inputStream.endHandler(event -> response.end());
        Pump.pump(inputStream, response).start();
    }

    /**
     * 章节下载 支持批量 耗时操作异步执行
     *
     * @param routingContext
     */
    private void handleGetChaptersAsync(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();

        // blocking async
//        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
//            try {
//                // 书 耗时2ms
//                // 章节 耗时3ms
//                // 非免费章节时，调 ReadAndPay TAF 接口鉴权，耗时52ms
//                Thread.sleep(2 + 3 + 52 + 200);
//            } catch (Exception e) {
//                // ignore
//            }
//            return "Sleep Succeed";
//        });
//        try {
//            String succeed = future.get();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        Future<String> future = Future.future();
        future.setHandler(res -> {
            try {
                // 书 耗时2ms
                // 章节 耗时3ms
                // 非免费章节时，调 ReadAndPay TAF 接口鉴权，耗时52ms
                Thread.sleep(2 + 3 + 52 + 200);
            } catch (Exception e) {
                // ignore
            }
        });
        future.complete("Sleep Succeed");

        List<ContentBO> contentList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ContentBO bo = new ContentBO(ChapterContent.content, "你的节操掉啦" + i);
            contentList.add(bo);
        }

        byte[] encrGzipToByteArr = null;
        try {
            encrGzipToByteArr = CompressUtil.tarEncryptGzipSyncToByteArr(contentList);
        } catch (Exception e) {
            // ignore
        }

        response.setStatusCode(200);
        response.setChunked(true); // 流的长度不确定，请求使用chunk及分块传输
        response.putHeader("Content-type", "application/octet-stream");
        response.putHeader("Content-Length", String.valueOf(encrGzipToByteArr.length));
        response.putHeader("Accept-Ranges", "bytes");
        response.putHeader("Pragma", "no-cache");
//        response.putHeader("encoding", "gzip");
        response.end(new BufferFactoryImpl().buffer(encrGzipToByteArr));

//        AsyncInputStream inputStream = new AsyncInputStream(vertx, Executors.newCachedThreadPool(), new ByteArrayInputStream(encrGzipToByteArr));
//        inputStream.endHandler(event -> response.end());
//        Pump.pump(inputStream, response).start();

        // now call the next handler
//        routingContext.next();
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

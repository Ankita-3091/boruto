package me.jiangew.boruto.service.consumer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import me.jiangew.boruto.common.util.Runner;
import me.jiangew.boruto.service.provide.ProcessorService;

/**
 * Author: Jiangew
 * Date: 06/07/2017
 */
public class ConsumerVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        Runner.runExample(ConsumerVerticle.class);
    }

    private final String SERVICE_ADDRESS = "service.provide.processor";

    @Override
    public void start() throws Exception {
        ProcessorService proxyService = ProcessorService.createProxy(vertx, SERVICE_ADDRESS);

        JsonObject document = new JsonObject().put("name", "vertx");

        proxyService.process(document, (r) -> {
            if (r.succeeded()) {
                System.out.println(r.result().encodePrettily());
            } else {
                System.out.println(r.cause());
                Failures.handleFailure(r.cause());
            }
        });
    }

}

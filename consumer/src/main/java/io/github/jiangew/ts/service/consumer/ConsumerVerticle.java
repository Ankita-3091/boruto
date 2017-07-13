package io.github.jiangew.ts.service.consumer;

import io.github.jiangew.ts.service.ProcessorService;
import io.github.jiangew.ts.util.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

/**
 * Author: Jiangew
 * Date: 06/07/2017
 */
public class ConsumerVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        Runner.runExample(ConsumerVerticle.class);
    }

    @Override
    public void start() throws Exception {
        ProcessorService service = ProcessorService.createProxy(vertx, "vertx-processor");

        JsonObject document = new JsonObject().put("name", "vertx");

        service.process(document, (r) -> {
            if (r.succeeded()) {
                System.out.println(r.result().encodePrettily());
            } else {
                System.out.println(r.cause());
                Failures.dealWithFailure(r.cause());
            }
        });
    }

}

package me.jiangew.boruto.ebservice.consumer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import me.jiangew.boruto.common.util.Runner;
import me.jiangew.boruto.ebservice.processor.ProcessorService;

public class ProcessorConsumerVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Runner.runExample(ProcessorConsumerVerticle.class);
  }

  private final String SERVICE_ADDRESS = "service.provide.processor";

  @Override
  public void start() throws Exception {
    ProcessorService proxyService = ProcessorService.createProxy(vertx, SERVICE_ADDRESS);

    JsonObject document = new JsonObject().put("name", "vertx");

    proxyService.process(
        document,
        (r) -> {
          if (r.succeeded()) {
            System.out.println(r.result().encodePrettily());
          } else {
            System.out.println(r.cause());
            ProcessorFailures.handleFailure(r.cause());
          }
        });
  }
}

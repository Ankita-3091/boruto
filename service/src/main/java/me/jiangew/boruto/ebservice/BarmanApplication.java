package me.jiangew.boruto.ebservice;

import io.vertx.core.Vertx;
import me.jiangew.boruto.ebservice.consumer.BarmanConsumerVerticle;
import me.jiangew.boruto.ebservice.provider.BarmanVerticle;

public class BarmanApplication {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(
        new BarmanVerticle(),
        ar -> {
          System.out.println("... Barman is ready to serve you ...");
          vertx.deployVerticle(
              new BarmanConsumerVerticle(),
              ac -> {
                vertx.close();
              });
        });
  }
}

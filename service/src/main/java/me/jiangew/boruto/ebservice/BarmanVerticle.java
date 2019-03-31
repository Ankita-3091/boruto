package me.jiangew.boruto.ebservice;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceBinder;
import me.jiangew.boruto.ebservice.barman.BarmanService;
import me.jiangew.boruto.ebservice.barman.impl.BarmanServiceImpl;

public class BarmanVerticle extends AbstractVerticle {

  @Override
  public void start() {
    BarmanService service = new BarmanServiceImpl(WebClient.create(vertx)); // (1)

    new ServiceBinder(vertx) // (2)
        .setAddress("ebservice.barman.application") // (3)
        .register(BarmanService.class, service); // (4)
  }
}

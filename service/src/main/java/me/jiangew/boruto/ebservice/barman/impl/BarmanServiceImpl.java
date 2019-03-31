package me.jiangew.boruto.ebservice.barman.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import me.jiangew.boruto.ebservice.barman.BarmanService;
import me.jiangew.boruto.ebservice.barman.Beer;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BarmanServiceImpl implements BarmanService {

  Map<String, Integer> bills;
  Random random;
  WebClient webClient;

  public BarmanServiceImpl(WebClient webClient) {
    this.bills = new HashMap<>();
    this.random = new Random();
    this.webClient = webClient;
  }

  @Override
  public void giveMeARandomBeer(String customerName, Handler<AsyncResult<Beer>> handler) {
    webClient
        .get(443, "www.craftbeernamegenerator.com", "/api/api.php?type=classic")
        .ssl(true)
        .send(
            ar -> { // <1>
              if (ar.failed()) handler.handle(Future.failedFuture(ar.cause())); // <2>
              else {
                JsonObject result = ar.result().bodyAsJsonObject();
                if (result.getInteger("status") != 200) // <2>
                handler.handle(
                      Future.failedFuture(
                          "Beer Generator Service replied with "
                              + result.getInteger("status")
                              + ": "
                              + result.getString("status_message")));
                else {
                  Beer beer =
                      new Beer( // <3>
                          result.getJsonObject("data").getString("name"),
                          result.getJsonObject("data").getString("style"),
                          3 + random.nextInt(5));
                  System.out.println("Generated a new Beer! " + beer);
                  bills.merge(
                      customerName, beer.getPrice(), (oldVal, newVal) -> oldVal + newVal); // <4>
                  handler.handle(Future.succeededFuture(beer)); // <5>
                }
              }
            });
  }

  @Override
  public void getMyBill(String customerName, Handler<AsyncResult<Integer>> handler) {
    handler.handle(Future.succeededFuture(bills.get(customerName)));
  }

  @Override
  public void payMyBill(String customerName) {
    bills.remove(customerName);
    System.out.println("Removed debt of " + customerName);
  }
}

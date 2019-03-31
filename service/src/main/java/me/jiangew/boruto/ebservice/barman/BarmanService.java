package me.jiangew.boruto.ebservice.barman;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

@VertxGen
@ProxyGen
public interface BarmanService {

  void giveMeARandomBeer(String customerName, Handler<AsyncResult<Beer>> handler);

  void getMyBill(String customerName, Handler<AsyncResult<Integer>> handler);

  void payMyBill(String customerName);

  static BarmanService createProxy(Vertx vertx, String address) {
    return new BarmanServiceVertxEBProxy(vertx, address);
  }
}

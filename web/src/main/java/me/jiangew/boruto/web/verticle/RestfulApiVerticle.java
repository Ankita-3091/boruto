package me.jiangew.boruto.web.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Author: Jiangew
 * Date: 04/07/2017
 */
public class RestfulApiVerticle extends AbstractVerticle {
    private final static Logger LOGGER = Logger.getLogger("RestfulApiVerticle");

    // Convenience method so you can run it in your IDE
//    public static void main(String[] args) {
//        Runner.runExample(RestfulApiVerticle.class);
//    }

    private Map<String, JsonObject> products = new HashMap<>();

    @Override
    public void start() {
        // initial data
        setUpInitialData();

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.get("/products/:productId").handler(this::handleGetProduct);
        router.get("/products").handler(this::handleListProducts);
        router.put("/products/:productId").handler(this::handleAddProduct);

        // blocking handler hold 500 mil
        router.get("/productsAsync").blockingHandler(routingContext -> {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // ignore
            }
            // now call the next handler
            routingContext.next();
            // ordered
        }, false);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

    private void handleGetProduct(RoutingContext routingContext) {
        String productId = routingContext.request().getParam("productId");
        HttpServerResponse response = routingContext.response();
        if (productId == null) {
            sendError(400, response);
        } else {
            JsonObject product = products.get(productId);
            if (product == null) {
                sendError(500, response);
            } else {
                response.putHeader("content-type", "application/json").end(product.encodePrettily());
            }
        }
    }

    private void handleAddProduct(RoutingContext routingContext) {
        String productId = routingContext.request().getParam("productId");
        HttpServerResponse response = routingContext.response();
        if (productId == null) {
            sendError(400, response);
        } else {
            JsonObject product = routingContext.getBodyAsJson();
            if (product == null) {
                sendError(400, response);
            } else {
                products.put(productId, product);
                response.end();
            }
        }
    }

    private void handleListProducts(RoutingContext routingContext) {
        JsonArray arr = new JsonArray();
        products.forEach((k, v) -> arr.add(v));

        routingContext.response().putHeader("content-type", "application/json").end(arr.encodePrettily());
    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }

    private void addProduct(JsonObject product) {
        products.put(product.getString("id"), product);
    }

    private void setUpInitialData() {
        addProduct(new JsonObject().put("id", "prod3568").put("name", "Egg Whisk").put("price", 3.99).put("weight", 150));
        addProduct(new JsonObject().put("id", "prod7340").put("name", "Tea Cosy").put("price", 5.99).put("weight", 100));
        addProduct(new JsonObject().put("id", "prod8643").put("name", "Spatula").put("price", 1.00).put("weight", 80));
    }

}

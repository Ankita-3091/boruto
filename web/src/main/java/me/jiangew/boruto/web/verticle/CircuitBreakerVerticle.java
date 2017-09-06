package me.jiangew.boruto.web.verticle;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Vert.x Circuit Breaker 熔断器
 * Author: Jiangew
 * Date: 06/09/2017
 */
public class CircuitBreakerVerticle extends AbstractVerticle {

    protected CircuitBreaker circuitBreaker;

    @Override
    public void start() throws Exception {
        // router
        Router router = Router.router(vertx);

        // config
        JsonObject cbOptions = config().getJsonObject("circuit-breaker") != null ? config().getJsonObject("circuit-breaker") : new JsonObject();
        // circuit breaker instance
        circuitBreaker = CircuitBreaker.create(
                cbOptions.getString("name", "circuit-breaker"),
                vertx,
                new CircuitBreakerOptions()
                        .setMaxFailures(cbOptions.getInteger("max-failures", 5)) // number of failure before opening the circuit
                        .setTimeout(cbOptions.getLong("timeout", 10000L)) // consider a failure if the operation does not succeed in time
                        .setFallbackOnFailure(true) // do we call the fallback on failure
                        .setResetTimeout(cbOptions.getLong("reset-timeout", 30000L)) // time spent in open state before attempting to re-try
        ).fallback(v -> {
            // Executed when the circuit is opened.
            return "hello";
        }).openHandler(v -> {
            System.out.println("Circuit opened");
        }).closeHandler(v -> {
            System.out.println("Circuit closed");
        });

        // api dispatcher
        router.route("/api/*").handler(this::dispatchRequests);

        // http server
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);

        // test circuit breaker
        circuitBreaker.<String>execute(future -> {
            // the code reports failures or success on the given future.
            // if this future is marked as failed, the breaker increased the number of failures.
            vertx.createHttpClient().getNow(8080, "localhost", "/", response -> {
                if (response.statusCode() != 200) {
                    future.fail("HTTP error");
                } else {
                    response.exceptionHandler(future::fail)
                            .bodyHandler(buffer -> {
                                future.complete(buffer.toString());
                            });
                }
            });
        }).setHandler(ar -> {
            if (ar.failed()) {
                // do something
            }
        });

        // test circuit breaker
        // Optionally, you can provide a fallback which is executed when the circuit is open.
        // The fallback is called whenever the circuit is open, or if the isFallbackOnFailure is enabled.
        circuitBreaker.executeWithFallback(future -> {
            vertx.createHttpClient().getNow(8080, "localhost", "/", response -> {
                if (response.statusCode() != 200) {
                    future.fail("HTTP error");
                } else {
                    response.exceptionHandler(future::fail)
                            .bodyHandler(buffer -> {
                                future.complete(buffer.toString());
                            });
                }
            });
        }, v -> {
            // Executed when the circuit is opened
            return "Hello";
        }).setHandler(ar -> {
            if (ar.failed()) {
                // do something
            }
        });

    }

    /**
     * api dispatcher
     *
     * @param context
     */

    private void dispatchRequests(RoutingContext context) {
        circuitBreaker.execute(future -> {
            // the code reports failures or success on the given future.
            // if this future is marked as failed, the breaker increased the number of failures.
        }).setHandler(ar -> {
            if (ar.failed()) {
                badGateway(ar.cause(), context);
            }
        });
    }

    /**
     * bad gateway
     *
     * @param ex
     * @param context
     */
    private void badGateway(Throwable ex, RoutingContext context) {
        ex.printStackTrace();
        context.response()
                .setStatusCode(502)
                .putHeader("content-type", "application/json")
                .end(new JsonObject().put("error", "bad gateway").encodePrettily());
    }

}

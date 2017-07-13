package io.github.jiangew.ts.service;

import io.github.jiangew.ts.util.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.serviceproxy.ProxyHelper;

/**
 * Author: Jiangew
 * Date: 06/07/2017
 */
public class ProcessorServiceVerticle extends AbstractVerticle {

    private ProcessorService service;

    public static void main(String[] args) {
        Runner.runExample(ProcessorServiceVerticle.class);
    }

    @Override
    public void start() throws Exception {
        service = ProcessorService.create(vertx);
        // register the handler
        ProxyHelper.registerService(ProcessorService.class, vertx, service, "vertx.processor");

        Router router = Router.router(vertx);

        // allow events for the designated addresses i/o of the event bus bridge
        BridgeOptions opts = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddress("vertx.processor"))
                .addOutboundPermitted(new PermittedOptions().setAddress("vertx.processor"));

        // create the event bus bridge and add it to the router
        SockJSHandler ebHandler = SockJSHandler.create(vertx).bridge(opts);

        router.route("/eventbus/*").handler(ebHandler);

        router.route().handler(StaticHandler.create());

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

}
package me.jiangew.boruto.service.provide;

import me.jiangew.boruto.service.provide.impl.ProcessorServiceImpl;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ProxyHelper;

/**
 * Author: Jiangew
 * Date: 06/07/2017
 */
@ProxyGen // Generate the proxy and handler
@VertxGen // Generate clients in non-java languages
public interface ProcessorService {

    int NO_NAME_ERROR = 10001;
    int BAD_NAME_ERROR = 10002;

    /**
     * factory methods to create an instance
     *
     * @param vertx
     * @return
     */
    static ProcessorService create(Vertx vertx) {
        return new ProcessorServiceImpl();
    }

    /**
     * factory methods to create a proxy
     *
     * @param vertx
     * @param address
     * @return
     */
    static ProcessorService createProxy(Vertx vertx, String address) {
        return ProxyHelper.createProxy(ProcessorService.class, vertx, address);
        // Alternatively, you can create the proxy directly using:
        // return new ProcessorServiceVertxEBProxy(vertx, address);
        // The name of the class to instantiate is the service interface + `VertxEBProxy`.
        // This class is generated during the compilation.
    }

    /**
     * process
     *
     * @param document
     * @param resultHandler
     */
    void process(JsonObject document, Handler<AsyncResult<JsonObject>> resultHandler);

}

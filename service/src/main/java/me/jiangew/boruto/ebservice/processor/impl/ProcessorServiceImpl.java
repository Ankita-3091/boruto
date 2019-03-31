package me.jiangew.boruto.ebservice.processor.impl;

import io.vertx.core.Vertx;
import me.jiangew.boruto.ebservice.processor.ProcessorService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceException;

/**
 * Author: Jiangew
 * Date: 06/07/2017
 */
public class ProcessorServiceImpl implements ProcessorService {

    private Vertx vertx;

    public ProcessorServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    /**
     * process
     *
     * @param document
     * @param resultHandler
     */
    @Override
    public void process(JsonObject document, Handler<AsyncResult<JsonObject>> resultHandler) {
        JsonObject result = document.copy();
        if (!result.containsKey("name")) {
            resultHandler.handle(ServiceException.fail(NO_NAME_ERROR, "no name in the document"));
        } else if (document.getString("name").isEmpty() || document.getString("name").equalsIgnoreCase("bad")) {
            resultHandler.handle(ServiceException.fail(BAD_NAME_ERROR, "Bad name in the document: " +
                    document.getString("name"), new JsonObject().put("name", document.getString("name"))));
        } else {
            result.put("approved", true);
            resultHandler.handle(Future.succeededFuture(result));
        }
    }

    @Override
    public ProcessorService fluentProcess(String id, Handler<AsyncResult<JsonObject>> resultHandler) {
        return null;
    }

    public Vertx getVertx() {
        return vertx;
    }

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

}

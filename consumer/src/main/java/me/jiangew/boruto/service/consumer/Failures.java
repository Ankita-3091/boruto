package me.jiangew.boruto.service.consumer;

import io.vertx.serviceproxy.ServiceException;
import me.jiangew.boruto.service.provide.ProcessorService;

/**
 * Isolated failure management to support CodeTrans (generation of the example in the different languages)
 *
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class Failures {

    public static void handleFailure(Throwable t) {
        if (t instanceof ServiceException) {
            ServiceException exc = (ServiceException) t;
            if (exc.failureCode() == ProcessorService.BAD_NAME_ERROR) {
                System.out.println("Failed to process the document: The name in the document is bad. " +
                        "The name provided is: " + exc.getDebugInfo().getString("name"));
            } else if (exc.failureCode() == ProcessorService.NO_NAME_ERROR) {
                System.out.println("Failed to process the document: No name was found");
            }
        } else {
            System.out.println("Unexpected error: " + t);
        }
    }

}

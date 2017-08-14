package me.jiangew.boruto.web;

import io.vertx.core.VertxOptions;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.hawkular.VertxHawkularOptions;

/**
 * Author: Jiangew
 * Date: 02/08/2017
 */
public class Launcher extends io.vertx.core.Launcher {

//    private final int core = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {

        // Force to use slf4j
//        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");

        new Launcher().dispatch(args);
    }

    @Override
    public void beforeStartingVertx(VertxOptions options) {
//        options.setClustered(true)
//                .setHAEnabled(true)
//                .setWorkerPoolSize(core * 50)
//                .setMaxWorkerExecuteTime(VertxOptions.DEFAULT_MAX_WORKER_EXECUTE_TIME);

        // Start dropwizard monitor
        options.setMetricsOptions(
                new DropwizardMetricsOptions()
                        .setEnabled(true)
                        .setJmxEnabled(true)
                        .setJmxDomain("vertx-metrics-boruto")
        );

        // Start hawkular metrics
        options.setMetricsOptions(
                new VertxHawkularOptions().setEnabled(true)
        );
    }

}
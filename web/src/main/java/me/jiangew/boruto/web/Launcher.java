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

        // Start hawkular metrics
        //        options.setMetricsOptions(
        //                new VertxHawkularOptions()
        //                        .setEnabled(true)
        //                        // user defined metrics
        //                        .setMetricsBridgeEnabled(true)
        //                        .setMetricsBridgeAddress("hawkular.metrics")
        ////                        .setHost("localhost")
        ////                        .setPort(8080)
        //                        // https ssl
        ////                        .setHttpOptions(new HttpClientOptions().setSsl(true))
        //                        // requests sent to a Hawkular server must be authenticated and tenant must be set to hawkular
        //                        .setTenant("hawkular")
        //                        .setAuthenticationOptions(new AuthenticationOptions()
        //                                .setEnabled(true)
        //                                .setId("jiangew")
        //                                .setSecret("123456")
        //                        )
        //        );

    }

}
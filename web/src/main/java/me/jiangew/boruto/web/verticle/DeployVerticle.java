package me.jiangew.boruto.web.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import me.jiangew.boruto.common.util.Runner;

/**
 * Author: Jiangew
 * Date: 12/07/2017
 */
public class DeployVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        Runner.runExample(DeployVerticle.class);
    }

    @Override
    public void start() {
        System.out.println("Main verticle has started, let's deploy some others ...");

        // different ways of deploying verticles

        // 01 deploy a verticle and do not wait for it to start
//        vertx.deployVerticle("MetricsVerticle");

        // 02 deploy a verticle and wait for it to start
//        vertx.deployVerticle("MetricsVerticle", res -> {
//            if (res.succeeded()) {
//                String deployId = res.result();
//                System.out.println("DeployVerticle deployed ok, deployId = " + deployId);
//            }
//        });

        // 03 deploy a verticle with options
        int core = Runtime.getRuntime().availableProcessors();
        vertx.deployVerticle("me.jiangew.boruto.web.verticle.MetricsVerticle",
                new DeploymentOptions()
                        .setInstances(core)
                        .setHa(true)
                        .setWorkerPoolName("vertx-work-pool-boruto")
                        .setWorkerPoolSize(core * 10)
                        .setMaxWorkerExecuteTime(VertxOptions.DEFAULT_MAX_WORKER_EXECUTE_TIME)
        );

    }
}

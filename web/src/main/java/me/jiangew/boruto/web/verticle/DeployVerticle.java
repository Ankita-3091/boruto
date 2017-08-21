package me.jiangew.boruto.web.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
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

        JsonObject config = config();
        int core = Runtime.getRuntime().availableProcessors();

        // 03 deploy a verticle with options
        vertx.deployVerticle(config.getString("deployVerticle", "me.jiangew.boruto.web.verticle.MetricsVerticle"),
                new DeploymentOptions()
                        .setInstances(core * config.getInteger("instanceFactor", 1))
                        .setHa(config.getBoolean("ha", true))
                        .setWorkerPoolName(config.getString("workerPoolName", "vertx-work-pool-boruto"))
                        .setWorkerPoolSize(core * config.getInteger("workerPoolSizeFactor", 5))
                        .setMaxWorkerExecuteTime(config.getLong("maxWorkerExecTime", VertxOptions.DEFAULT_MAX_WORKER_EXECUTE_TIME))
        );

    }
}

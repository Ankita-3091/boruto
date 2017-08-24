package me.jiangew.boruto.grpc.conversation;

import io.vertx.core.AbstractVerticle;
import me.jiangew.boruto.common.util.Runner;

/**
 * Author: Jiangew
 * Date: 24/08/2017
 */
public class Server extends AbstractVerticle {

    public static void main(String[] args) {
        Runner.runExample(Server.class);
    }

    @Override
    public void start() throws Exception {
    }

}

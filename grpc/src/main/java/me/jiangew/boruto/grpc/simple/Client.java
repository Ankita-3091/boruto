package me.jiangew.boruto.grpc.simple;

import io.grpc.ManagedChannel;
import io.vertx.core.AbstractVerticle;
import io.vertx.grpc.VertxChannelBuilder;
import me.jiangew.boruto.common.util.Runner;
import me.jiangew.boruto.grpc.Messages;
import me.jiangew.boruto.grpc.SimpleServiceGrpc;

/**
 * Author: Jiangew
 * Date: 24/08/2017
 */
public class Client extends AbstractVerticle {

    public static void main(String[] args) {
        Runner.runExample(Client.class);
    }

    @Override
    public void start() throws Exception {
        // create channel
        ManagedChannel channel = VertxChannelBuilder
                .forAddress(vertx, "localhost", 8080)
                .usePlaintext(true)
                .build();

        // get stub
        SimpleServiceGrpc.SimpleServiceVertxStub stub = SimpleServiceGrpc.newVertxStub(channel);

        Messages.SimpleRequest request = Messages.SimpleRequest.newBuilder().setFillUsername(true).build();

        // call remote server
        stub.unaryCall(request, ar -> {
            if (ar.succeeded()) {
                System.out.println("My username is: " + ar.result().getUsername());
            } else {
                System.out.println("Could not reach server " + ar.cause().getMessage());
            }
        });
    }

}

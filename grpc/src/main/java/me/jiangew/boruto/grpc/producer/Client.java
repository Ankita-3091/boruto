package me.jiangew.boruto.grpc.producer;

import io.grpc.ManagedChannel;
import io.vertx.core.AbstractVerticle;
import io.vertx.grpc.VertxChannelBuilder;
import me.jiangew.boruto.common.util.Runner;
import me.jiangew.boruto.grpc.Messages;
import me.jiangew.boruto.grpc.ProducerServiceGrpc;

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
        ManagedChannel channel = VertxChannelBuilder
                .forAddress(vertx, "localhost", 8080)
                .usePlaintext(true)
                .build();

        ProducerServiceGrpc.ProducerServiceVertxStub stub = ProducerServiceGrpc.newVertxStub(channel);

        stub.streamingInputCall(exchange -> {
            exchange.handler(ar -> {
                if (ar.succeeded()) {
                    System.out.println("Server replied OK");
                } else {
                    ar.cause().printStackTrace();
                }
            });

            for (int i = 0; i < 10; i++) {
                exchange.write(Messages.StreamingInputCallRequest.newBuilder().build());
            }

            exchange.end();
        });
    }

}

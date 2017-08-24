package me.jiangew.boruto.grpc.conversation;

import io.grpc.ManagedChannel;
import io.vertx.core.AbstractVerticle;
import io.vertx.grpc.VertxChannelBuilder;
import me.jiangew.boruto.common.util.Runner;
import me.jiangew.boruto.grpc.ConversationServiceGrpc;
import me.jiangew.boruto.grpc.Messages;

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

        ConversationServiceGrpc.ConversationServiceVertxStub stub = ConversationServiceGrpc.newVertxStub(channel);

        Messages.StreamingOutputCallRequest request = Messages.StreamingOutputCallRequest.newBuilder().build();

        // call remote service
        stub.fullDuplexCall(exchange -> {
            exchange.handler(req -> {
                System.out.println("Client: received response");
                vertx.setTimer(500L, t -> {
                    exchange.write(Messages.StreamingOutputCallRequest.newBuilder().build());
                });
            });

            // start conversation
            exchange.write(Messages.StreamingOutputCallRequest.newBuilder().build());
        });
    }

}

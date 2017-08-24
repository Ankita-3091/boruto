package me.jiangew.boruto.grpc.conversation;

import io.vertx.core.AbstractVerticle;
import io.vertx.grpc.GrpcBidiExchange;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;
import me.jiangew.boruto.common.util.Runner;
import me.jiangew.boruto.grpc.ConversationServiceGrpc;
import me.jiangew.boruto.grpc.Messages;

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
        ConversationServiceGrpc.ConversationServiceVertxImplBase service = new ConversationServiceGrpc.ConversationServiceVertxImplBase() {
            @Override
            public void fullDuplexCall(GrpcBidiExchange<Messages.StreamingOutputCallRequest, Messages.StreamingOutputCallResponse> exchange) {
                exchange.handler(req -> {
                    System.out.println("Server: received request");
                    vertx.setTimer(500L, t -> {
                        exchange.write(Messages.StreamingOutputCallResponse.newBuilder().build());
                    });
                });
            }
        };

        VertxServer server = VertxServerBuilder
                .forPort(vertx, 8080)
                .addService(service)
                .build();

        server.start(ar -> {
            if (ar.failed()) {
                ar.cause().printStackTrace();
            }
        });
    }

}

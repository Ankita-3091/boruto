package me.jiangew.boruto.grpc.producer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.grpc.GrpcReadStream;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;
import me.jiangew.boruto.common.util.Runner;
import me.jiangew.boruto.grpc.Messages;
import me.jiangew.boruto.grpc.ProducerServiceGrpc;

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
        ProducerServiceGrpc.ProducerServiceVertxImplBase service = new ProducerServiceGrpc.ProducerServiceVertxImplBase() {
            @Override
            public void streamingInputCall(GrpcReadStream<Messages.StreamingInputCallRequest> request, Future<Messages.StreamingInputCallResponse> response) {
                request.handler(payload -> {
                    System.out.println(payload.getPayload().getType().getNumber());
                }).endHandler(v -> {
                    System.out.println("Request has ended.");
                    response.complete(Messages.StreamingInputCallResponse.newBuilder().build());
                });
            }
        };

        VertxServer server = VertxServerBuilder.forPort(vertx, 8080).addService(service).build();

        server.start(ar -> {
            if (ar.failed()) {
                ar.cause().printStackTrace();
            }
        });
    }

}

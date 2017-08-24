package me.jiangew.boruto.grpc.simple;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;
import me.jiangew.boruto.common.util.Runner;
import me.jiangew.boruto.grpc.Messages;
import me.jiangew.boruto.grpc.SimpleServiceGrpc;

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
        // rpc service
        SimpleServiceGrpc.SimpleServiceVertxImplBase service = new SimpleServiceGrpc.SimpleServiceVertxImplBase() {
            @Override
            public void unaryCall(Messages.SimpleRequest request, Future<Messages.SimpleResponse> response) {
                response.complete(Messages.SimpleResponse.newBuilder()
                                          .setUsername("Jamesiworks")
                                          .build());
            }
        };

        // create server
        VertxServer server = VertxServerBuilder.forPort(vertx, 8080)
                .addService(service)
                .build();

        // start is asynchronous
        server.start(ar -> {
            if (ar.succeeded()) {
                System.out.println("Simple rpc service started");
            } else {
                System.out.println("Could not start rpc server " + ar.cause().getMessage());
            }
        });
    }

}

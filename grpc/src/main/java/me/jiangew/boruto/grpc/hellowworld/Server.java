package me.jiangew.boruto.grpc.hellowworld;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.net.JksOptions;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;
import me.jiangew.boruto.common.util.Runner;
import me.jiangew.boruto.grpc.helloworld.GreeterGrpc;
import me.jiangew.boruto.grpc.helloworld.HelloReply;
import me.jiangew.boruto.grpc.helloworld.HelloRequest;

/**
 * Author: Jiangew
 * Date: 21/08/2017
 */
public class Server extends AbstractVerticle {

    public static void main(String[] args) {
        Runner.runExample(Server.class);
    }

    @Override
    public void start() throws Exception {
        // rpc service
        GreeterGrpc.GreeterVertxImplBase service = new GreeterGrpc.GreeterVertxImplBase() {
            @Override
            public void sayHello(HelloRequest request, Future<HelloReply> future) {
                System.out.println("Hello " + request.getName());
                future.complete(HelloReply.newBuilder().setMessage(request.getName()).build());
            }
        };

        // create server
        VertxServer rpcServer = VertxServerBuilder
                .forAddress(vertx, "localhost", 8080)
                // enable TLS/SSL
                .useSsl(options -> options
                        .setSsl(true)
                        .setUseAlpn(true)
                        .setKeyStoreOptions(new JksOptions().setPath("server-keystore.jks").setPassword("secret"))
                )
                .addService(service)
                .build();

        // start is asynchronous
        rpcServer.start(ar -> {
            if (ar.succeeded()) {
                System.out.println("gRPC service started");
            } else {
                System.out.println("Could not start gRPC server " + ar.cause().getMessage());
            }
        });
    }

}

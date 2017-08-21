package me.jiangew.boruto.grpc.hellowworld;

import io.grpc.ManagedChannel;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.net.JksOptions;
import io.vertx.grpc.VertxChannelBuilder;
import me.jiangew.boruto.common.util.Runner;
import me.jiangew.boruto.grpc.helloworld.GreeterGrpc;
import me.jiangew.boruto.grpc.helloworld.HelloRequest;

/**
 * Author: Jiangew
 * Date: 21/08/2017
 */
public class Client extends AbstractVerticle {

    public static void main(String[] args) {
        Runner.runExample(Client.class);
    }

    @Override
    public void start() throws Exception {
        // managed channel
        ManagedChannel channel = VertxChannelBuilder
                .forAddress(vertx, "localhost", 8080)
                // enable TLS/SSL
                .useSsl(options -> options
                        .setSsl(true)
                        .setUseAlpn(true)
                        .setTrustStoreOptions(new JksOptions().setPath("client-truststore.jks").setPassword("secret"))
                )
                .usePlaintext(true)
                .build();

        // get a stub to use for interacting with the remote service
        GreeterGrpc.GreeterVertxStub stub = GreeterGrpc.newVertxStub(channel);

        HelloRequest request = HelloRequest.newBuilder().setName("JamesiWorks").build();

        // call the remote server
        stub.sayHello(request, ar -> {
            if (ar.succeeded()) {
                System.out.println("Got the server response: " + ar.result().getMessage());
            } else {
                System.out.println("Could not reach server " + ar.cause().getMessage());
            }
        });
    }

}

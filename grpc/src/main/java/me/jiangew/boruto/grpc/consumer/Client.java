package me.jiangew.boruto.grpc.consumer;

import io.grpc.ManagedChannel;
import io.vertx.core.AbstractVerticle;
import io.vertx.grpc.VertxChannelBuilder;
import me.jiangew.boruto.common.util.Runner;
import me.jiangew.boruto.grpc.ConsumerServiceGrpc;
import me.jiangew.boruto.grpc.Messages;

import java.nio.charset.Charset;

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

        ConsumerServiceGrpc.ConsumerServiceVertxStub stub = ConsumerServiceGrpc.newVertxStub(channel);

        Messages.StreamingOutputCallRequest request = Messages.StreamingOutputCallRequest.newBuilder().build();

        stub.streamingOutputCall(request, stream -> {
            stream.handler(response -> {
                System.out
                        .println(new String(response.getPayload().toByteArray(), Charset.forName("UTF-8")));
            }).endHandler(v -> {
                System.out.println("Response has ended.");
            });
        });
    }

}

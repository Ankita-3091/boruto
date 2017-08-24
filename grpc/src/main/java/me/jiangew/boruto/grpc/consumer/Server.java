package me.jiangew.boruto.grpc.consumer;

import com.google.protobuf.ByteString;
import io.vertx.core.AbstractVerticle;
import io.vertx.grpc.GrpcWriteStream;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;
import me.jiangew.boruto.common.util.Runner;
import me.jiangew.boruto.grpc.ConsumerServiceGrpc;
import me.jiangew.boruto.grpc.Messages;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

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
        ConsumerServiceGrpc.ConsumerServiceVertxImplBase service = new ConsumerServiceGrpc.ConsumerServiceVertxImplBase() {
            @Override
            public void streamingOutputCall(Messages.StreamingOutputCallRequest request, GrpcWriteStream<Messages.StreamingOutputCallResponse> response) {
                final AtomicInteger counter = new AtomicInteger();
                vertx.setPeriodic(1000L, t -> {
                    response.write(Messages.StreamingOutputCallResponse.newBuilder().setPayload(
                            Messages.Payload.newBuilder()
                                    .setTypeValue(Messages.PayloadType.COMPRESSABLE.getNumber())
                                    .setBody(ByteString.copyFrom(
                                            String.valueOf(counter.incrementAndGet()), Charset.forName("UTF-8")))
                    ).build());
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
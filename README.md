# Boruto

* 博人
* 火影儿子

## Module gRPC - Vert.x gRPC examples
Here you will find examples demonstrating Vert.x gRPC in action.

### Dependencies required

To use Vert.x gRPC in your own Maven or Gradle project add the following dependency

```jar
Group ID: io.vertx
Artifact ID: vertx-grpc
```

You will need to use the `com.google.protobuf` plugin to compile the `.proto` files.
These examples use the `io.vertx:protoc-gen-grpc-java` plugin that creates
server and stub classes for Vert.x, this plugin is an extension of the
 `io.grpc:protoc-gen-grpc-java` plugin.

The protobuf compiler is available for Maven or Gradle and is OS dependant.

### Ping Pong exchanges

This example show how to handle RPC calls that send a single object and receive a single object.

- [gRPC client example](link:src/main/java/io/vertx/example/grpc/pingpong/Client.java)
- [gRPC server example](link:src/main/java/io/vertx/example/grpc/pingpong/Server.java)

### Consumer exchange

This example show how to handle RPC calls that do receive an arbitrary stream of response objects.

- [gRPC client example](link:src/main/java/io/vertx/example/grpc/consumer/Client.java)
- [gRPC server example](link:src/main/java/io/vertx/example/grpc/consumer/Server.java)

### Producer exchange

This example show how to handle RPC calls that do sends an arbitrary stream of request objects.

- [gRPC client example](link:src/main/java/io/vertx/example/grpc/producer/Client.java)
- [gRPC server example](link:src/main/java/io/vertx/example/grpc/producer/Server.java)

### Conversational exchange

This example shows how to handle arbitrary streams of request and response objects.

- [gRPC client example](link:src/main/java/io/vertx/example/grpc/conversation/Client.java)
- [gRPC server example](link:src/main/java/io/vertx/example/grpc/conversation/Server.java)

### Hello World example

This example is adapted from the [gRPC examples](https://github.com/grpc/grpc-java/tree/master/examples).

It shows a simple gRPC service that invokes a service that
takes takes an `HelloRequest` string wrapper and returns an `HelloReply` string wrapper.

[gRPC server example](link:src/main/java/io/vertx/example/grpc/helloworld/Server.java)

The gRPC service extends the `GreeterGrpc.GreeterVertxImplBase` generated class and
implements the service logic.

[gRPC client example](link:src/main/java/io/vertx/example/grpc/helloworld/Client.java)

The gRPC client creates an instance of the `GreeterGrpc.newVertxStub` generated class and
then use it to invoke the service.

You can run the server and then run the client.

You can read more about it on the [gRPC website](http://www.grpc.io/docs/quickstart/java.html)

### SSL example

This example is the `Hello World example` with SSL configuration.

It shows a simple gRPC service that invokes a service that
takes takes an `HelloRequest` string wrapper and returns an `HelloReply` string wrapper. The communication uses SSL.

[gRPC server example](link:src/main/java/io/vertx/example/grpc/ssl/Server.java)

The gRPC service extends the `GreeterGrpc.GreeterVertxImplBase` generated class and
implements the service logic.

[gRPC client example](link:src/main/java/io/vertx/example/grpc/ssl/Client.java)

The gRPC client creates an instance of the `GreeterGrpc.newVertxStub` generated class and
then use it to invoke the service.

You can run the server and then run the client.

### Route Guide example

This example is adapted from the [gRPC examples](https://github.com/grpc/grpc-java/tree/master/examples).

This example shows the various kind of gRPC service calls:

- simple RPC
- server-side streaming RPC
- client-side streaming RPC
- bidirectional streaming RPC

[gRPC server example](link:src/main/java/io/vertx/example/grpc/routeguide/Server.java)
[gRPC client example](link:src/main/java/io/vertx/example/grpc/routeguide/Client.java)

You can run the server and then run the client.

You can read more about it on the [gRPC website](http://www.grpc.io/docs/tutorials/basic/java.html)


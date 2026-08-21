package com.innowise.userservice.server.interceptor;

import io.grpc.*;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import static com.innowise.userservice.config.ConstantConfiguration.SERVICE_SECRET_KEY_CONST;

@GrpcGlobalServerInterceptor
public class GrpcAuthenticationInterceptor implements ServerInterceptor {
  private static final Logger logger = LoggerFactory.getLogger(GrpcAuthenticationInterceptor.class);
  private final String serviceKey;

  public GrpcAuthenticationInterceptor(@Value("${service.secret}") String serviceKey) {
    this.serviceKey = serviceKey;
  }

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
    logger.debug("gRPC interceptor called");
    String receivedKey = metadata.get(SERVICE_SECRET_KEY_CONST);
    if (receivedKey != null && receivedKey.equals(serviceKey)) {
      return serverCallHandler.startCall(serverCall, metadata);
    } else {
      logger.warn("gRPC interceptCall received unexpected key {}", receivedKey);
      serverCall.close(
              Status.UNAUTHENTICATED,
              new Metadata()
      );
      return new ServerCall.Listener<ReqT>() {
      };
    }
  }
}
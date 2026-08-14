package com.innowise.userservice.client.interceptor;

import io.grpc.*;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import static com.innowise.userservice.config.ConstantConfiguration.SERVICE_SECRET_KEY_CONST;

@GrpcGlobalClientInterceptor
public class GrpcCallInterceptor implements ClientInterceptor {
  private static final Logger logger = LoggerFactory.getLogger(GrpcCallInterceptor.class);
  private final String serviceSecret;

  public GrpcCallInterceptor(@Value("${service.secret}") String serviceKey) {
    this.serviceSecret = serviceKey;
  }

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
    return new ForwardingClientCall.SimpleForwardingClientCall<>(
            channel.newCall(methodDescriptor, callOptions)) {
      @Override
      public void start(Listener<RespT> responseListener, Metadata headers) {
        headers.put(SERVICE_SECRET_KEY_CONST, serviceSecret);
        super.start(responseListener, headers);
      }
    };
  }
}
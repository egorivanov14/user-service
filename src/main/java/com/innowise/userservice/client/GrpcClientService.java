package com.innowise.userservice.client;

import com.google.protobuf.Empty;
import com.innowise.authentication_service.grpc.AuthenticationServiceGrpc;
import com.innowise.authentication_service.grpc.AuthenticationServiceProto;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GrpcClientService{
  private static final Logger logger = LoggerFactory.getLogger(GrpcClientService.class);

  @GrpcClient("userClientService")
  private AuthenticationServiceGrpc.AuthenticationServiceBlockingStub authenticationServiceBlockingStub;

  public void deleteUserCredentials(Long userId){
    logger.debug("dRPC deleteUserCredentials() called");
    AuthenticationServiceProto.DeleteUserCredentialsRequest request = AuthenticationServiceProto.DeleteUserCredentialsRequest
            .newBuilder()
            .setUserId(userId)
            .build();
   Empty response = authenticationServiceBlockingStub.deleteUserCredentials(request);
  }
}
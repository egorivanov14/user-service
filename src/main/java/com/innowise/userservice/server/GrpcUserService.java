package com.innowise.userservice.server;

import com.google.protobuf.Empty;
import com.innowise.userservice.dto.user.CreateUserRequest;
import com.innowise.userservice.dto.user.UserResponse;
import com.innowise.userservice.grpc.UserServiceGrpc;
import com.innowise.userservice.grpc.UserServiceProto;
import com.innowise.userservice.service.UserService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@GrpcService
public class GrpcUserService extends UserServiceGrpc.UserServiceImplBase {
  private static final Logger logger = LoggerFactory.getLogger(GrpcUserService.class);
  private final UserService userService;

  public GrpcUserService(UserService userService) {
    this.userService = userService;
  }

  @Override
  public void createUser(UserServiceProto.CreateUserRequest request,
                         StreamObserver<UserServiceProto.CreateUserResponse> responseObserver) {
    logger.debug("gRPC createUser() called");
    String birthdateString = request.getBirthdate();
    LocalDate birthDate;
    try {
      birthDate = LocalDate.parse(birthdateString);
    } catch (DateTimeParseException | IllegalArgumentException e) {
      logger.error("Failed to createUser, invalid parameter: {}", e.getMessage());
      responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
      return;
    }

    String name = request.getName();
    String surname = request.getSurname();
    String email = request.getEmail();

    try {
      CreateUserRequest createUserRequest = new CreateUserRequest(name, surname, birthDate, email);
      UserResponse userResponse = userService.create(createUserRequest);
      Long userId = userResponse.id();
      UserServiceProto.CreateUserResponse response = UserServiceProto.CreateUserResponse.newBuilder()
              .setUserId(userId)
              .build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      logger.error("Failed to createUser: {}", e.getMessage());
      responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void deleteUser(UserServiceProto.DeleteUserRequest request, StreamObserver<Empty> responseObserver) {
    logger.debug("gRPC deleteUser called");
    Long userId = request.getUserId();
    try {
      userService.delete(userId);
      responseObserver.onNext(Empty.getDefaultInstance());
      responseObserver.onCompleted();
    } catch (Exception e) {
      logger.error("Failed to deleteUser: {}", e.getMessage());
      responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void getUserInfo(UserServiceProto.GetUserInfoRequest request, StreamObserver<UserServiceProto.UserInfoResponse> responseObserver) {
    logger.debug("gRPC getUserInfo called");
    Long userId = request.getUserId();
    try {
      UserResponse userResponse = userService.findById(userId);
      UserServiceProto.UserInfoResponse response = UserServiceProto.UserInfoResponse.newBuilder()
              .setId(userResponse.id())
              .setName(userResponse.name())
              .setSurname(userResponse.surname())
              .setBirthDate(userResponse.birthDate().toString())
              .setEmail(userResponse.email())
              .setIsActive(userResponse.active())
              .setCreatedAt(userResponse.createdAt().toString())
              .setUpdatedAt(userResponse.updatedAt().toString())
              .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      logger.error("Failed to getUserInfo: {}", e.getMessage());
      responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
    }
  }
}
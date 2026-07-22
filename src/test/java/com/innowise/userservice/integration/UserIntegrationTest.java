package com.innowise.userservice.integration;

import com.innowise.userservice.controller.UserController;
import com.innowise.userservice.dto.user.CreateUserRequest;
import com.innowise.userservice.dto.user.FilterByNameAndSurnameRequest;
import com.innowise.userservice.dto.user.UpdateUserRequest;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.NoDataException;
import com.innowise.userservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static com.innowise.userservice.TestConstantConfiguration.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserIntegrationTest extends AbstractIntegrationTest {

  @Autowired
  private WebApplicationContext webApplicationContext;
  @Autowired
  private UserRepository userRepository;
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .build();
  }

  @AfterEach
  public void clearAll() {
    userRepository.deleteAll();
  }

  @Test
  void create_shouldCreateUser() throws Exception {
    CreateUserRequest createUserRequest = new CreateUserRequest(NAME, SURNAME, BIRTH_DATE, EMAIL);
    String jsonRequest = objectMapper.writeValueAsString(createUserRequest);

    mockMvc.perform(
            post("/api/users/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest)
    ).andExpect(status().isCreated());

    User user = userRepository.findByEmail(EMAIL).orElseThrow(() -> new NoDataException("User not found"));
    assertEquals(EMAIL, user.getEmail());
    assertEquals(NAME, user.getName());
    assertEquals(BIRTH_DATE, user.getBirthDate());
    assertEquals(SURNAME, user.getSurname());
  }

  @Test
  void create_duplicatedEmail_shouldThrowConflictException() throws Exception {
    CreateUserRequest createUserRequest = new CreateUserRequest(NAME, SURNAME, BIRTH_DATE, EMAIL);
    String jsonRequest = objectMapper.writeValueAsString(createUserRequest);

    mockMvc.perform(
            post("/api/users/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest)
    ).andExpect(status().isCreated());

    mockMvc.perform(
            post("/api/users/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest)
    ).andExpect(status().isConflict());
  }

  @Test
  void update_shouldUpdateUser() throws Exception {
    CreateUserRequest createUserRequest = new CreateUserRequest(NAME, SURNAME, BIRTH_DATE, EMAIL);
    String jsonRequest = objectMapper.writeValueAsString(createUserRequest);

    mockMvc.perform(
            post("/api/users/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest)
    ).andExpect(status().isCreated());

    UpdateUserRequest updateUserRequest = new UpdateUserRequest(null, null, null, UPDATE_EMAIL);
    String jsonUpdateRequest = objectMapper.writeValueAsString(updateUserRequest);

    User user = userRepository.findByEmail(EMAIL).orElseThrow(() -> new NoDataException("User not found"));
    Long userId = user.getId();

    mockMvc.perform(
            put("/api/users/update/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonUpdateRequest)
    ).andExpect(status().isOk());

    User userUpdated = userRepository.findById(userId).orElseThrow(() -> new NoDataException("User not found"));

    assertEquals(UPDATE_EMAIL, userUpdated.getEmail());
    assertEquals(NAME, userUpdated.getName());
    assertEquals(BIRTH_DATE, userUpdated.getBirthDate());
    assertEquals(SURNAME, userUpdated.getSurname());
  }

  @Test
  void delete_shouldDeleteUser() throws Exception {
    CreateUserRequest createUserRequest = new CreateUserRequest(NAME, SURNAME, BIRTH_DATE, EMAIL);
    String jsonRequest = objectMapper.writeValueAsString(createUserRequest);

    mockMvc.perform(
            post("/api/users/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest)
    ).andExpect(status().isCreated());

    User user = userRepository.findByEmail(EMAIL).orElseThrow(() -> new NoDataException("User not found"));
    Long userId = user.getId();

    mockMvc.perform(
            delete("/api/users/delete/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk());

    Optional<User> userOptional = userRepository.findById(userId);
    assertFalse(userOptional.isPresent());
  }

  @Test
  void activate_shouldActivateUser() throws Exception {
    CreateUserRequest createUserRequest = new CreateUserRequest(NAME, SURNAME, BIRTH_DATE, EMAIL);
    String jsonRequest = objectMapper.writeValueAsString(createUserRequest);

    mockMvc.perform(
            post("/api/users/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest)
    ).andExpect(status().isCreated());

    User user = userRepository.findByEmail(EMAIL).orElseThrow(() -> new NoDataException("User not found"));
    Long userId = user.getId();

    mockMvc.perform(
            post("/api/users/activate/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk());
  }

  @Test
  void deactivate_shouldDeactivateUser() throws Exception {
    CreateUserRequest createUserRequest = new CreateUserRequest(NAME, SURNAME, BIRTH_DATE, EMAIL);
    String jsonRequest = objectMapper.writeValueAsString(createUserRequest);

    mockMvc.perform(
            post("/api/users/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest)
    ).andExpect(status().isCreated());

    User user = userRepository.findByEmail(EMAIL).orElseThrow(() -> new NoDataException("User not found"));
    Long userId = user.getId();

    mockMvc.perform(
            post("/api/users/deactivate/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk());

    User deactivatedUser = userRepository.findByEmail(EMAIL).orElseThrow(() -> new NoDataException("User not found"));
    assertFalse(deactivatedUser.getActive());
  }

  @Test
  void findById_shouldReturnUserResponse() throws Exception {
    CreateUserRequest createUserRequest = new CreateUserRequest(NAME, SURNAME, BIRTH_DATE, EMAIL);
    String jsonRequest = objectMapper.writeValueAsString(createUserRequest);

    mockMvc.perform(
            post("/api/users/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest)
    ).andExpect(status().isCreated());

    User user = userRepository.findByEmail(EMAIL).orElseThrow(() -> new NoDataException("User not found"));
    Long userId = user.getId();

    mockMvc.perform(
            get("/api/users/get/{id}", userId)
            .contentType(MediaType.APPLICATION_JSON)).andExpect(result ->
            {
              assertEquals(EMAIL, user.getEmail());
              assertEquals(NAME, user.getName());
              assertEquals(BIRTH_DATE, user.getBirthDate());
              assertEquals(SURNAME, user.getSurname());
              assertEquals(userId, user.getId());
            });
  }

  @Test
  void getAll_shouldReturnPageOfUsers() throws Exception {
    CreateUserRequest createUserRequest = new CreateUserRequest(NAME, SURNAME, BIRTH_DATE, EMAIL);
    String jsonRequest = objectMapper.writeValueAsString(createUserRequest);

    mockMvc.perform(post("/api/users/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest))
            .andExpect(status().isCreated());

    mockMvc.perform(get("/api/users")
                    .param("page", "0")
                    .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].email").value(EMAIL));
  }

  @Test
  void getFiltered_shouldReturnFilteredUsers() throws Exception {
    CreateUserRequest createUserRequest = new CreateUserRequest(NAME, SURNAME, BIRTH_DATE, EMAIL);
    String jsonRequest = objectMapper.writeValueAsString(createUserRequest);

    mockMvc.perform(post("/api/users/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest))
            .andExpect(status().isCreated());

    FilterByNameAndSurnameRequest filterRequest = new FilterByNameAndSurnameRequest(NAME, SURNAME);
    String jsonFilterRequest = objectMapper.writeValueAsString(filterRequest);

    mockMvc.perform(post("/api/users/filter")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonFilterRequest)
                    .param("page", "0")
                    .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].email").value(EMAIL));
  }
}
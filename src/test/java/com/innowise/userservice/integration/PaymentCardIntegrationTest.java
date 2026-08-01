package com.innowise.userservice.integration;

import com.innowise.userservice.dto.card.CreatePaymentCardRequest;
import com.innowise.userservice.dto.card.FilterByOwnerNameAndSurnameRequest;
import com.innowise.userservice.dto.card.UpdatePaymentCardRequest;
import com.innowise.userservice.dto.user.CreateUserRequest;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.NoDataException;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static com.innowise.userservice.TestConstantConfiguration.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class PaymentCardIntegrationTest extends AbstractIntegrationTest {

  @Autowired
  private WebApplicationContext webApplicationContext;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PaymentCardRepository paymentCardRepository;
  @Autowired
  private ObjectMapper objectMapper;
  private MockMvc mockMvc;
  private Long userId;

  @BeforeEach
  void setUp() throws Exception {
    mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .build();

    CreateUserRequest createUserRequest = new CreateUserRequest(NAME, SURNAME, BIRTH_DATE, EMAIL);
    String jsonCreateUserRequest = objectMapper.writeValueAsString(createUserRequest);

    mockMvc.perform(post("/api/users/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonCreateUserRequest))
            .andExpect(status().isCreated());

    User user = userRepository.findByEmail(EMAIL).orElseThrow(()->new NoDataException("User no found"));
    userId = user.getId();
  }

  @AfterEach
  public void clearAll() {
    paymentCardRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void create_shouldCreatePaymentCard() throws Exception {
    CreatePaymentCardRequest request = new CreatePaymentCardRequest(
            userId, CARD_NUMBER, HOLDER, EXPIRE_DATE
    );
    createPaymentCard(request);

    PaymentCard paymentCard = paymentCardRepository.findByNumber(CARD_NUMBER).orElseThrow(()->new NoDataException("Payment card no found"));

    assertEquals(userId, paymentCard.getUser().getId());
    assertEquals(CARD_NUMBER, paymentCard.getNumber());
    assertEquals(HOLDER, paymentCard.getHolder());
    assertEquals(EXPIRE_DATE, paymentCard.getExpirationDate());
  }

  @Test
  void update_shouldUpdatePaymentCard() throws Exception {
    CreatePaymentCardRequest request = new CreatePaymentCardRequest(
            userId, CARD_NUMBER, HOLDER, EXPIRE_DATE
    );
    createPaymentCard(request);

    PaymentCard paymentCard = paymentCardRepository.findByNumber(CARD_NUMBER).orElseThrow(()->new NoDataException("Payment card no found"));
    Long paymentCardId = paymentCard.getId();

    UpdatePaymentCardRequest updatePaymentCardRequest = new UpdatePaymentCardRequest(NEW_CARD_NUMBER, null, null);
    String jsonUpdatePaymentCardRequest = objectMapper.writeValueAsString(updatePaymentCardRequest);

    mockMvc.perform(put("/api/payment-cards/update/{id}", paymentCardId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonUpdatePaymentCardRequest))
            .andExpect(status().isOk());

    PaymentCard updatedPaymentCard = paymentCardRepository.findById(paymentCardId).orElseThrow(()->new NoDataException("Payment card no found"));
    assertEquals(NEW_CARD_NUMBER, updatedPaymentCard.getNumber());
    assertEquals(HOLDER, updatedPaymentCard.getHolder());
    assertEquals(EXPIRE_DATE, updatedPaymentCard.getExpirationDate());
  }

  @Test
  void findById_shouldReturnPaymentCard() throws Exception {
    CreatePaymentCardRequest request = new CreatePaymentCardRequest(
            userId, CARD_NUMBER, HOLDER, EXPIRE_DATE
    );
    createPaymentCard(request);

    PaymentCard paymentCard = paymentCardRepository.findByNumber(CARD_NUMBER).orElseThrow(()->new NoDataException("Payment card no found"));
    Long paymentCardId = paymentCard.getId();

    mockMvc.perform(get("/api/payment-cards/{id}", paymentCardId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andExpect(result->{
              assertEquals(CARD_NUMBER, paymentCard.getNumber());
              assertEquals(HOLDER, paymentCard.getHolder());
              assertEquals(EXPIRE_DATE, paymentCard.getExpirationDate());
            });
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void activate_shouldActivatePaymentCard() throws Exception {
    CreatePaymentCardRequest request = new CreatePaymentCardRequest(
            userId, CARD_NUMBER, HOLDER, EXPIRE_DATE
    );
    createPaymentCard(request);

    PaymentCard paymentCard = paymentCardRepository.findByNumber(CARD_NUMBER).orElseThrow(()->new NoDataException("Payment card no found"));
    Long paymentCardId = paymentCard.getId();

    mockMvc.perform(post("/api/payment-cards/activate/{id}", paymentCardId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void deactivate_shouldDeactivatePaymentCard() throws Exception {
    CreatePaymentCardRequest request = new CreatePaymentCardRequest(
            userId, CARD_NUMBER, HOLDER, EXPIRE_DATE
    );
    createPaymentCard(request);

    PaymentCard paymentCard = paymentCardRepository.findByNumber(CARD_NUMBER).orElseThrow(()->new NoDataException("Payment card no found"));
    Long paymentCardId = paymentCard.getId();


    mockMvc.perform(post("/api/payment-cards/deactivate/{id}", paymentCardId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

    PaymentCard deactivatedPaymentCard = paymentCardRepository.findById(paymentCardId).orElseThrow(()->new NoDataException("Payment card no found"));
    assertEquals(INACTIVE, deactivatedPaymentCard.getActive());
  }

  @Test
  void delete_shouldDeletePaymentCard() throws Exception {
    CreatePaymentCardRequest request = new CreatePaymentCardRequest(
            userId, CARD_NUMBER, HOLDER, EXPIRE_DATE
    );
    createPaymentCard(request);

    PaymentCard paymentCard = paymentCardRepository.findByNumber(CARD_NUMBER).orElseThrow(()->new NoDataException("Payment card no found"));
    Long paymentCardId = paymentCard.getId();

    mockMvc.perform(delete("/api/payment-cards/delete/{id}", paymentCardId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

    Optional<PaymentCard> paymentCardOptional = paymentCardRepository.findById(paymentCardId);
    assertFalse(paymentCardOptional.isPresent());
  }

  @Test
  void findAllByUserId_shouldReturnCards() throws Exception {
    CreatePaymentCardRequest request = new CreatePaymentCardRequest(
            userId, CARD_NUMBER, HOLDER, EXPIRE_DATE
    );
    createPaymentCard(request);

    mockMvc.perform(get("/api/payment-cards/by-user/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].holder").value(HOLDER));
  }

  @Test
  void findAllByUserNameAndSurname_shouldReturnFilteredCards() throws Exception {
    CreatePaymentCardRequest request = new CreatePaymentCardRequest(
            userId, CARD_NUMBER, HOLDER, EXPIRE_DATE
    );
    createPaymentCard(request);

    FilterByOwnerNameAndSurnameRequest filterRequest = new FilterByOwnerNameAndSurnameRequest(NAME, SURNAME);
    String jsonFilter = objectMapper.writeValueAsString(filterRequest);

    mockMvc.perform(post("/api/payment-cards/by-user/name-surname-filter")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonFilter)
                    .param("page", "0")
                    .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].holder").value(HOLDER));
  }

  private void createPaymentCard(CreatePaymentCardRequest createPaymentCardRequest) throws Exception {
    String jsonCreatePaymentCardRequest = objectMapper.writeValueAsString(createPaymentCardRequest);

    mockMvc.perform(post("/api/payment-cards/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonCreatePaymentCardRequest))
            .andExpect(status().isCreated());
  }
}
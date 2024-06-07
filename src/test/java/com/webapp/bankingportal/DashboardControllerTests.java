package com.webapp.bankingportal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.jayway.jsonpath.JsonPath;
import com.webapp.bankingportal.dto.LoginRequest;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class DashboardControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private static Faker faker;
    private static ObjectMapper objectMapper;
    private static User user;
    private static String accountNumber;
    private static String token;
    private static boolean isLoggedIn = false;

    private static final int MAX_PASSWORD_LENGTH = 127;

    @BeforeAll
    public static void setup(){
        faker = new Faker();
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
    
    private void createAndLoginUser() throws Exception {
        if (isLoggedIn) {
            return;
        }

        user = new User();
        user.setName(faker.name().fullName());
        user.setEmail(faker.internet().safeEmailAddress());
        user.setAddress(faker.address().fullAddress());
        user.setPhoneNumber("+2010" + faker.number().digits(8));
        user.setPassword("!" + faker.internet().password(
                MAX_PASSWORD_LENGTH - 2,
                MAX_PASSWORD_LENGTH - 1,
                true,
                true));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccountNumber(accountNumber);
        loginRequest.setPassword(user.getPassword());

        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        token = JsonPath.read(responseBody, "$.token");

        isLoggedIn = true;
    }

    @Test
    public void test_get_account_details_authorized() throws Exception {
        createAndLoginUser();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/dashboard/account")
                .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber")
                        .value(accountNumber))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance")
                        .value(0.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountType")
                        .value("Savings"));
    }

    @Test
    public void test_get_account_details_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/dashboard/account"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_get_user_details_authorized() throws Exception {
        createAndLoginUser();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/dashboard/user")
                .header("Authorization", "Bearer " + token))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                        .value(user.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email")
                        .value(user.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address")
                        .value(user.getAddress()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phoneNumber")
                        .value(user.getPhoneNumber()));
    }

    @Test
    public void test_get_user_details_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/dashboard/user"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}

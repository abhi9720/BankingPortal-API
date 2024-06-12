package com.webapp.bankingportal;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.webapp.bankingportal.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class DashboardControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private HashMap<String, String> userDetails = null;

    private TestUtil testUtil;

    @BeforeEach
    public void setup() {
        testUtil = new TestUtil(mockMvc, userRepository);
    }

    private HashMap<String, String> createAndLoginUser() throws Exception {
        if (userDetails == null) {
            userDetails = testUtil.createAndLoginUser();
        }

        return userDetails;
    }

    @Test
    public void test_get_account_details_authorized() throws Exception {
        createAndLoginUser();

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/dashboard/account")
                .header("Authorization", "Bearer " + userDetails.get("token")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber")
                        .value(userDetails.get("accountNumber")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance")
                        .value(0.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountType")
                        .value("Savings"));
    }

    @Test
    public void test_get_account_details_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/dashboard/account"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_get_user_details_authorized() throws Exception {
        createAndLoginUser();

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/dashboard/user")
                .header("Authorization", "Bearer " + userDetails.get("token")))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                        .value(userDetails.get("name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email")
                        .value(userDetails.get("email")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address")
                        .value(userDetails.get("address")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phoneNumber")
                        .value(userDetails.get("phoneNumber")));
    }

    @Test
    public void test_get_user_details_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/dashboard/user"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}

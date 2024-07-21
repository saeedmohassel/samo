package com.wallet.walletapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.walletapp.model.dto.TokenRequest;
import com.wallet.walletapp.model.dto.UserDto;
import com.wallet.walletapp.model.entity.AppUser;
import com.wallet.walletapp.repository.UserRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for User API endpoints")
@Tag("integration")
class UserControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    String tokenPrefix = "Bearer ";


    @BeforeEach
    void setUp() {
        AppUser u1 = new AppUser();
        u1.setUsername("A1");
        u1.setPassword(passwordEncoder.encode("A2"));
        u1.setEmail("A3@A3");
        u1.setEnabled(true);
        u1.setRoles("User");
        userRepository.save(u1);

        AppUser u2 = new AppUser();
        u2.setUsername("B1");
        u2.setPassword(passwordEncoder.encode("B2"));
        u2.setEmail("B3@B3");
        u2.setEnabled(true);
        u2.setRoles("User,Admin");
        userRepository.save(u2);
    }

    @Test
    @Transactional
    void testGetLoginToken() throws Exception {
        String USERNAME = "A1";
        String PASSWORD = "A2";

        TokenRequest request = new TokenRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void testGetUserByUsername() throws Exception {
        String USERNAME = "A1";
        String PASSWORD = "A2";

        TokenRequest request = new TokenRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);
        String requestJson = objectMapper.writeValueAsString(request);

        ResultActions resultActions = mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject json = new JSONObject(contentAsString);
        String token = tokenPrefix + json.getString("token");

        mockMvc.perform(get("/user/" + USERNAME)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USERNAME));
    }

    @Test
    @Transactional
    void testRegisterUser() throws Exception {
        String USERNAME = "C1";
        String PASSWORD = "C2";

        UserDto user = new UserDto();
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        user.setEmail("C3@C3");

        String requestJson = objectMapper.writeValueAsString(user);
        requestJson = requestJson.replaceAll("}", ",\"password" + "\":\"" + PASSWORD + "\"}");

        ResultActions resultActions = mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject json = new JSONObject(contentAsString);
        String token = tokenPrefix + json.getString("token");
        mockMvc.perform(get("/user/" + USERNAME)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USERNAME));
    }

    @Test
    @Transactional
    void testGetUserByUsernameWithUserAccessingAnotherUsersInfo() throws Exception {
        String USERNAME = "A1";
        String PASSWORD = "A2";

        String OTHER_USERNAME = "B1";

        TokenRequest request = new TokenRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);
        String requestJson = objectMapper.writeValueAsString(request);

        ResultActions resultActions = mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject json = new JSONObject(contentAsString);
        String token = tokenPrefix + json.getString("token");
        mockMvc.perform(get("/user/" + OTHER_USERNAME)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isForbidden());
    }

}
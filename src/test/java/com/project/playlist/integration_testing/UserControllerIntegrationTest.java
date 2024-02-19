package com.project.playlist.integration_testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.playlist.dto.UserDTO;
import com.project.playlist.dto.UserRequest;
import com.project.playlist.integration_testing.util.MySqlIntegrationTest;
import com.project.playlist.model.User;
import com.project.playlist.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest extends MySqlIntegrationTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    private User user1;

    UserRequest userRequest;
    @BeforeEach
    void setUp() throws Exception {
        user1 = userRepository.save(User.builder().username("username1").email("email1").password("pass1").build());
    }

    @Test
    public void givenValidUserRequest_whenRegisterNewUser_thenReturnRegisteredUser() throws Exception {
        //arrange
        userRequest = UserRequest.builder()
                .username("username2")
                .email("email2@gmail.com")
                .password("pass2")
                .build();
        String userRequestJson = objectMapper.writeValueAsString(userRequest);
        //act
        MvcResult result = mvc.perform(post("/api/users/register")
                        .content(userRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserDTO userDTO = objectMapper.readValue(result.getResponse().getContentAsString(), UserDTO.class);
        List<User> allUsers = userRepository.findAll();
        //assert
        assertAll(
                () -> assertEquals(2, allUsers.size()),
                () -> assertEquals("username2", userDTO.getUsername()),
                () -> assertEquals("email2@gmail.com", userDTO.getEmail())
        );
    }

    @Test
    public void givenExistingUserId_whenRegisterNewUser_thenExpectConflictStatus() throws Exception {
        //arrange
        userRequest = UserRequest.builder()
                .username("username1")
                .email("email1@gmail.com")
                .password("pass1")
                .build();
        String userRequestJson = objectMapper.writeValueAsString(userRequest);
        //act
        //assert
        mvc.perform(post("/api/users/register")
                        .content(userRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.CONFLICT.value()));
    }

    @Test
    public void givenBlankUsername_whenRegisterNewUser_thenExpectBadRequestStatus() throws Exception {
        //arrange
        userRequest = UserRequest.builder()
                .username("  ")
                .email("email2@gmail.com")
                .password("pass2")
                .build();
        String userRequestJson = objectMapper.writeValueAsString(userRequest);
        //act
        //assert
        mvc.perform(post("/api/users/register")
                        .content(userRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void givenNotEmail_whenRegisterNewUser_thenExpectBadRequestStatus() throws Exception {
        //arrange
        userRequest = UserRequest.builder()
                .username("username2")
                .email("notEmail")
                .password("pass2")
                .build();
        String userRequestJson = objectMapper.writeValueAsString(userRequest);
        //act
        //assert
        mvc.perform(post("/api/users/register")
                        .content(userRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void givenShortPassword_whenRegisterNewUser_thenExpectBadRequestStatus() throws Exception {
        //arrange
        userRequest = UserRequest.builder()
                .username("username2")
                .email("email2@gmail.com")
                .password("pas")
                .build();
        String userRequestJson = objectMapper.writeValueAsString(userRequest);
        //act
        //assert
        mvc.perform(post("/api/users/register")
                        .content(userRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void givenNullUserRequest_whenRegisterNewUser_thenExpectBadRequestStatus() throws Exception {
        //arrange
        userRequest = null;
        String userRequestJson = objectMapper.writeValueAsString(userRequest);
        //act
        //assert
        mvc.perform(post("/api/users/register")
                        .content(userRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

}

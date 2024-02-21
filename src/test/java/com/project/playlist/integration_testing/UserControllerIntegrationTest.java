package com.project.playlist.integration_testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.playlist.dto.UserDTO;
import com.project.playlist.dto.UserRequest;
import com.project.playlist.integration_testing.util.MySqlIntegrationTest;
import com.project.playlist.model.Role;
import com.project.playlist.model.User;
import com.project.playlist.repository.RoleRepository;
import com.project.playlist.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserControllerIntegrationTest extends MySqlIntegrationTest {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    private User testUser;
    private Role testRole;
    UserRequest userRequest;

    @BeforeEach
    void setUp() throws Exception {
        //Authentication
        testRole = roleRepository.save(Role.builder().name("ROLE_USER").build());
        Set<Role> roles = new HashSet<>();
        roles.add(testRole);
        testUser = userRepository.save(User.builder().username("username1").email("email1@gmail.com").password(passwordEncoder.encode("pass1")).roles(roles).build());
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
        List<Role> roleList = new ArrayList<>(userDTO.getRoles());
        //assert
        assertAll(
                () -> assertEquals(2, allUsers.size()),
                () -> assertEquals("username2", userDTO.getUsername()),
                () -> assertEquals("email2@gmail.com", userDTO.getEmail()),
                () -> assertEquals("ROLE_USER", roleList.getFirst().getName())
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

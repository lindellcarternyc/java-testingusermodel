package com.lambdaschool.usermodel.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdaschool.usermodel.UserModelApplicationTesting;
import com.lambdaschool.usermodel.models.Role;
import com.lambdaschool.usermodel.models.User;
import com.lambdaschool.usermodel.models.UserRoles;
import com.lambdaschool.usermodel.models.Useremail;
import com.lambdaschool.usermodel.services.UserService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WithMockUser(username = "admin",
    roles = {"USER", "ADMIN"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = UserModelApplicationTesting.class,
    properties = "command.line.runner.enabled=false")
@AutoConfigureMockMvc
public class UserControllerUnitTestNoDB {
    private static final String API_START = "/users";
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    List<User> userList = new ArrayList<>();
    @Before
    public void setUp() {
        Role r1 = new Role("admin");
        Role r2 = new Role("user");
        Role r3 = new Role("data");

        // admin, data, user
        User u1 = new User("TEST admin",
                "password",
                "admin@lambdaschool.local");
        u1.setUserid('1');
        u1.getRoles()
                .add(new UserRoles(u1,
                        r1));
        u1.getRoles()
                .add(new UserRoles(u1,
                        r2));
        u1.getRoles()
                .add(new UserRoles(u1,
                        r3));
        u1.getUseremails()
                .add(new Useremail(u1,
                        "admin@email.local"));
        u1.getUseremails()
                .add(new Useremail(u1,
                        "admin@mymail.local"));

        userList.add(u1);

        // data, user
        User u2 = new User("TEST cinnamon",
                "1234567",
                "cinnamon@lambdaschool.local");
        u2.setUserid(2);
        u2.getRoles()
                .add(new UserRoles(u2,
                        r2));
        u2.getRoles()
                .add(new UserRoles(u2,
                        r3));
        u2.getUseremails()
                .add(new Useremail(u2,
                        "cinnamon@mymail.local"));
        u2.getUseremails()
                .add(new Useremail(u2,
                        "hops@mymail.local"));
        u2.getUseremails()
                .add(new Useremail(u2,
                        "bunny@email.local"));
        userList.add(u2);

        // user
        User u3 = new User("TEST barnbarn",
                "ILuvM4th!",
                "barnbarn@lambdaschool.local");
        u3.setUserid(3);
        u3.getRoles()
                .add(new UserRoles(u3,
                        r2));
        u3.getUseremails()
                .add(new Useremail(u3,
                        "barnbarn@email.local"));
        userList.add(u3);

        User u4 = new User("puttat",
                "password",
                "puttat@school.lambda");
        u4.setUserid(4);
        u4.getRoles()
                .add(new UserRoles(u4,
                        r2));
        userList.add(u4);

        User u5 = new User("misskitty",
                "password",
                "misskitty@school.lambda");
        u5.setUserid(5);
        u5.getRoles()
                .add(new UserRoles(u5,
                        r2));
        userList.add(u5);

        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void listAllUsers() throws Exception {
        String apiUrl = API_START + "/users";
        Mockito.when(userService.findAll())
                .thenReturn(userList);

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb)
                .andReturn();
        String tr = r.getResponse()
                .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(userList);

        assertEquals(er, tr);
    }

    @Test
    public void getUserById() throws Exception {
        String apiUrl = API_START + "/user/1";
        Mockito.when(userService.findUserById(1))
                .thenReturn(userList.get(0));

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl)
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(rb)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("test admin")));
    }

    @Test
    public void getUserByIdNotFound() throws Exception {
        String apiUrl = API_START + "/user/100";
        Mockito.when(userService.findUserById(100))
                .thenReturn(null);

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb)
                .andDo(print())
                .andReturn();
        String tr = r.getResponse()
                .getContentAsString();

        String er = "";

        assertEquals(er, tr);
    }

    @Test
    public void getUserByName() {
    }

    @Test
    public void getUserLikeName() {
    }

    @Test
    public void addNewUser() {
    }

    @Test
    public void updateFullUser() {
    }

    @Test
    public void updateUser() {
    }

    @Test
    public void deleteUserById() {
    }

    @Test
    public void getCurrentUserInfo() {
    }
}
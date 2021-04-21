package com.lambdaschool.usermodel.services;

import com.lambdaschool.usermodel.UserModelApplicationTesting;
import com.lambdaschool.usermodel.exceptions.ResourceNotFoundException;
import com.lambdaschool.usermodel.models.Role;
import com.lambdaschool.usermodel.models.User;
import com.lambdaschool.usermodel.models.UserRoles;
import com.lambdaschool.usermodel.models.Useremail;
import com.lambdaschool.usermodel.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserModelApplicationTesting.class,
    properties = { "command.line.runner.enabled=false"})
public class UserServiceImplUnitTestNoDB {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

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

        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void findUserById() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(userList.get(0)));

        assertEquals("test admin",
                userService.findUserById(1).getUsername());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void findUserByIdNotFound() {
        Mockito.when(userRepository.findById(100L))
                .thenReturn(Optional.empty());
        userService.findUserById(100);
    }

    @Test
    public void findByNameContaining() {

        Mockito.when(userRepository.findByUsernameContainingIgnoreCase("any name"))
                .thenReturn(userList);

        List<User> result = userService.findByNameContaining("any name");

        assertEquals(userList.size(), result.size());
    }

    @Test
    public void findByNameContainingNotFound() {
        Mockito.when(userRepository.findByUsernameContainingIgnoreCase("not found"))
                .thenReturn(new ArrayList<>());
        assertEquals(0, userService.findByNameContaining("not found").size());
    }

    @Test
    public void findAll() {
        Mockito.when(userRepository.findAll())
                .thenReturn(userList);

        assertEquals(userList.size(), userService.findAll().size());
    }

    @Test
    public void delete() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(userList.get(0)));
        Mockito.doNothing()
                .when(userRepository)
                .deleteById(1L);

        userService.delete(1);

        assertEquals(5, userList.size());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deleteFailed() {
        Mockito.when(userRepository.findById(100L))
                .thenReturn(Optional.empty());
        Mockito.doNothing()
                .when(userRepository)
                .deleteById(100L);

        userService.delete(100);

        assertEquals(5, userList.size());
    }
    @Test
    public void findByName() {
    }

    @Test
    public void save() {
    }

    @Test
    public void update() {
    }

    @Test
    public void deleteAll() {
    }
}
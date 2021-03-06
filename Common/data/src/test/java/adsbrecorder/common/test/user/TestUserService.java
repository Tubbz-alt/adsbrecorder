package adsbrecorder.common.test.user;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import adsbrecorder.common.test.conf.InMemoryDBTestConfiguration;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.service.UserService;

@EnableJpaRepositories(basePackages = "adsbrecorder.user.repo")
@TestPropertySource(locations = "/test.properties")
@ContextConfiguration(classes = InMemoryDBTestConfiguration.class)
@ComponentScan(basePackages = {
        "adsbrecorder.user",
        "adsbrecorder.user.service"})
@EntityScan(basePackages = {
        "adsbrecorder.user.entity"})
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestUserService {

    @Autowired
    public UserService userService;

    @Test
    public void testIsUsernameExist() {
        boolean exist = userService.isUsernameExist("Some non-exists name");
        assertFalse(exist);
    }

    @Test
    public void testCreateNewUser() {
        String username = "TestUser";
        String password = "TestUserPassword";
        User newUser = userService.createNewUser(username, password);
        assertTrue(newUser.getUserId() > 0L);
        boolean exist = userService.isUsernameExist(newUser.getUsername());
        assertTrue(exist);
    }

    @Test
    public void testUserLogin() {
        String username = "TestUserLogin";
        String password = "TestUserPassword";
        userService.createNewUser(username, password);
        User user = userService.login(username, password);
        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertNotEquals(password, user.getPassword());
    }
    
    @Test
    public void testUserLoginHash() {
        String username = "TestUserLoginHash";
        String password = "TestUserPassword";
        userService.createNewUser(username, password);
        User user = userService.login(username, password);
        User userHash = userService.loginHash(user.getUsername(), user.getPassword());
        assertEquals(user, userHash);
        User incorrectHash = userService.loginHash(user.getUsername(), user.getPassword() + "incorrect");
        assertNull(incorrectHash);
    }

    @Test
    public void testFindUserByName() {
        String username = "TestUserFindUserByName";
        String password = "TestUserPassword";
        User newUser = userService.createNewUser(username, password);
        User findByName = userService.findUserByName(username);
        assertEquals(newUser, findByName);
        User unauthorizedUser = userService.findUserByName("Non-exists username");
        assertEquals(unauthorizedUser.getUserId(), Long.valueOf(-1L));
    }

    @Test
    public void testFindUserById() {
        String username = "TestUserFindUserById";
        String password = "TestUserPassword";
        User newUser = userService.createNewUser(username, password);
        User findById = userService.findUserById(newUser.getUserId());
        assertEquals(newUser, findById);
        User unauthorizedUser = userService.findUserById(-2L);
        assertEquals(unauthorizedUser.getUserId(), Long.valueOf(-1L));
    }
}

package adsbrecorder.user.service;

import java.security.Key;

import adsbrecorder.user.entity.User;

public interface UserService {
    boolean isUsernameExist(String username);
    User login(String username, String password);
    User loginHash(String username, String passwordHash);
    User authenticate(String username, String password);
    User authorize(User user);
    User createNewUser(String username, String password);
    User findUserByName(String username);
    User findUserById(Long userId);
    Key getSecretSigningKey();
}

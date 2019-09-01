package adsbrecorder.user.controller;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import adsbrecorder.user.UserServiceMappings;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.service.UserService;

@RestController
public class UserManagementController implements UserServiceMappings {

    private UserService userService;

    @Autowired
    public UserManagementController(UserService userService) {
        this.userService = requireNonNull(userService);
    }

    @GetMapping(LIST_OF_USERS)
    public Object listOfUsers(HttpServletRequest request,
            @RequestParam(name = "p", required = false, defaultValue = "1") int page,
            @RequestParam(name = "n", required = false, defaultValue = "5") int amount) {
        if (page <= 0) page = -page;
        if (page > 0) page--;
        if (amount < 0) amount = -amount;
        if (amount == 0) amount = 5;
        long[] count = new long[1];
        List<User> users = this.userService.findUsers(request.getParameterMap(), page, amount, count);
        if (users.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No user found"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("users", users,
                             "totalCount", count[0]));
    }
}
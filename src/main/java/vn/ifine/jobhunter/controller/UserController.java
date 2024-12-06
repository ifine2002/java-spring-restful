package vn.ifine.jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ifine.jobhunter.domain.User;
import vn.ifine.jobhunter.service.UserService;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/create")
    public String createNewUser() {
        User user = new User();
        user.setEmail("buivandoan@gmail.com");
        user.setName("Bùi Đoàn");
        user.setPassword("123456");

        this.userService.handleCreateUser(user);
        return "Create user";
    }
}

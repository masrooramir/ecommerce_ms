package com.baloch.user.controller;


import com.baloch.user.dto.UserRequest;
import com.baloch.user.dto.UserRequestUpdate;
import com.baloch.user.model.User;
import com.baloch.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/v1/user")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @GetMapping("/{userId}")
    public Object user(@PathVariable String userId){
        return userService.user(userId);
    }

    @PostMapping
    public Object createUser(@RequestBody UserRequest userRequest){
        System.out.println("_____________________________"+userRequest);
        return userService.createUser(userRequest);
    }

    @PatchMapping("/{userId}")
    public Object updateUser(@PathVariable String userId, @RequestBody UserRequestUpdate userRequestUpdate){
        return userService.updateUser(userId,  userRequestUpdate);
    }
}







//@PatchMapping("/password-update/{userId}")
//public Object updateUserPassword(@PathVariable String userId, @RequestBody UserRequestPasswordUpdate passwordUpdate){
//    return userService.updateUserPassword(userId, passwordUpdate);
//}
//
//@DeleteMapping("/{userId}")
//public Object deleteUser(@PathVariable String userId, @RequestBody String password){
//    return userService.deleteUser(userId, password);
//}
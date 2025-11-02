package com.baloch.auth.controller;

import com.baloch.auth.dto.GenericResponseDTO;
import com.baloch.auth.dto.PasswordDTO;
import com.baloch.auth.dto.RequestDTO;
import com.baloch.auth.dto.UsernameDTO;
import com.baloch.auth.model.UserCredentials;
import com.baloch.auth.model.UserDetailsPrincipal;
import com.baloch.auth.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/validate-token")
    public GenericResponseDTO validateToken(@AuthenticationPrincipal UserDetailsPrincipal user){
        GenericResponseDTO a = authService.validateToken(user);
        System.out.println(a);
        return a;
    }

    @PostMapping("/login")
    public Object login(@RequestBody UserCredentials user){
        return authService.login(user);
    }

    @PostMapping("/register")
    public Object register(@RequestBody RequestDTO userRequestBody){
        return authService.register(userRequestBody);
    }

    @PutMapping("/update-username")
    public Object updateUsername(@RequestBody UsernameDTO username,
                                          Authentication principal) throws Exception {
        return authService.updateUsername(username,principal);
    }

    @PutMapping("/update-password")
    public Object updatePassword(@RequestBody PasswordDTO password,
                                          Authentication principal) throws Exception {
        return authService.updatePassword(password,principal);
    }
}
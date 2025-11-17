package com.baloch.auth.service;

import com.baloch.auth.config.SecurityConfig;
import com.baloch.auth.dto.*;
import com.baloch.auth.handlers.HandlerMethod;
import com.baloch.auth.model.UserCredentials;
import com.baloch.auth.model.UserDetailsPrincipal;
import com.baloch.auth.repository.AuthRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {
    private CustomUserDetailsService customUserDetailsService;
    private final AuthRepository authRepository;
    private final SecurityConfig customSecurityConfig;
    private HandlerMethod handlerMethod;
    private JWTService jwtService;
    private RestTemplate restTemplate;

    public GenericResponseDTO validateToken(UserDetailsPrincipal user){
        GenericResponseDTO responseDTO= new GenericResponseDTO();
        responseDTO.setUsername(user.getUsername());

        Collection<? extends GrantedAuthority> grantedAuthorities = user.getAuthorities();
        Collection<GrantedAuthority> roles = new ArrayList<>();
        for (GrantedAuthority authority : grantedAuthorities) {
            roles.add(authority);
//            if (authority instanceof Role) {
//                roles.add((Role) authority);
//            } else {
//                // Handle cases where the authority is not a Role, e.g., log a warning or throw an exception
//                System.err.println("Warning: GrantedAuthority is not a Role: " + authority.getClass().getName());
//            }
        }
        responseDTO.setRoles(roles);
        return responseDTO;
    }

    public Object login(UserCredentials user) {
        String username = user.getUsername();
        String password = user.getPassword();

        Authentication authentication = customSecurityConfig
                .authenticationProvider(customUserDetailsService)
                .authenticate(new UsernamePasswordAuthenticationToken(username,password));
        if (authentication.isAuthenticated()){
            return jwtService.generateToken(user.getUsername());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Authentication Failed");
    }

    @Transactional
    public Object register(RequestDTO userRequestBody) {
        UserCredentials userCredentials = getUserCredentialsMethod(userRequestBody);
        userCredentials.setPassword(customSecurityConfig.passwordEncoder().encode(userCredentials.getPassword()));

        try {
            ResponseDTO responseDTO = getUserCredentialsResponseMethod(authRepository.save(userCredentials));

            RequestForUser requestForUser = new RequestForUser(responseDTO.getUser_id(),"responseDTO.getUsername()");

            restTemplate.postForEntity("http://localhost:8082/api/v1/user",requestForUser,Object.class);

            GenericResponseBody genericResponseBody = handlerMethod.genericResponseBodyMethod(
                    201,"New User Created Successfully",
                    responseDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(genericResponseBody);
        }catch (Exception e){

            GenericResponseBody genericResponseBody = handlerMethod.genericResponseBodyMethod(500,
                    e.toString());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponseBody);
        }
    }


    public Object updateUsername(UsernameDTO username, Authentication principal) throws Exception {
        UserDetails userDetails = (UserDetails) principal.getPrincipal();

        UserCredentials userCredentials = authRepository.findByUsername(userDetails.getUsername());
        if(userCredentials ==null){
            GenericResponseBody genericResponseBody = handlerMethod.genericResponseBodyMethod(404,
                    "User Not Found Error!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(genericResponseBody);
        }
        UserCredentials userCredentialsWithMatchingUsername = authRepository.findByUsername(username.getUsername());
        if(userCredentialsWithMatchingUsername !=null){
            GenericResponseBody genericResponseBody = handlerMethod.genericResponseBodyMethod(400,
                    "Username is already taken!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(genericResponseBody);
        }
        try {
            userCredentials.setUsername(username.getUsername());
            ResponseDTO responseDTO = getUserCredentialsResponseMethod(authRepository.save(userCredentials));
            GenericResponseBody genericResponseBody = handlerMethod.genericResponseBodyMethod(
                    201,"Username has been updated Successfully",
                    responseDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(genericResponseBody);
        }catch (Exception e){
            GenericResponseBody genericResponseBody = handlerMethod.genericResponseBodyMethod(500,
                    e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponseBody);
        }
    }


    public Object updatePassword(PasswordDTO password, Authentication principal) throws Exception {
        UserDetails userDetails = (UserDetails) principal.getPrincipal();

        UserCredentials userCredentials = authRepository.findByUsername(userDetails.getUsername());
        if(userCredentials ==null){
            GenericResponseBody genericResponseBody = handlerMethod.genericResponseBodyMethod(404,
                    "User Not Found Error!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(genericResponseBody);
        }
        try {
            userCredentials.setPassword(customSecurityConfig.passwordEncoder().encode(password.getPassword()));
            ResponseDTO responseDTO = getUserCredentialsResponseMethod(authRepository.save(userCredentials));
            GenericResponseBody genericResponseBody = handlerMethod.genericResponseBodyMethod(
                    201,"Password has been updated Successfully",
                    responseDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(genericResponseBody);
        }catch (Exception e){
            throw new Exception(e.toString());
        }
    }

    public UserCredentials getUserCredentialsMethod(RequestDTO userRequest){
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setUsername(userRequest.getUsername());
        userCredentials.setPassword(userRequest.getPassword());
        return userCredentials;
    }

    public ResponseDTO getUserCredentialsResponseMethod(UserCredentials userCredentials){
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setUser_id(userCredentials.getUser_id());
        responseDTO.setUsername(userCredentials.getUsername());
        responseDTO.setRole(String.valueOf(userCredentials.getRole()));
        return responseDTO;
    }
}

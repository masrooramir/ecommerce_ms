package com.baloch.user.service;

import com.baloch.user.core.handlers.HandlerMethod;
import com.baloch.user.dto.*;
import com.baloch.user.model.User;
import com.baloch.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;


@AllArgsConstructor
@Service
public class UserService{
    private UserRepository userRepository;
    private HandlerMethod handler;

    // SINGLE USER SERVICE
    public Object user(String userId){
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                UserResponse userResponse = userResponseMethod(user);

                GenericResponseBody genericResponseBody = handler.genericResponseBodyMethod(200,
                        "User with " + userId + " found successfully", userResponse);
                return ResponseEntity.status(200).body(genericResponseBody);
            }
            GenericResponseBody genericResponseBody = handler.genericResponseBodyMethod(404,
                    "User with " + userId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(genericResponseBody);
        } catch(Exception e){
            GenericResponseBody genericResponseBody = handler.genericResponseBodyMethod(500,
                    e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponseBody);
        }
    }



    // CREATE-USER SERVICE
    public Object createUser(UserRequest userRequest){
        System.out.println();
        if (userRequest.getId()==null || userRequest.getUsername()==null){
            System.out.println("''''''''''''''''''''");
            throw new RuntimeException("Error Occured");
        }
        System.out.println("---------------");
        User user = userRequestMethod(userRequest);

        try {
            User savedUser = userRepository.save(user);
            UserResponse userResponse = userResponseMethod(savedUser);
            GenericResponseBody genericResponseBody = handler.genericResponseBodyMethod(201,
                    "New User has been created successfully", userResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(genericResponseBody);
        } catch (Exception e){
            GenericResponseBody genericResponseBody = handler.genericResponseBodyMethod(500,
                    e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponseBody);
        }
    }

    // UPDATE SERVICE
    public Object updateUser(String userId , UserRequestUpdate userRequestUpdate) {
        User user = userRepository.findById(userId).orElse(null);
        if(user==null){
            GenericResponseBody genericResponseBody= handler.genericResponseBodyMethod(404,"Failed to update User with id "+userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(genericResponseBody);
        }
            user.setEmail(userRequestUpdate.getEmail());
            user.setName(userRequestUpdate.getName());
            user.setAge(userRequestUpdate.getAge());

        try {
            User updatedUser = userRepository.save(user);
            UserResponse userResponse = userResponseMethod(updatedUser);
            GenericResponseBody genericResponseBody = handler.genericResponseBodyMethod(202,"User with new Data has been updated successfully",
                    userResponse);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(genericResponseBody);
        }catch (Exception e){
            GenericResponseBody genericResponseBody = handler.genericResponseBodyMethod(500,e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponseBody);
        }
    }

    User userRequestMethod(UserRequest userRequest){
        User user = new User();
        user.setUser_id(userRequest.getId());
        user.setUsername(userRequest.getUsername());
        return user;
    }

    UserResponse userResponseMethod(User user){
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getUser_id());
        userResponse.setUsername(user.getUsername());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setAge(user.getAge());
        return userResponse;
    }
}














//Would setup these Urls in Auth Service
// UPDATE PASSWORD SERVICE
//    public Object updateUserPassword(String userId , UserRequestPasswordUpdate userRequest) {
//        User user = userRepository.findById(userId).orElse(null);
//        if(user==null){
//            GenericResponseBody genericResponseBody= handler.genericResponseBodyMethod(404,"Failed to update User with id "+userId);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(genericResponseBody);
//        }
//
//        user.setPassword(userRequest.getPassword());
//
//        try {
//            User updatedUser = userRepository.save(user);
//            UserResponse userResponse = userResponseMethod(updatedUser);
//            GenericResponseBody genericResponseBody = handler.genericResponseBodyMethod(202,"User with new Data has been updated successfully",
//                    userResponse);
//            return ResponseEntity.status(HttpStatus.ACCEPTED).body(genericResponseBody);
//        }catch (Exception e){
//            GenericResponseBody genericResponseBody = handler.genericResponseBodyMethod(500,e.toString());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponseBody);
//        }
//    }
///
//
//    //DELETE SERVICE
//    public Object deleteUser(String userId, String password){
//        if(password == null){
//            GenericResponseBody genericResponseBody = handler.genericResponseBodyMethod(500,
//                    "Enter password to delete the user account!");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponseBody);
//        }
//        User user = userRepository.findById(userId).orElse(null);
//        if(user!=null) {
//            if(user.getPassword()!=password){
//                GenericResponseBody genericResponseBody = handler.genericResponseBodyMethod(500,
//                        "Password for the user with id "+userId+" is incorrect!");
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponseBody);
//            }
//            try {
//                userRepository.deleteById(userId);
//                GenericResponseBody genericResponseBody = handler.genericResponseBodyMethod(202,
//                        "User has been deleted successfully");
//                return ResponseEntity.status(HttpStatus.ACCEPTED).body(genericResponseBody);
//            } catch (Exception e) {
//                GenericResponseBody genericResponseBody = handler.genericResponseBodyMethod(500,
//                        e.toString());
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponseBody);
//            }
//        }
//        GenericResponseBody genericResponseBody = handler.genericResponseBodyMethod(404,
//                "User with "+userId+" not found");
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(genericResponseBody);
//    }
package com.baloch.api_gateway.dto.User;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
public class User {
    private String username;
    private Object roles;

    public User() {
    }
}
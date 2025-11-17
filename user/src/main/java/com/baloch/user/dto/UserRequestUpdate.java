package com.baloch.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRequestUpdate {
    private String email;
    private String name;
    private int age;
}

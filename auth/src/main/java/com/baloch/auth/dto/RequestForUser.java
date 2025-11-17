package com.baloch.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestForUser {
    private String id;
    private String username;
}

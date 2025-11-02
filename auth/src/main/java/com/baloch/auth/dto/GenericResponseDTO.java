package com.baloch.auth.dto;

import com.baloch.auth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Data
@AllArgsConstructor
@Component
public class GenericResponseDTO {
    private String username;
    private Collection<GrantedAuthority> roles;
    
    public GenericResponseDTO() {
    }
}

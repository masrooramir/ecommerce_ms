package com.baloch.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @Column(name = "custom_id", unique = true, nullable = false)
    private String user_id;

    @Column(unique = true,nullable = false)
    private String username;

    private String name;

    @Column(unique = true)
    private String email;

    private int age;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public User() {

    }
}

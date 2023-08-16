package com.parishjain.SpringSecurityProject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class MyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String mobile;
    @Column(unique = true)
    private String email;
    private Boolean enable;
    private String password;
    private String role;  // ROLE_USER // ROLE_A
    private String verificationCode;
    private Boolean isAccountNonLocked;
    private Integer failedAttempt;
    private Date lockTime;

}

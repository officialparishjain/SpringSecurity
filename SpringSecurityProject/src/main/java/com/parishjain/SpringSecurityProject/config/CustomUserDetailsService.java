package com.parishjain.SpringSecurityProject.config;

import com.parishjain.SpringSecurityProject.model.MyUser;
import com.parishjain.SpringSecurityProject.service.MyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    MyUserService myUserService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        MyUser myUser =  myUserService.findByEmail(email);
        if(myUser == null){
            throw new UsernameNotFoundException("Username not found");
        }
        else{
            return new CustomUserDetails(myUser);
        }
    }
}

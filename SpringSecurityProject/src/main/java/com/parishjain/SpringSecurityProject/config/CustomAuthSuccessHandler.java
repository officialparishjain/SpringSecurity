package com.parishjain.SpringSecurityProject.config;

import com.parishjain.SpringSecurityProject.model.MyUser;
import com.parishjain.SpringSecurityProject.service.MyUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Set;

/*
        In the context of Java web applications, an AuthenticationSuccessHandler class is responsible for handling
        successful user authentication events. It is a part of the Spring Security framework, which provides security
        features for Java applications.

        When a user successfully logs in, Spring Security triggers an authentication success event.
        The AuthenticationSuccessHandler class allows you to define custom behavior that should occur when this event happens.
        This behavior could include tasks such as:

        Redirecting the user to a specific page or URL after successful login.
        Setting session attributes or updating user-related information in the application's context.
        Logging successful login events or updating user login statistics.
        Sending notifications or alerts to administrators or users.
        Customizing the response or rendering a different view based on the user's role or authentication method.
 */
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    MyUserService myUserService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        MyUser myUser = customUserDetails.getMyUser();

        if(myUser != null) myUserService.resetAttempt(myUser.getEmail());

        if(roles.contains("ROLE_ADMIN")){
            response.sendRedirect("/admin/home");
        }
        else {
            response.sendRedirect("/user/home");

        }
    }
}

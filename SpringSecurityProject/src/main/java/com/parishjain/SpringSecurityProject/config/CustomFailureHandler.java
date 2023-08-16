package com.parishjain.SpringSecurityProject.config;

import com.parishjain.SpringSecurityProject.model.MyUser;
import com.parishjain.SpringSecurityProject.service.MyUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

public class CustomFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    MyUserService myUserService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {


        // Parameter name should be same as the name that is specified in the form
        String email = request.getParameter("username");

        // Now we are getting the user by email
        MyUser user = myUserService.findByEmail(email);

        if(user != null){
            boolean isEnable = user.getEnable();
            // Here we are checking that is User account is enable or not
            if(isEnable){
                    // Here we are checking if user account is locked or not
                   // if true
                    if(user.getIsAccountNonLocked()){
                        //Mow we are checking the attempt that user have tried
                        // and compare it with the attempt that we have allowed
                        if(user.getFailedAttempt() < (MyUserService.ATTEMPT_TIME - 1)){
                            // Now we are increasing the failed attempt
                            myUserService.increasesFailedAttempt(user);
                        }
                        else{
                            // In this else case the control will come when the user
                            // has tried his failed attempt and now his account should be locked
                            myUserService.lock(user);
                            exception = new LockedException("Your Account is Locked... !! Failed attempt 3");
                        }
                    }
                    else if( !user.getIsAccountNonLocked()){
                        if(myUserService.unlockAccountTimeExpired(user)){
                            exception = new LockedException("Account is unlocked ! Please try to login");
                        }
                        else {
                            exception = new LockedException("Account is Locked ! Please try after sometime.");
                        }
                    }
            }
            else {
                exception = new LockedException("Account is inactive... Kindly verify the account");
            }
        }
        super.setDefaultFailureUrl("/signIn?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}

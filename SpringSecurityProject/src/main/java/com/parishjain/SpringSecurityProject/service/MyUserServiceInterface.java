package com.parishjain.SpringSecurityProject.service;

import com.parishjain.SpringSecurityProject.model.MyUser;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public interface MyUserServiceInterface {

    public MyUser addUser(MyUser myUser, String url);

    public void removeSessionMessage();

    public BCryptPasswordEncoder bCryptPasswordEncoder();

    public MyUser findByEmail(String email);

    public void sendMail(MyUser user,String url);

    public boolean findByVerificationCode(String code);


    public void increasesFailedAttempt(MyUser user);

    public void resetAttempt(String email);

    public void lock(MyUser user);

    public boolean unlockAccountTimeExpired(MyUser user);


}

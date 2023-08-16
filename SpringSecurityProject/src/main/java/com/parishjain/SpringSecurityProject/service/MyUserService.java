package com.parishjain.SpringSecurityProject.service;

import com.parishjain.SpringSecurityProject.model.MyUser;
import com.parishjain.SpringSecurityProject.repository.MyUserRepo;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.UUID;

@Service
@Transactional
public class MyUserService implements MyUserServiceInterface{

//    private static final long lock_duration_time = 1 * 60 * 60 * 1000;
    private static final long lock_duration_time = 50000;  // sec * 1000
    public static final long ATTEMPT_TIME = 3;

    @Autowired
    MyUserRepo myUserRepo;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public MyUser addUser(MyUser myUser,String url) {
        String encryptPassword = bCryptPasswordEncoder().encode(myUser.getPassword());
        myUser.setPassword(encryptPassword);
        myUser.setRole("ROLE_USER");
        myUser.setEnable(false);
        myUser.setVerificationCode(UUID.randomUUID().toString());
        myUser.setIsAccountNonLocked(true);
        myUser.setFailedAttempt(0);
        myUser.setLockTime(null);
        MyUser newUser = myUserRepo.save(myUser);
        sendMail(newUser,url);
        return newUser;
    }

    @Override
    public void removeSessionMessage(){
        HttpSession httpSession = ((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())).getRequest().getSession();
        httpSession.removeAttribute("msg");
    }

    @Override
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public MyUser findByEmail(String email){

        return myUserRepo.findByEmail(email);
    }

    @Override
    public void sendMail(MyUser user,String url){

        String from = "officialparishjain@gmail.com";
        String to = user.getEmail();
        String subject = "Account Verification";
        String content = "Dear [[name]], <br>" + "Please click the link below to verify your registration: <br>"
                + "<h3> <a href=\"[[url]]\" target=\"_self\">VERIFY</h3>" + "Thank you,<br>" + "Parish Jain";

        try{
            // Create a new MimeMessage object
            MimeMessage message = mailSender.createMimeMessage();
            // Wrap the MimeMessage object in a MimeMessageHelper object
            // It provides various methods to create the mime message
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from,"Parish Jain");
            helper.setTo(to);
            helper.setSubject(subject);
            content = content.replace("[[name]]", user.getName());
            String siteUrl = url + "/verify?code=" + user.getVerificationCode();
            content = content.replace("[[url]]",siteUrl);
//            helper.setText(content);
            helper.setText(content, true);
            mailSender.send(message);

        }
        catch (Exception ex){
            ex.printStackTrace();
        }



    }

    @Override
    @Transactional
    public boolean findByVerificationCode(String code) {
        MyUser user = myUserRepo.findByVerificationCode(code);
        if(user != null){
            user.setVerificationCode(null);
            user.setEnable(true);
            myUserRepo.save(user);
            return true;
        }
        else return false;
    }

    @Override
    public void increasesFailedAttempt(MyUser user) {
       Integer attempt = user.getFailedAttempt() +1 ;
       myUserRepo.updateFailedAttemptByEmail(user.getEmail(), attempt);
    }

    @Override
    public void resetAttempt(String email) {
        myUserRepo.updateFailedAttemptByEmail(email,0);
    }

    @Override
    public void lock(MyUser user) {
        user.setIsAccountNonLocked(false);
        user.setLockTime(new Date());
        myUserRepo.save(user);
    }

    @Override
    public boolean unlockAccountTimeExpired(MyUser user) {
        long lockTimeInMills = user.getLockTime().getTime();
        long currentTimeMills = System.currentTimeMillis();
        if((lockTimeInMills + lock_duration_time) < currentTimeMills){
            user.setLockTime(null);
            user.setFailedAttempt(0);
            user.setIsAccountNonLocked(true);
            myUserRepo.save(user);
            return true;
        }
        return false;
    }
}

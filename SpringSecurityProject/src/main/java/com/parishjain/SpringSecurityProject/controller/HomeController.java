package com.parishjain.SpringSecurityProject.controller;

import com.parishjain.SpringSecurityProject.model.MyUser;
import com.parishjain.SpringSecurityProject.service.MyUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    MyUserService myUserService;

    @GetMapping({"/","/index"})
    private String index(){
        return "index";
    }



    @GetMapping("/signIn")
    private String signIn(){
        return "signIn";
    }

    @GetMapping("/register")
    private String register(Model model){
        model.addAttribute("myUser",new MyUser());
        return "register";
    }

    @PostMapping("/register")
    private String register(@ModelAttribute("myUser") MyUser myUser, HttpSession httpSession, HttpServletRequest request){
       String requestUrl = request.getRequestURL().toString();  // http://localhost:8080/register
        // We want to make http:localhost:8080/verify?code=2464823154545454519144
        String path = request.getServletPath();   // /register
        requestUrl= requestUrl.replace(path,"");  // http://localhost:8080

        try{
            MyUser user =  myUserService.addUser(myUser,requestUrl);
            if(user == null){
                httpSession.setAttribute("msg","Something went wrong..");
            }
            else httpSession.setAttribute("msg","Register Successfully");
        }
        catch(DataIntegrityViolationException ex){
            httpSession.setAttribute("msg","User already exists");
        }
        return "redirect:/register";
    }

    @GetMapping("/verify")
    public String verify(@RequestParam("code") String code,Model m){
        boolean isVerified = myUserService.findByVerificationCode(code);
        if(isVerified){
            m.addAttribute("msg","Successfully your account is verified");
        }
        else{
            m.addAttribute("msg","Verification link is invalid or you are Already verified");
        }
        return "message";
    }

}

package com.parishjain.SpringSecurityProject.controller;

import com.parishjain.SpringSecurityProject.model.MyUser;
import com.parishjain.SpringSecurityProject.service.MyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    MyUserService myUserService;

    @GetMapping("/home")
    public String home(Principal principal){
        // This will give us the currently authenticated username in our case username is email
        String email  = principal.getName();
        MyUser user = myUserService.findByEmail(email);
        return "home";
    }


    @GetMapping("/about")
    private String about(){
        return "about";
    }

    @GetMapping("/profile")
    private String profile(){
        return "profile";
    }

    @ModelAttribute
    private void commonUser(Principal principal, Model model){
        if(principal!=null){
            String email = principal.getName();
            MyUser myUser = myUserService.findByEmail(email);
            model.addAttribute("user",myUser);
        }
    }

}

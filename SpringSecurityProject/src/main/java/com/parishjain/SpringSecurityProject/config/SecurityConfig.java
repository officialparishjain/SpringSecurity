package com.parishjain.SpringSecurityProject.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoder(){
            return new BCryptPasswordEncoder();
    }


    /*
        HTTP Basic is a simple authentication scheme that is built into the HTTP protocol. It works by sending the username and password as clear text in the HTTP header. This means that the credentials are not encrypted, and they can be intercepted by anyone who can see the network traffic.
        Form login is a more secure authentication mechanism that uses a web form to collect the username and password. The credentials are then encrypted and sent to the server. This makes it more difficult for attackers to intercept the credentials.
     */

    @Bean
    public UserDetailsService userDetailsService(){
        return new CustomUserDetailsService();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(userDetailsService());

        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public CustomAuthSuccessHandler customAuthSuccessHandler(){
        return new CustomAuthSuccessHandler();
    }

    @Bean
    public CustomFailureHandler customFailureHandler(){
        return new CustomFailureHandler();
    }


    /*
        **** SecurityFilterChain ***
           This method configures the security filter chain. The SecurityFilterChain is responsible
           for defining how incoming HTTP requests should be handled by Spring Security filters.
    */

    /*
            http is the HttpSecurity object that is being configured.
            securityMatchers() is the method that is used to configure the HttpSecurity object to match specific requests.
            matchers is the Customizer that is used to customize the SecurityRequestMatchers object.
            requestMatchers() is the method that is used to match a specific path.
            "/**" is the path that the HttpSecurity object should match.
     */
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        // The requestMatchers("/**") method tells the HttpSecurity object to match any request that starts with the / character.
//        http.securityMatchers((matchers) -> matchers.requestMatchers("/**"))
//                .authorizeHttpRequests((authorize) -> authorize.anyRequest().hasAnyRole("USER", "ADMIN"))
//                .csrf(AbstractHttpConfigurer::disable)
//                .formLogin(formLogin -> formLogin.loginPage("/signIn")  // Specify the custom login page URL
//                        .loginProcessingUrl("/login")
//                        .defaultSuccessUrl("/home")
//                        .permitAll());
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/user/**").hasAnyRole("USER")
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN")// Authenticate APIs starting with /user/
                        .anyRequest().permitAll()  // Permit all other requests without authentication
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(formLogin -> formLogin
                        .loginPage("/signIn")  // Specify the custom login page URL
                        .loginProcessingUrl("/userLogin")
                        .failureHandler(customFailureHandler())
                        .successHandler(customAuthSuccessHandler())
                        .permitAll()
                );

        return http.build();
    }

}

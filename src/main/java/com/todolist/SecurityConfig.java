package com.todolist;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.servlet.http.Cookie;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {

        http
        	.cors().and().csrf().disable();
//            .antMatcher("/**").authorizeRequests()
//            .antMatchers(new String[]{"/api/", "/api/not-restricted"}).permitAll()
//            .anyRequest().authenticated()
//            .and()
//            .logout(l -> l
//            	.clearAuthentication(true)
//            	.logoutSuccessUrl("https://accounts.google.com/Logout")
//            	.permitAll()
//            )
//            .oauth2Login();
    }
}
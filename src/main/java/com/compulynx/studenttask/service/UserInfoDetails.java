package com.compulynx.studenttask.service;

import com.compulynx.studenttask.model.db.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class UserInfoDetails implements UserDetails {
    private  final String userName;
    private final String password;
    private final Long id;
    private  final  List<GrantedAuthority> authorities;


    public UserInfoDetails(UserInfo userInfo){
        userName=userInfo.getUserName();
        password=userInfo.getPassword();
        authorities = Arrays.stream(userInfo.getRoles().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        id=userInfo.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }


    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    public  static Optional<String> getLoggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!authentication.isAuthenticated()){
            return Optional.empty();
        }
        var userName= authentication.getName();
        return  Optional.of(userName);

    }
}

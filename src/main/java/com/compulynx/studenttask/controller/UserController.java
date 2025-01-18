package com.compulynx.studenttask.controller;

import com.compulynx.studenttask.Dao.LoginResponseDao;
import com.compulynx.studenttask.model.AuthRequest;
import com.compulynx.studenttask.service.JwtService;
import com.compulynx.studenttask.service.UserInfoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(description = "Student management system",name = "User")
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserInfoService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/login")
    public LoginResponseDao authenticateAndGetToken(@RequestBody AuthRequest authRequest) throws InterruptedException {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.userName(), authRequest.password()));

        if (authentication.isAuthenticated()) {
            var userInfo = userService.findUserByUserName(authRequest.userName());

            return   LoginResponseDao
                    .builder()
                    .jwt(jwtService.generateToken(userInfo,authRequest.userName()))
                    .userName(userInfo.getUserName())
                    .user(userInfo)
                    .build();
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }
}

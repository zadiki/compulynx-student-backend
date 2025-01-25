package com.compulynx.studenttask.util;

import com.compulynx.studenttask.model.db.UserInfo;
import com.compulynx.studenttask.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserUtil {
    @Autowired
    private UserInfoRepository userInfoRepository;

    public Optional<UserInfo> getLoggedInUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return Optional.empty();
        }
        var userName = authentication.getName();
        return userInfoRepository.findByUserName(userName);


    }
}

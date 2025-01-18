package com.compulynx.studenttask.service;

import com.compulynx.studenttask.exception.DuplicateEntryException;
import com.compulynx.studenttask.model.db.UserInfo;
import com.compulynx.studenttask.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserInfoService implements UserDetailsService {
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private PasswordEncoder encoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> userDetails = userInfoRepository.findByUserName(username);

        return userDetails.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("user not found" + username));
    }

    public UserInfo findUserByUserName(String userName) throws UsernameNotFoundException {
        Optional<UserInfo> userDetails = userInfoRepository.findByUserName(userName);
        return userDetails
                .orElseThrow(() -> new UsernameNotFoundException("user not found" + userName));
    }

    public UserInfo createUser(UserInfo userInfo) {
        var existingUser = userInfoRepository.findByUserName(userInfo.getUserName());
        if (existingUser.isPresent())
            throw new DuplicateEntryException("User with that username already exist");
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        userInfoRepository.save(userInfo);
        return userInfo;
    }

    public List<UserInfo> findAllUsers() {
        return userInfoRepository.findAll();
    }
}

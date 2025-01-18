package com.compulynx.studenttask.Dao;


import com.compulynx.studenttask.model.db.UserInfo;
import lombok.Builder;

@Builder
public record LoginResponseDao(String jwt, String userName, UserInfo user) {
}


package com.compulynx.studenttask;

import com.compulynx.studenttask.model.db.UserInfo;


import com.compulynx.studenttask.service.UserInfoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;


@SpringBootApplication
public class StudentTaskApplication {
	final Logger log = LoggerFactory.getLogger(StudentTaskApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(StudentTaskApplication.class, args);
	}
	@Bean
	public CommandLineRunner loadData(UserInfoService service) {
		return (args) -> {
			// save a couple of system users
			var user1 = new UserInfo();
			user1.setUserName("admin");
			user1.setPassword("admin");
			var user2 = new UserInfo();
			user2.setUserName("admin1");
			user2.setPassword("admin1");
			service.createUser(user1);
			service.createUser(user2);

			// fetch all customers
			log.info("users found with findAll():");
			log.info("-------------------------------");
			for (UserInfo userInfo : service.findAllUsers()) {
				log.info(userInfo.toString());
			}


		};
	}
}

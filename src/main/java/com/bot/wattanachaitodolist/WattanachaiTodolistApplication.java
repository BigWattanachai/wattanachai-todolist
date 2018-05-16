package com.bot.wattanachaitodolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class WattanachaiTodolistApplication {

	public static void main(String[] args) {
		SpringApplication.run(WattanachaiTodolistApplication.class, args);
	}
}

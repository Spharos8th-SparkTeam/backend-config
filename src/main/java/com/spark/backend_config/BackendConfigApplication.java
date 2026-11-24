package com.spark.backend_config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class BackendConfigApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendConfigApplication.class, args);
	}

}

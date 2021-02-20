package com.uwaai.datareststastemachine;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan
@EnableJpaRepositories
public class DataRestStastemachineApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataRestStastemachineApplication.class, args);
	}
}

package com.tablenow.tablenow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class TablenowApplication
{
	public static void main(String[] args) {
		SpringApplication.run(TablenowApplication.class, args);
	}
}

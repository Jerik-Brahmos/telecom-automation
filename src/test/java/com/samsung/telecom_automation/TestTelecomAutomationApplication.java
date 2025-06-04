package com.samsung.telecom_automation;

import org.springframework.boot.SpringApplication;

public class TestTelecomAutomationApplication {

	public static void main(String[] args) {
		SpringApplication.from(TelecomAutomationApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

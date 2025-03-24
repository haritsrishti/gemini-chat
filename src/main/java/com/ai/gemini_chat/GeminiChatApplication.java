package com.ai.gemini_chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.ai.gemini_chat")
public class GeminiChatApplication {

	public static void main(String[] args) {

		SpringApplication.run(GeminiChatApplication.class, args);
	}

}

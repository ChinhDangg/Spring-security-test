package com.auth;

import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthenticationApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationApplication.class, args);
	}

//	public static void encrypt() {
//		String secretKey = "e9bafb2bcaa7a7dec93447313079432534ef8088978cd10fae044542f1e477f40";
//		String password = "9wl%BZ~7uR1w";
//
//		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
//		textEncryptor.setPassword(password);
//
//		String encryptedSecretKey = textEncryptor.encrypt(secretKey);
//		System.out.println("Encrypted Secret Key: " + encryptedSecretKey);
//	}


}

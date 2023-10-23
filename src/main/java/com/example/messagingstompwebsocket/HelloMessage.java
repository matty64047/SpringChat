package com.example.messagingstompwebsocket;

public class HelloMessage {

	private String name, message;

	public HelloMessage() {
	}

	public HelloMessage(String name, String message) {
		this.name = name;
		this.message = message;
	}

	public String getName() {
		return name;
	}

	public String getMessage() {
		return message;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}

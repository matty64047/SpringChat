package com.example.messagingstompwebsocket;

public class HelloMessage {

	private String name, message, stream;

	public HelloMessage() {
	}

	public HelloMessage(String name, String message, String stream) {
		this.name = name;
		this.message = message;
		this.stream = stream;
	}

	public String getName() {
		return name;
	}

	public String getMessage() {
		return message;
	}

	public String getStream() {
		return stream;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}
}

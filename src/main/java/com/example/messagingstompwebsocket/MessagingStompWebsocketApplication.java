package com.example.messagingstompwebsocket;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;



@SpringBootApplication
public class MessagingStompWebsocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessagingStompWebsocketApplication.class, args);
	}
}

@Controller
class UserController {

    @GetMapping("/")
    public String loginPage() {
        return "login.html"; // This will return the "login.html" page from resources/static
    }

	@GetMapping("/messages")
    public String messagesPage() {
        return "messages.html"; // This will return the "login.html" page from resources/static
    }
}

@RestController
class LoginController {
	private Map<String, String> users = new HashMap<>();

	@PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        if (!users.containsKey(user.getEmail())) {
            users.put(user.getEmail(), user.getPassword());
            return "Registration successful";
        }
        return "Email already exists";
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody User user) {
        String storedPassword = users.get(user.getEmail());
        if (storedPassword != null && storedPassword.equals(user.getPassword())) {
            return "Login successful";
        }
        return "Login failed";
    }
}

class User {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
package com.example.messagingstompwebsocket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

	public static void addToDatabase(String name, String message) {
		// Database connection parameters
        String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
        String user = "sa";
        String password = "sa";

        // Sample Java object
        HelloMessage myObject = new HelloMessage(name, message);
		
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS messages (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), message TEXT)";
            try (PreparedStatement createTableStatement = connection.prepareStatement(createTableSQL)) {
                createTableStatement.executeUpdate();
            }
            // SQL insert statement
            String insertSQL = "INSERT INTO messages (name, message) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                // Set object values to SQL parameters
                preparedStatement.setString(1, myObject.getName());
                preparedStatement.setString(2, myObject.getMessage());

                // Execute the insert statement
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
    	
    public static List<HelloMessage> retrieveFromDatabase() {
		// Database connection parameters
        String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
        String user = "sa";
        String password = "sa";

        List<HelloMessage> messages = new ArrayList<>();
		
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS messages (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), message TEXT)";
            try (PreparedStatement createTableStatement = connection.prepareStatement(createTableSQL)) {
                createTableStatement.executeUpdate();
            }
            // SQL select statement to retrieve all records
            String selectSQL = "SELECT name, message FROM messages";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String message = resultSet.getString("message");
                    messages.add(new HelloMessage(name, message));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
	}

    @MessageMapping("/history")
    @SendTo("/topic/greetings")
	public Greeting history() throws Exception {
        List<HelloMessage> messages = retrieveFromDatabase();
        String returnString = "";
        if (messages.size() > 0) {
            for (HelloMessage message : messages) {
                returnString += HtmlUtils.htmlEscape(message.getName())+": " + HtmlUtils.htmlEscape(message.getMessage()) + "@@";
            }
        }  
        return new Greeting(returnString);
	}

	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public Greeting greeting(HelloMessage message) throws Exception {
		addToDatabase(message.getName(), message.getMessage());
        List<HelloMessage> messages = retrieveFromDatabase();
        String returnString = "";
        if (messages.size() > 0) {
            for (HelloMessage _message : messages) {
                returnString += HtmlUtils.htmlEscape(_message.getName())+": " + HtmlUtils.htmlEscape(_message.getMessage()) + "@@";
            }
        }  
        return new Greeting(returnString);
	}

}

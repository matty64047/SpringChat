package com.example.messagingstompwebsocket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

	public static void addToDatabase(String name, String message, String stream) {
		// Database connection parameters
        String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
        String user = "sa";
        String password = "sa";

        // Sample Java object
        HelloMessage myObject = new HelloMessage(name, message, stream);
		
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS messages (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), message TEXT, stream TEXT)";
            try (PreparedStatement createTableStatement = connection.prepareStatement(createTableSQL)) {
                createTableStatement.executeUpdate();
            }
            // SQL insert statement
            String insertSQL = "INSERT INTO messages (name, message, stream) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                // Set object values to SQL parameters
                preparedStatement.setString(1, myObject.getName());
                preparedStatement.setString(2, myObject.getMessage());
                preparedStatement.setString(3, myObject.getStream());
                // Execute the insert statement
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
    	
    public static List<HelloMessage> retrieveFromDatabase(String targetStream) {
		// Database connection parameters
        String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
        String user = "sa";
        String password = "sa";

        List<HelloMessage> messages = new ArrayList<>();
		
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS messages (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), message TEXT, stream TEXT)";
            try (PreparedStatement createTableStatement = connection.prepareStatement(createTableSQL)) {
                createTableStatement.executeUpdate();
            }
            // SQL select statement to retrieve all records
            String selectSQL = "SELECT name, message, stream FROM messages WHERE stream = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
                preparedStatement.setString(1, targetStream);
                ResultSet resultSet = preparedStatement.executeQuery();
                
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String message = resultSet.getString("message");
                    String stream = resultSet.getString("stream");
                    messages.add(new HelloMessage(name, message, stream));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
	}

    @MessageMapping("/history/{stream}")
    @SendTo("/topic/streams/{stream}")
	public Greeting history(@DestinationVariable String stream) throws Exception {
        List<HelloMessage> messages = retrieveFromDatabase(stream);
        String returnString = "";
        if (messages.size() > 0) {
            for (HelloMessage message : messages) {
                returnString += HtmlUtils.htmlEscape(message.getName())+": " + HtmlUtils.htmlEscape(message.getMessage()) + "@@";
            }
        }  
        return new Greeting(returnString);
	}

    @MessageMapping("/streams")
    @SendTo("/topic/streams")
	public List<String> streamList() throws Exception {
        List<String> streamList = new ArrayList<>();
        String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
        String user = "sa";
        String password = "sa";
         try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS messages (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), message TEXT, stream TEXT)";
            try (PreparedStatement createTableStatement = connection.prepareStatement(createTableSQL)) {
                createTableStatement.executeUpdate();
            }
            // SQL select statement to retrieve all records
            String selectSQL = "SELECT DISTINCT stream FROM messages";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                
                while (resultSet.next()) {
                    String stream = resultSet.getString("stream");
                    if (stream != null) {
                        streamList.add(stream);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return streamList;
	}

    


	@MessageMapping("/streams/{stream}")
	@SendTo("/topic/streams/{stream}")
	public Greeting greeting(HelloMessage message, @DestinationVariable String stream) throws Exception {
        addToDatabase(message.getName(), message.getMessage(), message.getStream());
        List<HelloMessage> messages = retrieveFromDatabase(stream);
        String returnString = "";
        if (messages.size() > 0) {
            for (HelloMessage _message : messages) {
                returnString += HtmlUtils.htmlEscape(_message.getName())+": " + HtmlUtils.htmlEscape(_message.getMessage()) + "@@";
            }
        }  
        return new Greeting(returnString);
	}

}
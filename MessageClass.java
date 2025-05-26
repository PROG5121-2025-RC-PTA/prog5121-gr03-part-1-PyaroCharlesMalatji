/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pyaropart2;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 
 * @author User
 */
public class MessageClass {
    
    private static final String MESSAGES_FILE = "messages.txt";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static class Message {
        private String sender;
        private String recipient;
        private String content;
        private String timestamp;
        
        public Message(String sender, String recipient, String content, String timestamp) {
            this.sender = sender;
            this.recipient = recipient;
            this.content = content;
            this.timestamp = timestamp;
        }
        
        public String getSender() { return sender; }
        public String getRecipient() { return recipient; }
        public String getContent() { return content; }
        public String getTimestamp() { return timestamp; }
        
        @Override
        public String toString() {
            return String.format("[%s] From: %s\nMessage: %s\n", timestamp, sender, content);
        }

        
    }
    
    public static void sendMessage(String sender, String recipient, String messageContent) {
        if (sender == null || recipient == null || messageContent == null || 
            sender.trim().isEmpty() || recipient.trim().isEmpty() || messageContent.trim().isEmpty()) {
            System.err.println("Invalid message parameters");
            return;
        }
        
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        String messageEntry = String.format("SENDER:%s|RECIPIENT:%s|TIMESTAMP:%s|CONTENT:%s%n",sender, recipient, timestamp, messageContent);
        
        try (FileWriter writer = new FileWriter(MESSAGES_FILE, true)) {
            writer.write(messageEntry);
            System.out.println("Message sent from " + sender + " to " + recipient);
        } catch (IOException e) {
            System.err.println("Error saving message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static String getMessagesForUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "Invalid username";
        }
        
        List<Message> userMessages = loadMessagesForUser(username);
        
        if (userMessages.isEmpty()) {
            return "No messages found for " + username;
        }
        
        StringBuilder messageDisplay = new StringBuilder();
        messageDisplay.append("Messages for ").append(username).append(":\n");
        messageDisplay.append("=" .repeat(40)).append("\n\n");
        
        // Divide sent and received messages
        List<Message> sentMessages = new ArrayList<>();
        List<Message> receivedMessages = new ArrayList<>();
        
        for (Message msg : userMessages) {
            if (msg.getSender().equals(username)) {
                sentMessages.add(msg);
            } else {
                receivedMessages.add(msg);
            }
        }
        
        // Shows received messages first
        if (!receivedMessages.isEmpty()) {
            messageDisplay.append("RECEIVED MESSAGES:\n");
            messageDisplay.append("-" .repeat(20)).append("\n");
            for (Message msg : receivedMessages) {
                messageDisplay.append(msg.toString()).append("\n");
            }
        }
        
        // Display sent messages
        if (!sentMessages.isEmpty()) {
            messageDisplay.append("SENT MESSAGES:\n");
            messageDisplay.append("-" .repeat(20)).append("\n");
            for (Message msg : sentMessages) {
                messageDisplay.append(String.format("[%s] To: %s\nMessage: %s\n\n", 
                 msg.getTimestamp(), msg.getRecipient(), msg.getContent()));
            }
        }
        
        return messageDisplay.toString();
    }
    
    private static List<Message> loadMessagesForUser(String username) {
        List<Message> userMessages = new ArrayList<>();
        File file = new File(MESSAGES_FILE);
        
        if (!file.exists()) {
            return userMessages;
        }
        //Learned from Chatgpt in 2025
        try (BufferedReader reader = new BufferedReader(new FileReader(MESSAGES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Message message = parseMessageLine(line);
                if (message != null && 
                    (message.getSender().equals(username) || message.getRecipient().equals(username))) {
                    userMessages.add(message);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading messages: " + e.getMessage());
            e.printStackTrace();
        }
        
        return userMessages;
    }
    
    /**
     * Parse a message line from the file
     */
    private static Message parseMessageLine(String line) {
        try {
            
            String[] parts = line.split("\\|");
            
            if (parts.length != 4) {
                System.err.println("Invalid message format: " + line);
                return null;
            }
            
            String sender = extractValue(parts[0], "SENDER:");
            String recipient = extractValue(parts[1], "RECIPIENT:");
            String timestamp = extractValue(parts[2], "TIMESTAMP:");
            String content = extractValue(parts[3], "CONTENT:");
            
            if (sender != null && recipient != null && timestamp != null && content != null) {
                return new Message(sender, recipient, content, timestamp);
            }
            
        } catch (Exception e) {
            System.err.println("Error parsing message line: " + e.getMessage());
        }
        
        return null;
    }
    
    //Extract value from a key
    private static String extractValue(String keyValuePair, String key) {
        if (keyValuePair.startsWith(key)) {
            return keyValuePair.substring(key.length());
        }
        return null;
    }
    
    /**
     * Get count of messages for a user
     */
    public static int getMessageCount(String username) {
        return loadMessagesForUser(username).size();
    }
    
   
    public static void clearAllMessages() {
        try {
            File file = new File(MESSAGES_FILE);
            if (file.exists()) {
                file.delete();
                System.out.println("All messages cleared");
            }
        } catch (Exception e) {
            System.err.println("Error clearing messages: " + e.getMessage());
        }
    }

   

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pyaropart2;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author User
 */
public class MessageManager {
    private static List<Message> allMessages = new ArrayList<>();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    

    public static class Message {
        private String sender;
        private String receiver;
        private String content;
        private String timestamp;
        private boolean isRead;
        
        public Message(String sender, String receiver, String content) {
            this.sender = sender;
            this.receiver = receiver;
            this.content = content;
            this.timestamp = LocalDateTime.now().format(DATE_FORMAT);
            this.isRead = false;
        }
        
        //  For loading from file
        public Message(String sender, String receiver, String content, String timestamp, boolean isRead) {
            this.sender = sender;
            this.receiver = receiver;
            this.content = content;
            this.timestamp = timestamp;
            this.isRead = isRead;
        }
        
        public String getSender() { return sender; }
        public String getReceiver() { return receiver; }
        public String getContent() { return content; }
        public String getTimestamp() { return timestamp; }
        public boolean isRead() { return isRead; }
        
        public void setRead(boolean read) { this.isRead = read; }
        
        @Override
        public String toString() {
            String status = isRead ? "" : "[NEW] ";
            return String.format("%s[%s] From: %s\n%s", status, timestamp, sender, content);
        }
    }
    
  
    public static void sendMessage(String sender, String receiver, String content) {
        Message message = new Message(sender, receiver, content);
        allMessages.add(message);
        System.out.println("Message sent from " + sender + " to " + receiver);
    }
    
     //Get messages for a specific user (received messages)
   
    public static List<Message> getMessagesForUser(String username) {
        return allMessages.stream()
                .filter(msg -> msg.getReceiver().equals(username))
                .collect(Collectors.toList());
    }
    
    //Gemini used in 2025
    public static List<Message> getSentMessages(String username) {
        return allMessages.stream()
                .filter(msg -> msg.getSender().equals(username))
                .collect(Collectors.toList());
    }
    
    
    public static int getUnreadCount(String username) {
        return (int) allMessages.stream()
                .filter(msg -> msg.getReceiver().equals(username) && !msg.isRead())
                .count();
    }
    
    public static void markMessagesAsRead(String username) {
        allMessages.stream()
                .filter(msg -> msg.getReceiver().equals(username))
                .forEach(msg -> msg.setRead(true));
    }
    
    
    public static List<Message> getAllMessages() {
        return new ArrayList<>(allMessages);
    }
    
    
    public static void setMessages(List<Message> messages) {
        allMessages = new ArrayList<>(messages);
    }
    
    
    public static void clearAllMessages() {
        allMessages.clear();
        System.out.println("All messages cleared");
    }
}
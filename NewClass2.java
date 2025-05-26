/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pyaropart2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NewClass2 {
    
    private static final String DATA_FILE = "users.json";
    private static final String MESSAGES_FILE = "messages.json";
    
    //Save users to JSON file
    public static void saveUsersToFile(Map<String, PyaroPart2.User> users) {
        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            writer.write("{\n");
            writer.write("  \"users\": [\n");
            
            int count = 0;
            for (Map.Entry<String, PyaroPart2.User> entry : users.entrySet()) {
                PyaroPart2.User user = entry.getValue();
                
                writer.write("    {\n");
                writer.write("      \"username\": \"" + escapeJson(user.getUsername()) + "\",\n");
                writer.write("      \"password\": \"" + escapeJson(user.getPassword()) + "\",\n");
                writer.write("      \"phone\": \"" + escapeJson(user.getPhone()) + "\"\n");
                writer.write("    }");
                
                count++;
                if (count < users.size()) {
                    writer.write(",");
                }
                writer.write("\n");
            }
            
            writer.write("  ]\n");
            writer.write("}\n");
            
            System.out.println("Users saved to " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load users from JSON file
     */
    public static Map<String, PyaroPart2.User> loadUsersFromFile() {
        Map<String, PyaroPart2.User> users = new HashMap<>();
        File file = new File(DATA_FILE);
        
        if (!file.exists()) {
            System.out.println("No existing user data found. Starting fresh.");
            return users;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line).append("\n");
            }
            
            String content = jsonContent.toString();
            users = parseUsersFromJson(content);
            
            System.out.println("Loaded " + users.size() + " users from " + DATA_FILE);
            
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Save messages to JSON file
     */
    public static void saveMessagesToFile(List<MessageManager.Message> messages) {
        try (FileWriter writer = new FileWriter(MESSAGES_FILE)) {
            writer.write("{\n");
            writer.write("  \"messages\": [\n");
            
            for (int i = 0; i < messages.size(); i++) {
                MessageManager.Message msg = messages.get(i);
                
                writer.write("    {\n");
                writer.write("      \"sender\": \"" + escapeJson(msg.getSender()) + "\",\n");
                writer.write("      \"receiver\": \"" + escapeJson(msg.getReceiver()) + "\",\n");
                writer.write("      \"content\": \"" + escapeJson(msg.getContent()) + "\",\n");
                writer.write("      \"timestamp\": \"" + escapeJson(msg.getTimestamp()) + "\",\n");
                writer.write("      \"isRead\": " + msg.isRead() + "\n");
                writer.write("    }");
                
                if (i < messages.size() - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }
            
            writer.write("  ]\n");
            writer.write("}\n");
            
            System.out.println("Messages saved to " + MESSAGES_FILE);
        } catch (IOException e) {
            System.err.println("Error saving messages: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static List<MessageManager.Message> loadMessagesFromFile() {
        List<MessageManager.Message> messages = new ArrayList<>();
        File file = new File(MESSAGES_FILE);
        
        if (!file.exists()) {
            System.out.println("No existing message data found. Starting fresh.");
            return messages;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(MESSAGES_FILE))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line).append("\n");
            }
            
            String content = jsonContent.toString();
            messages = parseMessagesFromJson(content);
            
            System.out.println("Loaded " + messages.size() + " messages from " + MESSAGES_FILE);
            
        } catch (IOException e) {
            System.err.println("Error loading messages: " + e.getMessage());
            e.printStackTrace();
        }
        
        return messages;
    }
    
    
    private static List<MessageManager.Message> parseMessagesFromJson(String jsonContent) {
        List<MessageManager.Message> messages = new ArrayList<>();
        
        try {
            String cleanJson = jsonContent.replaceAll("\\s+", " ").trim();
            
            int messagesStart = cleanJson.indexOf("\"messages\":");
            if (messagesStart == -1) {
                System.out.println("No messages array found in JSON");
                return messages;
            }
            
            int arrayStart = cleanJson.indexOf("[", messagesStart);
            int arrayEnd = findMatchingBracket(cleanJson, arrayStart);
            
            if (arrayStart == -1 || arrayEnd == -1) {
                System.out.println("Invalid JSON structure");
                return messages;
            }
            
            String messagesArray = cleanJson.substring(arrayStart + 1, arrayEnd);
            
            int pos = 0;
            while (pos < messagesArray.length()) {
                while (pos < messagesArray.length() && (messagesArray.charAt(pos) == ' ' || messagesArray.charAt(pos) == ',')) {
                    pos++;
                }
                
                if (pos >= messagesArray.length()) break;
                
                if (messagesArray.charAt(pos) == '{') {
                    int objEnd = findMatchingBrace(messagesArray, pos);
                    if (objEnd != -1) {
                        String msgObj = messagesArray.substring(pos + 1, objEnd);
                        
                        String sender = extractJsonValue(msgObj, "sender");
                        String receiver = extractJsonValue(msgObj, "receiver");
                        String content = extractJsonValue(msgObj, "content");
                        String timestamp = extractJsonValue(msgObj, "timestamp");
                        String isReadStr = extractJsonValue(msgObj, "isRead");
                        boolean isRead = "true".equals(isReadStr);
                        
                        if (sender != null && receiver != null && content != null && timestamp != null) {
                            messages.add(new MessageManager.Message(sender, receiver, content, timestamp, isRead));
                        }
                        
                        pos = objEnd + 1;
                    } else {
                        break;
                    }
                } else {
                    pos++;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error parsing messages JSON: " + e.getMessage());
            e.printStackTrace();
        }
        
        return messages;
    }
    
   //Chatgpt use in 2025
    private static Map<String, PyaroPart2.User> parseUsersFromJson(String jsonContent) {
        Map<String, PyaroPart2.User> users = new HashMap<>();
        
        try {
            String cleanJson = jsonContent.replaceAll("\\s+", " ").trim();
            
            int usersStart = cleanJson.indexOf("\"users\":");
            if (usersStart == -1) {
                return users;
            }
            
            int arrayStart = cleanJson.indexOf("[", usersStart);
            int arrayEnd = findMatchingBracket(cleanJson, arrayStart);
            
            if (arrayStart == -1 || arrayEnd == -1) {
                return users;
            }
            
            String usersArray = cleanJson.substring(arrayStart + 1, arrayEnd);
            
            int pos = 0;
            while (pos < usersArray.length()) {
                while (pos < usersArray.length() && (usersArray.charAt(pos) == ' ' || usersArray.charAt(pos) == ',')) {
                    pos++;
                }
                
                if (pos >= usersArray.length()) break;
                
                if (usersArray.charAt(pos) == '{') {
                    int objEnd = findMatchingBrace(usersArray, pos);
                    if (objEnd != -1) {
                        String userObj = usersArray.substring(pos + 1, objEnd);
                        
                        String username = extractJsonValue(userObj, "username");
                        String password = extractJsonValue(userObj, "password");
                        String phone = extractJsonValue(userObj, "phone");
                        
                        if (username != null && password != null && phone != null) {
                            users.put(username, new PyaroPart2.User(username, password, phone));
                        }
                        
                        pos = objEnd + 1;
                    } else {
                        break;
                    }
                } else {
                    pos++;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
        
        return users;
    }
    
    private static int findMatchingBracket(String str, int openIndex) {
        int count = 1;
        for (int i = openIndex + 1; i < str.length(); i++) {
            if (str.charAt(i) == '[') count++;
            else if (str.charAt(i) == ']') count--;
            if (count == 0) return i;
        }
        return -1;
    }
    
    private static int findMatchingBrace(String str, int openIndex) {
        int count = 1;
        for (int i = openIndex + 1; i < str.length(); i++) {
            if (str.charAt(i) == '{') count++;
            else if (str.charAt(i) == '}') count--;
            if (count == 0) return i;
        }
        return -1;
    }
    
    private static String extractJsonValue(String jsonObject, String fieldName) {
        String pattern = "\"" + fieldName + "\"";
        int fieldStart = jsonObject.indexOf(pattern);
        
        if (fieldStart == -1) return null;
        
        int colonIndex = jsonObject.indexOf(":", fieldStart);
        if (colonIndex == -1) return null;
        
        int valueStart = colonIndex + 1;
        while (valueStart < jsonObject.length() && jsonObject.charAt(valueStart) == ' ') {
            valueStart++;
        }
        
        if (valueStart >= jsonObject.length()) return null;
        
        
        if (jsonObject.charAt(valueStart) != '"') {
            int valueEnd = valueStart;
            while (valueEnd < jsonObject.length() && 
                   jsonObject.charAt(valueEnd) != ',' && 
                   jsonObject.charAt(valueEnd) != '}' && 
                   jsonObject.charAt(valueEnd) != ' ') {
                valueEnd++;
            }
            return jsonObject.substring(valueStart, valueEnd);
        }
        
        int openQuote = jsonObject.indexOf("\"", valueStart);
        if (openQuote == -1) return null;
        
        int closeQuote = openQuote + 1;
        while (closeQuote < jsonObject.length()) {
            if (jsonObject.charAt(closeQuote) == '"' && jsonObject.charAt(closeQuote - 1) != '\\') {
                break;
            }
            closeQuote++;
        }
        
        if (closeQuote >= jsonObject.length()) return null;
        
        return unescapeJson(jsonObject.substring(openQuote + 1, closeQuote));
    }
    
    private static String escapeJson(String str) {
        if (str == null) return "";
        
        return str.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
    }
    
    private static String unescapeJson(String str) {
        if (str == null) return "";
        
        return str.replace("\\\"", "\"")
                 .replace("\\\\", "\\")
                 .replace("\\n", "\n")
                 .replace("\\r", "\r")
                 .replace("\\t", "\t");
    }
    
    public static void autoSave(Map<String, PyaroPart2.User> users) {
        saveUsersToFile(users);
    }
    
    public static void autoSaveMessages(List<MessageManager.Message> messages) {
        saveMessagesToFile(messages);
    }
    
    public static boolean isDataFileAvailable() {
        File file = new File(DATA_FILE);
        return file.exists() && file.canRead();
    }
    
    public static String getDataFilePath() {
        return new File(DATA_FILE).getAbsolutePath();
    }
}
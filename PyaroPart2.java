/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.pyaropart2;
import com.mycompany.pyaropart2.MessageClass.Message;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author User
 */
//Everything in this method is a part of part1 newly intergrated by chatgbt to better fit for part2
public class PyaroPart2 {

    private static Map<String, User> registeredUsers = new HashMap<>();

    public static void main(String[] args) {
        // Load existing users and messages from JSON files at startup
        registeredUsers = NewClass2.loadUsersFromFile();
        MessageManager.setMessages(NewClass2.loadMessagesFromFile());
        
        while (true) {
            String[] options = {"Sign Up", "Log In", "Exit"};
            int choice = JOptionPane.showOptionDialog(null, "Choose an option:", "Still Learning",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            if (choice == 0) {
                showSignUpDialog();
            } else if (choice == 1) {
                showLoginDialog();
            } else {
                // Save data before exit
                NewClass2.saveUsersToFile(registeredUsers);
                NewClass2.autoSaveMessages(MessageManager.getAllMessages());
                JOptionPane.showMessageDialog(null, "Data saved. Goodbye!");
                break;
            }
        }
    }

    private static void showSignUpDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField phoneField = new JTextField();

        Object[] message = {
            "Username (max 5 characters, include '_'):", usernameField,
            "Password (8 characters, uppercase, number, special):", passwordField,
            "Cellphone (country code, max 10 characters):", phoneField
        };

        int result = JOptionPane.showConfirmDialog(null, message, "Sign Up", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String phone = phoneField.getText();

            if (!isValidUsername(username)) {
                JOptionPane.showMessageDialog(null, "Username must be 5 characters or fewer and contain an underscore.");
            } else if (!isValidPassword(password)) {
                JOptionPane.showMessageDialog(null, "Password must be 8 characters and include uppercase letters, numbers, and special characters.");
            } else if (!isValidPhone(phone)) {
                JOptionPane.showMessageDialog(null, "Phone number must be 10 characters or fewer and may include a leading '+'.");
            } else if (registeredUsers.containsKey(username)) {
                JOptionPane.showMessageDialog(null, "Username already exists. Please choose a different username.");
            } else {
                registeredUsers.put(username, new User(username, password, phone));
                NewClass2.autoSave(registeredUsers);
                JOptionPane.showMessageDialog(null, "Sign Up successful! Data saved.");
            }
        }
    }

    private static void showLoginDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
            "Username:", usernameField,
            "Password:", passwordField
        };

        int result = JOptionPane.showConfirmDialog(null, message, "Log In", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (registeredUsers.containsKey(username)) {
                User user = registeredUsers.get(username);
                if (user.getPassword().equals(password)) {
                    showUserMenu(user);
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect password. Please try again.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Username not found. Please sign up first.");
            }
        }
    }
    
    private static void showUserMenu(User user) {
        while (true) {
            int unreadCount = MessageManager.getUnreadCount(user.getUsername());
            String messagesOption = "Messages" + (unreadCount > 0 ? " (" + unreadCount + " new)" : "");
            
            String[] options = {"View Profile", "Change Password", messagesOption, "Logout"};
            int choice = JOptionPane.showOptionDialog(null, 
                "Welcome, " + user.getUsername() + "! What would you like to do?", 
                "User Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, 
                null, options, options[0]);

            if (choice == 0) {
                showUserProfile(user);
            } else if (choice == 1) {
                changePassword(user);
            } else if (choice == 2) {
                showMessagingMenu(user);
            } else {
                // Logout - return to main menu
                break;
            }
        }
    }
    
    private static void showMessagingMenu(User user) {
        while (true) {
            int unreadCount = MessageManager.getUnreadCount(user.getUsername());
            String inboxOption = "Inbox" + (unreadCount > 0 ? " (" + unreadCount + " new)" : "");
            
            String[] options = {inboxOption, "Send Message", "Sent Messages", "Back"};
            int choice = JOptionPane.showOptionDialog(null, 
                "Messaging - " + user.getUsername(), 
                "Messages",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, 
                null, options, options[0]);

            if (choice == 0) {
                showInbox(user);
            } else if (choice == 1) {
                showSendMessage(user);
            } else if (choice == 2) {
                showSentMessages(user);
            } else {
                // Back to user menu
                break;
            }
        }
    }
    
    private static void showInbox(User user) {
        List<MessageManager.Message> messages = MessageManager.getMessagesForUser(user.getUsername());
        
        if (messages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages in your inbox.", "Inbox", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder inbox = new StringBuilder("=== INBOX ===\n\n");
        for (int i = 0; i < messages.size(); i++) {
            MessageManager.Message msg = messages.get(i);
            inbox.append((i + 1)).append(". ").append(msg.toString()).append("\n\n");
        }
        
        // Mark messages as read
        MessageManager.markMessagesAsRead(user.getUsername());
        NewClass2.autoSaveMessages(MessageManager.getAllMessages());
        
        JTextArea textArea = new JTextArea(inbox.toString());
        textArea.setEditable(false);
        textArea.setRows(15);
        textArea.setColumns(50);
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        JOptionPane.showMessageDialog(null, scrollPane, "Inbox", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private static void showSendMessage(User user) {
        // Get list of other users
        String[] usernames = registeredUsers.keySet().stream()
            .filter(username -> !username.equals(user.getUsername()))
            .toArray(String[]::new);
        
        if (usernames.length == 0) {
            JOptionPane.showMessageDialog(null, "No other users to send messages to.", "Send Message", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String recipient = (String) JOptionPane.showInputDialog(null,
            "Select recipient:",
            "Send Message",
            JOptionPane.QUESTION_MESSAGE,
            null,
            usernames,
            usernames[0]);
        
        if (recipient != null) {
            JTextArea messageArea = new JTextArea(5, 30);
            messageArea.setLineWrap(true);
            messageArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(messageArea);
            
            Object[] message = {
                "To: " + recipient,
                "Message:",
                scrollPane
            };
            
            int result = JOptionPane.showConfirmDialog(null, message, "Send Message", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String messageContent = messageArea.getText().trim();
                if (!messageContent.isEmpty()) {
                    MessageManager.sendMessage(user.getUsername(), recipient, messageContent);
                    NewClass2.autoSaveMessages(MessageManager.getAllMessages());
                    JOptionPane.showMessageDialog(null, "Message sent successfully!", "Send Message", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Message cannot be empty.", "Send Message", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private static void showSentMessages(User user) {
        List<MessageManager.Message> sentMessages = MessageManager.getSentMessages(user.getUsername());
        
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sent messages.", "Sent Messages", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sent = new StringBuilder("=== SENT MESSAGES ===\n\n");
        for (int i = 0; i < sentMessages.size(); i++) {
            MessageManager.Message msg = sentMessages.get(i);
            sent.append((i + 1)).append(". To: ").append(msg.getReceiver())
                .append(" (").append(msg.getTimestamp()).append(")\n")
                .append(msg.getContent()).append("\n\n");
        }
        
        JTextArea textArea = new JTextArea(sent.toString());
        textArea.setEditable(false);
        textArea.setRows(15);
        textArea.setColumns(50);
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        JOptionPane.showMessageDialog(null, scrollPane, "Sent Messages", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private static void showUserProfile(User user) {
        String profile = "Username: " + user.getUsername() + "\n" +
                        "Phone: " + user.getPhone() + "\n" +
                        "Password: " + "*".repeat(user.getPassword().length());
        
        JOptionPane.showMessageDialog(null, profile, "User Profile", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private static void changePassword(User user) {
        JPasswordField newPasswordField = new JPasswordField();
        Object[] message = {
            "Enter new password (8 characters, uppercase, number, special):", newPasswordField
        };

        int result = JOptionPane.showConfirmDialog(null, message, "Change Password", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newPassword = new String(newPasswordField.getPassword());
            
            if (!isValidPassword(newPassword)) {
                JOptionPane.showMessageDialog(null, "Password must be 8 characters and include uppercase letters, numbers, and special characters.");
            } else {
                user.setPassword(newPassword);
                NewClass2.autoSave(registeredUsers);
                JOptionPane.showMessageDialog(null, "Password changed successfully!");
            }
        }
    }

    private static boolean isValidUsername(String username) {
        return username.length() <= 5 && username.contains("_");
    }

    private static boolean isValidPassword(String password) {
        if (password.length() != 8) return false;
        Pattern pattern = Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=]).{8}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private static boolean isValidPhone(String phone) {
        return phone.matches("^\\+(?:[0-9]{1,3})?[0-9]{1,10}$");
    }

    public static class User {
        private String username;
        private String password;
        private String phone;

        public User(String username, String password, String phone) {
            this.username = username;
            this.password = password;
            this.phone = phone;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhone() {
            return phone;
        }
    }
}


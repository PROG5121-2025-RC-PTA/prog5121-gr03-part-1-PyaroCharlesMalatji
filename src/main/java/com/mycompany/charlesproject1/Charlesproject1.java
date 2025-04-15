/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.charlesproject1;
     import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author User
 */
public class Charlesproject1 {

    public static void main(String[] args) {
   

      //JFrame = GUI window to add components to
        JFrame frame = new JFrame();// Creates a frame

        frame.setTitle("Still Learning");
        frame.setVisible(true);
        frame.setSize(500,500);//set the x and y length

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(64, 224, 208));//set background image
        frame.setMinimumSize(new Dimension(300, 500));

        JLabel titleLabel = new JLabel("Welcome to Still Learning");
        titleLabel.setFont(new Font("Monaco", Font.BOLD,24));
        titleLabel.setForeground(new Color(240, 240, 240));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the title label

        //Panel with Buttons on the center
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setFont(new Font("Monaco",Font.PLAIN,18));
        signUpButton.setBackground(Color.BLACK);
        signUpButton.setForeground(new Color(200,200,200));
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.setMaximumSize(new Dimension(200,40));


        JButton loginButton = new JButton("Log in");
        loginButton.setFont(new Font("Monaco",Font.PLAIN,18));
        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(new Color(200,200,200));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(200,40));


        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(signUpButton);
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(loginButton);
        centerPanel.add(Box.createVerticalGlue());

        frame.add(titleLabel, BorderLayout.NORTH); // Added title label to the frame
        frame.add(centerPanel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        signUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSignUpDialog(frame);
            }
        });

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLoginDialog(frame); // Corrected method name
            }
        });
    }

    private static void showSignUpDialog(JFrame parentFrame) {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField phoneField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Username (max 5 chars, incl. '_')")); // Updated label
        panel.add(usernameField);
        panel.add(new JLabel("Password (8 chars, uppercase, number, special)"));
        panel.add(passwordField);
        panel.add(new JLabel("Cellphone (w/ country code, max 10 chars)"));
        panel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(parentFrame, panel, "Sign Up",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String phone = phoneField.getText();

            if (!isValidUsername(username)) {
                JOptionPane.showMessageDialog(parentFrame, "Username must be 5 characters or fewer and contain an underscore."); // Updated message
            } else if (!isValidPassword(password)) {
                JOptionPane.showMessageDialog(parentFrame,
                        "Password must be 8 characters and include uppercase letters, numbers, and special characters.");
            } else if (!isValidPhone(phone)) {
                JOptionPane.showMessageDialog(parentFrame,
                        "Phone number must be 10 characters or fewer and may include a leading '+'."); // Updated message
            } else {
                JOptionPane.showMessageDialog(parentFrame, "Sign Up successful!");
                // In a real application, you would save this user data here.
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
        return phone.matches("^\\+?[0-9]{1,11}$");
    }

    private static void showLoginDialog(JFrame parentFrame) { // Corrected method name
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(parentFrame, panel, "Log In",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String inputUsername = usernameField.getText();
            String inputPassword = new String(passwordField.getPassword());

            // In a real application, you would retrieve the saved username and password
            // from a database or storage. For this example, we'll use hardcoded values.
            String savedUsername = "user_1";
            String savedPassword = "P@sswOrd1";
            String savedFirstName = "Charles";
            String savedLastName = "Malatji";

            if (inputUsername.equals(savedUsername) && inputPassword.equals(savedPassword)) {
                JOptionPane.showMessageDialog(parentFrame,
                        "Welcome " + savedFirstName + " " + savedLastName + ", it is great to see you again.");
                // Here you would typically open the main application window.
            } else {
                JOptionPane.showMessageDialog(parentFrame,
                        "Username or password incorrect, please try again.");
            }
        }
    }
     private static class User {
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

        public String getPhone() {
            return phone;
        }
}
}
    


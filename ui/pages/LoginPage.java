package com.campusevents.ui.pages;

import com.campusevents.dao.UserDAO;
import com.campusevents.model.User;
import com.campusevents.ui.MainFrame;
import com.campusevents.ui.components.Footer;
import com.campusevents.ui.components.ScrollablePanel;
import com.campusevents.util.PasswordUtil;
import com.campusevents.util.SessionManager;
import com.campusevents.util.UIConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

/**
 * Login Authentication Page supporting student and administrator sign-in.
 */
public class LoginPage extends ScrollablePanel {

    private final MainFrame mainFrame;
    private final UserDAO userDAO = new UserDAO();

    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheck;
    private JButton loginButton;

    public LoginPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIConstants.BG_MAIN);
        initUI();
    }

    private void initUI() {
        add(Box.createVerticalStrut(40));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                new EmptyBorder(36, 44, 36, 44)
        ));
        card.setPreferredSize(new Dimension(460, 480));
        card.setMaximumSize(new Dimension(460, 480));

        JLabel title = new JLabel("Welcome Back");
        title.setFont(UIConstants.FONT_HERO);
        title.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Sign in to access your course events and registrations.");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.TEXT_SECONDARY);

        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(28));

        // Email Field
        JLabel emailLabel = new JLabel("College Email Address");
        emailLabel.setFont(UIConstants.FONT_BODY_BOLD);
        emailField = new JTextField();
        emailField.setFont(UIConstants.FONT_BODY);
        emailField.setPreferredSize(new Dimension(370, 38));
        emailField.setMaximumSize(new Dimension(370, 38));

        card.add(emailLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(emailField);
        card.add(Box.createVerticalStrut(16));

        // Password Field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(UIConstants.FONT_BODY_BOLD);
        passwordField = new JPasswordField();
        passwordField.setFont(UIConstants.FONT_BODY);
        passwordField.setPreferredSize(new Dimension(370, 38));
        passwordField.setMaximumSize(new Dimension(370, 38));

        card.add(passLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));

        // Show Password Checkbox
        showPasswordCheck = new JCheckBox("Show Password");
        showPasswordCheck.setFont(UIConstants.FONT_SMALL);
        showPasswordCheck.setOpaque(false);
        showPasswordCheck.addActionListener(e -> {
            if (showPasswordCheck.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });
        card.add(showPasswordCheck);
        card.add(Box.createVerticalStrut(22));

        // Login Action Button
        loginButton = new JButton("Sign In");
        UIConstants.stylePrimaryButton(loginButton);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(370, 40));
        loginButton.setMaximumSize(new Dimension(370, 40));
        loginButton.addActionListener(e -> handleLogin());

        // Press Enter to submit
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        };
        emailField.addKeyListener(enterListener);
        passwordField.addKeyListener(enterListener);

        card.add(loginButton);
        card.add(Box.createVerticalStrut(20));

        // Switch to Sign Up
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        switchPanel.setOpaque(false);

        JLabel promptLabel = new JLabel("Don't have a student account?");
        promptLabel.setFont(UIConstants.FONT_BODY);
        promptLabel.setForeground(UIConstants.TEXT_SECONDARY);

        JButton switchBtn = new JButton("Sign Up Here");
        switchBtn.setFont(UIConstants.FONT_BODY_BOLD);
        switchBtn.setForeground(UIConstants.PRIMARY_BLUE);
        switchBtn.setBorderPainted(false);
        switchBtn.setContentAreaFilled(false);
        switchBtn.setCursor(UIConstants.HAND_CURSOR);
        switchBtn.addActionListener(e -> mainFrame.navigateTo("SIGNUP"));

        switchPanel.add(promptLabel);
        switchPanel.add(switchBtn);
        card.add(switchPanel);

        // Demo Credentials Hint
        JPanel hintPanel = new JPanel();
        hintPanel.setLayout(new BoxLayout(hintPanel, BoxLayout.Y_AXIS));
        hintPanel.setBackground(new Color(241, 243, 244));
        hintPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        hintPanel.setMaximumSize(new Dimension(370, 80));

        JLabel h1 = new JLabel("Demo Accounts:");
        h1.setFont(UIConstants.FONT_SMALL_BOLD);
        JLabel h2 = new JLabel("Admin: admin@campus.com  /  Admin@123");
        h2.setFont(UIConstants.FONT_SMALL);
        JLabel h3 = new JLabel("Student: student@campus.com  /  Student@123");
        h3.setFont(UIConstants.FONT_SMALL);

        hintPanel.add(h1);
        hintPanel.add(h2);
        hintPanel.add(h3);

        card.add(Box.createVerticalStrut(16));
        card.add(hintPanel);

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.setOpaque(false);
        centerWrapper.add(card);

        add(centerWrapper);
        add(Box.createVerticalStrut(40));
        add(Box.createVerticalGlue());
        add(new Footer(mainFrame));
    }

    public void clearFields() {
        emailField.setText("");
        passwordField.setText("");
        showPasswordCheck.setSelected(false);
        passwordField.setEchoChar('•');
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your college email address.", "Validation Notice", JOptionPane.WARNING_MESSAGE);
            emailField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your password.", "Validation Notice", JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocus();
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Verifying...");

        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                User user = userDAO.findByEmail(email);
                if (user == null) {
                    throw new Exception("No account registered with email: " + email);
                }

                boolean match = PasswordUtil.verifyPassword(password, user.getPasswordHash());
                if (!match) {
                    throw new Exception("Invalid password. Please check your credentials.");
                }

                return user;
            }

            @Override
            protected void done() {
                loginButton.setEnabled(true);
                loginButton.setText("Sign In");

                try {
                    User authenticatedUser = get();
                    SessionManager.getInstance().login(authenticatedUser);
                    clearFields();

                    if (authenticatedUser.isAdmin()) {
                        mainFrame.navigateTo("ADMIN_DASHBOARD");
                    } else {
                        mainFrame.navigateTo("EVENTS");
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginPage.this, ex.getMessage(), "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                    passwordField.requestFocus();
                }
            }
        }.execute();
    }
}


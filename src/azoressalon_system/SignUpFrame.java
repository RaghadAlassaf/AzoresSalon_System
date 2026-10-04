/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package azoressalon_system;

/**
 *
 * @author ragha
 */


import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SignUpFrame extends JFrame {

    static final Color BROWN = new Color(0x73, 0x4B, 0x3A);
    static final Color BEIGE = new Color(0xE9, 0xDF, 0xDA);
    static final Color FIELD_BORDER = new Color(0xCF, 0xC8, 0xC5);
    static final Color LABEL_TEXT = new Color(0x6B, 0x5A, 0x57);

    static final Font TITLE = new Font("Serif", Font.BOLD, 30);
    static final Font BOLD = new Font("Serif", Font.BOLD, 16);
    static final Font NORMAL = new Font("Serif", Font.PLAIN, 15);

    private static final Map<String, String[]> users = new HashMap<>();
    private static String currentCustomerName;

    private JTextField nameField, mobileField;
    private JPasswordField passwordField, confirmPasswordField;

    public SignUpFrame() {
        super("Azores Salon");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);

        add(buildSignUpScreen());
    }

    private JPanel buildSignUpScreen() {
        JPanel shell = screenShell();

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        JPanel card = makeCard(500);

        card.add(centerLabel("Create Account", TITLE, Color.BLACK));
        card.add(Box.createVerticalStrut(16));

        nameField = new JTextField();
        mobileField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        card.add(fieldGroup("Full Name", nameField));
        card.add(Box.createVerticalStrut(10));
        card.add(fieldGroup("Mobile Number", mobileField));
        card.add(Box.createVerticalStrut(10));
        card.add(fieldGroup("Password", passwordField));
        card.add(Box.createVerticalStrut(10));
        card.add(fieldGroup("Confirm Password", confirmPasswordField));
        card.add(Box.createVerticalStrut(16));

        JButton create = makeMainButton("Create Account");
        create.addActionListener(e -> signUp());
        card.add(create);

        card.add(Box.createVerticalStrut(14));
        card.add(centerLabel("Already have an account?", NORMAL, LABEL_TEXT));

        JButton login = makeLinkButton("Login");
        login.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        card.add(login);

        center.add(card);
        shell.add(center, BorderLayout.CENTER);

        return shell;
    }

    private void signUp() {
        String name = nameField.getText().trim();
        String mobile = mobileField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        if (name.isEmpty() || mobile.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        if (!mobile.matches("05\\d{8}")) {
            showError("Mobile number must be 10 digits and start with 05.");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }

        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }

        if (users.containsKey(mobile)) {
            showError("This mobile number is already registered.");
            return;
        }

        users.put(mobile, new String[]{name, password});

        JOptionPane.showMessageDialog(this, "Account created successfully!\nYou can login now.");

        new LoginFrame().setVisible(true);
        dispose();
    }

    public static boolean checkLogin(String mobile, String password) {
        String[] user = users.get(mobile);

        if (user != null && user[1].equals(password)) {
            currentCustomerName = user[0];
            return true;
        }

        return false;
    }

    public static String getCurrentCustomerName() {
        return currentCustomerName;
    }

    private JPanel screenShell() {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(BEIGE);
        shell.add(createHeader(), BorderLayout.NORTH);
        return shell;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(BROWN);
        header.setPreferredSize(new Dimension(10, 55));

        JLabel salonName = new JLabel("Azores Salon");
        salonName.setFont(new Font("Serif", Font.BOLD, 22));
        salonName.setForeground(Color.WHITE);

        header.add(salonName);
        return header;
    }

    private JPanel makeCard(int height) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(new LineBorder(BROWN, 1), new EmptyBorder(28, 22, 24, 22)));
        card.setPreferredSize(new Dimension(360, height));
        return card;
    }

    private JPanel fieldGroup(String title, JTextField field) {
        styleField(field);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel label = new JLabel(title);
        label.setFont(BOLD);
        label.setForeground(LABEL_TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);

        return panel;
    }

    private void styleField(JTextField field) {
        field.setFont(NORMAL);
        field.setBackground(Color.WHITE);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setPreferredSize(new Dimension(100, 35));
        field.setBorder(new CompoundBorder(new LineBorder(FIELD_BORDER, 1), new EmptyBorder(4, 10, 4, 10)));
    }

    private JButton makeMainButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BOLD);
        button.setBackground(BROWN);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(100, 38));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton makeLinkButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BOLD);
        button.setForeground(BROWN);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JLabel centerLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
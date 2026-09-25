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

public class LoginFrame extends JFrame {

    static final Color BROWN = new Color(0x73, 0x4B, 0x3A);
    static final Color BEIGE = new Color(0xE9, 0xDF, 0xDA);
    static final Color FIELD_BORDER = new Color(0xCF, 0xC8, 0xC5);
    static final Color LABEL_TEXT = new Color(0x6B, 0x5A, 0x57);

    static final Font TITLE = new Font("Serif", Font.BOLD, 30);
    static final Font BOLD = new Font("Serif", Font.BOLD, 16);
    static final Font NORMAL = new Font("Serif", Font.PLAIN, 15);

    private JTextField mobileField;
    private JPasswordField passwordField;

    public LoginFrame() {
        super("Azores Salon");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 680);
        setLocationRelativeTo(null);

        add(buildLoginScreen());
    }

    private JPanel buildLoginScreen() {
        JPanel shell = screenShell();

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        JPanel card = makeCard(420);

        card.add(centerLabel("Welcome Back", TITLE, Color.BLACK));
        card.add(Box.createVerticalStrut(16));

        mobileField = new JTextField();
        passwordField = new JPasswordField();

        card.add(fieldGroup("Mobile Number", mobileField));
        card.add(Box.createVerticalStrut(10));
        card.add(fieldGroup("Password", passwordField));
        card.add(Box.createVerticalStrut(16));

        JButton login = makeMainButton("Login");
        login.addActionListener(e -> login());
        card.add(login);

        card.add(Box.createVerticalStrut(14));
        card.add(centerLabel("Don't have an account?", NORMAL, LABEL_TEXT));

        JButton signUp = makeLinkButton("Sign Up");
        signUp.addActionListener(e -> {
            new SignUpFrame().setVisible(true);
            dispose();
        });

        card.add(signUp);
        card.add(Box.createVerticalGlue());

        center.add(card);
        shell.add(center, BorderLayout.CENTER);

        return shell;
    }

    private void login() {
        String mobile = mobileField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (mobile.isEmpty() || password.isEmpty()) {
            showError("Please enter your mobile number and password.");
            return;
        }

        if (!SignUpFrame.checkLogin(mobile, password)) {
            showError("Wrong mobile number or password.");
            return;
        }

        new ServicesFrame().setVisible(true);
        dispose();
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
        header.setPreferredSize(new Dimension(10, 65));

        JLabel salonName = new JLabel("Azores Salon");
        salonName.setFont(new Font("Serif", Font.BOLD, 24));
        salonName.setForeground(Color.WHITE);

        header.add(salonName);
        return header;
    }

    private JPanel makeCard(int height) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(new LineBorder(BROWN, 1), new EmptyBorder(28, 22, 24, 22)));
        card.setPreferredSize(new Dimension(390, height));
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
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setPreferredSize(new Dimension(100, 38));
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
        button.setPreferredSize(new Dimension(100, 40));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
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
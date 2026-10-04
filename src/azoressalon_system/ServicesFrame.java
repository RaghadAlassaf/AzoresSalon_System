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
import java.net.URL;

public class ServicesFrame extends JFrame {

    static final Color BROWN = new Color(0x73, 0x4B, 0x3A);
    static final Color BEIGE = new Color(0xE9, 0xDF, 0xDA);
    static final Font TITLE = new Font("Serif", Font.BOLD, 30);
    static final Font BOLD = new Font("Serif", Font.BOLD, 16);
    static final Font NORMAL = new Font("Serif", Font.PLAIN, 15);

    public ServicesFrame() {
        super("Azores Salon");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);

        add(buildServicesScreen());
    }

    private JPanel buildServicesScreen() {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(BEIGE);

        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(20, 70, 40, 70));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JButton logout = makeSecondaryButton("Logout", null);
        JButton cart = makeSecondaryButton("Cart", "/images/cart.png");

        logout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        cart.addActionListener(e -> {
            new CartFrame().setVisible(true);
            dispose();
        });

        JLabel title = new JLabel("Our Services", SwingConstants.CENTER);
        title.setFont(TITLE);
        title.setForeground(BROWN);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(110, 40));
        left.add(logout);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(110, 40));
        right.add(cart);

        top.add(left, BorderLayout.WEST);
        top.add(title, BorderLayout.CENTER);
        top.add(right, BorderLayout.EAST);

        JPanel services = new JPanel(new GridLayout(2, 2, 20, 20));
        services.setOpaque(false);

        services.add(serviceCard("Hair", "/images/hair.png"));
        services.add(serviceCard("Makeup", "/images/makeup.png"));
        services.add(serviceCard("Nails", "/images/nails.png"));
        services.add(serviceCard("Skin Care", "/images/skincare.png"));

        content.add(top, BorderLayout.NORTH);
        content.add(services, BorderLayout.CENTER);

        shell.add(createHeader(), BorderLayout.NORTH);
        shell.add(content, BorderLayout.CENTER);

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

    private JPanel serviceCard(String category, String imagePath) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(BROWN, 1));

        JLabel imageLabel = new JLabel();
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon icon = loadIcon(imagePath, 80, 80);

        if (icon != null) {
            imageLabel.setIcon(icon);
        } else {
            imageLabel.setText("Image");
            imageLabel.setFont(NORMAL);
        }

        JLabel name = new JLabel(category);
        name.setFont(new Font("Serif", Font.BOLD, 20));
        name.setForeground(BROWN);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton view = makeMainButton("View Services");
        view.setAlignmentX(Component.CENTER_ALIGNMENT);

        view.addActionListener(e -> {
            new CategoryServicesFrame(category).setVisible(true);
            dispose();
        });

        card.add(Box.createVerticalGlue());
        card.add(imageLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(name);
        card.add(Box.createVerticalStrut(12));
        card.add(view);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private JButton makeMainButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BOLD);
        button.setBackground(BROWN);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(135, 36));
        return button;
    }

    private JButton makeSecondaryButton(String text, String imagePath) {
        JButton button = new JButton(text);
        button.setFont(BOLD);
        button.setBackground(Color.WHITE);
        button.setForeground(BROWN);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(new LineBorder(BROWN, 1));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(90, 34));

        if (imagePath != null) {
            ImageIcon icon = loadIcon(imagePath, 15, 15);
            if (icon != null) button.setIcon(icon);
        }

        return button;
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        URL imageURL = getClass().getResource(path);
        if (imageURL == null) return null;

        ImageIcon icon = new ImageIcon(imageURL);
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);

        return new ImageIcon(image);
    }
}
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

public class AppointmentFrame extends JFrame {

    static final Color BROWN = new Color(0x73, 0x4B, 0x3A);
    static final Color BEIGE = new Color(0xE9, 0xDF, 0xDA);
    static final Font TITLE = new Font("Serif", Font.BOLD, 30);
    static final Font BOLD = new Font("Serif", Font.BOLD, 16);
    static final Font NORMAL = new Font("Serif", Font.PLAIN, 15);
    static final Font SMALL = new Font("Serif", Font.PLAIN, 13);

    private final String category;
    private final String serviceName;
    private final String description;
    private final int servicePrice;
    private final int duration;

    private JComboBox<String> dateBox, timeBox;

    public AppointmentFrame(String category, String serviceName, String description, int servicePrice, int duration) {
        super("Azores Salon");

        this.category = category;
        this.serviceName = serviceName;
        this.description = description;
        this.servicePrice = servicePrice;
        this.duration = duration;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);

        add(buildAppointmentScreen());
    }

    private JPanel buildAppointmentScreen() {
        JPanel shell = screenShell();

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(15, 26, 20, 26));

        JPanel cartPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        cartPanel.setOpaque(false);

        JButton cartButton = makeSmallSecondaryButton("Cart", "/images/cart.png");

        cartButton.addActionListener(e -> {
            new CartFrame().setVisible(true);
            dispose();
        });

        cartPanel.add(cartButton);
        content.add(cartPanel, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(new LineBorder(BROWN, 1), new EmptyBorder(18, 22, 22, 22)));
        card.setPreferredSize(new Dimension(440, 390));

        card.add(label(serviceName, TITLE, Color.BLACK));
        card.add(label(description, SMALL, BROWN));
        card.add(Box.createVerticalStrut(22));

        JPanel info = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        info.setOpaque(false);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(twoLines("Price", servicePrice + " SAR"));
        info.add(twoLines("Duration", duration + " min"));

        card.add(info);
        card.add(Box.createVerticalStrut(22));

        card.add(label("Date", BOLD, Color.BLACK));

        dateBox = new JComboBox<>(new String[]{ "September 20", "September 21", "September 22", "September 23", "September 24" });

        styleCombo(dateBox);
        card.add(dateBox);
        card.add(Box.createVerticalStrut(14));

        card.add(label("Time", BOLD, Color.BLACK));

        timeBox = new JComboBox<>(new String[]{"10:00 AM", "12:00 PM", "2:00 PM", "4:00 PM", "5:00 PM", "6:00 PM" });

        timeBox.setSelectedItem("4:00 PM");
        styleCombo(timeBox);

        card.add(timeBox);
        card.add(Box.createVerticalGlue());
        card.add(Box.createVerticalStrut(20));

        JPanel buttons = new JPanel(new BorderLayout());
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JButton back = makeSmallSecondaryButton("Back", "/images/back.png");

        back.addActionListener(e -> {
            new CategoryServicesFrame(category).setVisible(true);
            dispose();
        });

        JButton add = makeMainButton("Add to cart");
        add.addActionListener(e -> addToCart());

        buttons.add(back, BorderLayout.WEST);
        buttons.add(add, BorderLayout.EAST);

        card.add(buttons);

        center.add(card);
        content.add(center, BorderLayout.CENTER);

        shell.add(content, BorderLayout.CENTER);

        return shell;
    }

    private void addToCart() {
        String date = (String) dateBox.getSelectedItem();
        String time = (String) timeBox.getSelectedItem();

        if (!CartFrame.addToCart(serviceName, date, time, servicePrice)) {
            JOptionPane.showMessageDialog(this, "You already have a service at this date and time.\nChoose a different time.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new CartFrame().setVisible(true);
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
        header.setPreferredSize(new Dimension(10, 55));

        JLabel salonName = new JLabel("Azores Salon");
        salonName.setFont(new Font("Serif", Font.BOLD, 22));
        salonName.setForeground(Color.WHITE);

        header.add(salonName);
        return header;
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
        button.setMargin(new Insets(2, 6, 2, 6));
        button.setPreferredSize(new Dimension(170, 38));
        return button;
    }

    private JButton makeSmallSecondaryButton(String text, String imagePath) {
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

    private JLabel label(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void styleCombo(JComboBox<String> box) {
        box.setFont(NORMAL);
        box.setBackground(Color.WHITE);
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        box.setBorder(new LineBorder(BROWN, 1));
    }

    private JPanel twoLines(String top, String bottom) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 0, 30));

        panel.add(label(top, BOLD, Color.BLACK));
        panel.add(label(bottom, NORMAL, BROWN));

        return panel;
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        URL imageURL = getClass().getResource(path);
        if (imageURL == null) return null;

        ImageIcon icon = new ImageIcon(imageURL);
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);

        return new ImageIcon(image);
    }
}
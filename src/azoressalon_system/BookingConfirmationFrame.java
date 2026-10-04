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
import java.util.List;

public class BookingConfirmationFrame extends JFrame {

    static final Color BROWN = new Color(0x73, 0x4B, 0x3A);
    static final Color BEIGE = new Color(0xE9, 0xDF, 0xDA);
    static final Color GRAY_TEXT = new Color(0x99, 0x99, 0x99);

    static final Font TITLE = new Font("Serif", Font.BOLD, 30);
    static final Font BOLD = new Font("Serif", Font.BOLD, 16);
    static final Font NORMAL = new Font("Serif", Font.PLAIN, 15);
    static final Font SMALL = new Font("Serif", Font.PLAIN, 13);

    public BookingConfirmationFrame() {
        super("Azores Salon");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);

        add(buildConfirmationScreen());
    }

    private JPanel buildConfirmationScreen() {
        JPanel shell = screenShell();

        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(16, 26, 22, 26));

        JLabel title = new JLabel("Confirm Your Booking");
        title.setFont(TITLE);
        title.setForeground(Color.BLACK);

        content.add(title, BorderLayout.NORTH);

        JPanel middle = new JPanel();
        middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
        middle.setOpaque(false);

        List<CartFrame.CartItem> cart = CartFrame.getCart();

        JPanel table = buildOrderTable(cart);
        middle.add(table);

        content.add(middle, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setOpaque(false);

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);

        JLabel totalText = new JLabel("Total");
        totalText.setFont(BOLD);

        JLabel totalPrice = new JLabel(CartFrame.getTotal() + " SAR");
        totalPrice.setFont(BOLD);

        totalRow.add(totalText, BorderLayout.WEST);
        totalRow.add(totalPrice, BorderLayout.EAST);

        bottom.add(totalRow);
        bottom.add(Box.createVerticalStrut(14));

        JPanel buttons = new JPanel(new BorderLayout());
        buttons.setOpaque(false);

        JButton back = makeSmallSecondaryButton("Back", "/images/back.png");

        back.addActionListener(e -> {
            new CartFrame().setVisible(true);
            dispose();
        });

        JButton confirm = makeMainButton("Confirm Booking");

        confirm.addActionListener(e -> {
            new InvoiceFrame().setVisible(true);
            dispose();
        });

        buttons.add(back, BorderLayout.WEST);
        buttons.add(confirm, BorderLayout.EAST);

        bottom.add(buttons);

        content.add(bottom, BorderLayout.SOUTH);
        shell.add(content, BorderLayout.CENTER);

        return shell;
    }

    private JPanel buildOrderTable(List<CartFrame.CartItem> items) {
        JPanel table = new JPanel();
        table.setLayout(new BoxLayout(table, BoxLayout.Y_AXIS));
        table.setOpaque(false);
        table.setAlignmentX(Component.LEFT_ALIGNMENT);
        table.setBorder(new LineBorder(BROWN, 1));

        table.add(tableRow("Service", "Date", "Time", "Price", true));

        for (CartFrame.CartItem item : items) {
            table.add(new JSeparator());
            table.add(tableRow(item.name, item.date, item.time, item.price + " SAR", false));
        }

        int tableHeight = 45 + (items.size() * 45);
        table.setPreferredSize(new Dimension(570, tableHeight));
        table.setMaximumSize(new Dimension(570, tableHeight));

        return table;
    }

    private JPanel tableRow(String service, String date, String time, String price, boolean header) {
        JPanel row = new JPanel(new GridLayout(1, 4));
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(8, 14, 8, 14));

        row.add(label(service, BOLD, Color.BLACK));
        row.add(label(date, header ? BOLD : SMALL, header ? Color.BLACK : GRAY_TEXT));
        row.add(label(time, header ? BOLD : SMALL, header ? Color.BLACK : GRAY_TEXT));
        row.add(label(price, BOLD, Color.BLACK));

        return row;
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

        return label;
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        URL imageURL = getClass().getResource(path);

        if (imageURL == null) return null;

        ImageIcon icon = new ImageIcon(imageURL);
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);

        return new ImageIcon(image);
    }
}
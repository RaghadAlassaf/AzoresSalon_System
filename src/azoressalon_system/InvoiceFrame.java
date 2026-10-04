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
import java.io.*;

public class InvoiceFrame extends JFrame {

    static final Color BROWN = new Color(0x73, 0x4B, 0x3A);
    static final Color BEIGE = new Color(0xE9, 0xDF, 0xDA);
    static final Color GRAY_TEXT = new Color(0x99, 0x99, 0x99);
    static final Color GREEN = new Color(0x2E, 0x7D, 0x32);

    static final Font TITLE = new Font("Serif", Font.BOLD, 30);
    static final Font BOLD = new Font("Serif", Font.BOLD, 16);
    static final Font NORMAL = new Font("Serif", Font.PLAIN, 15);
    static final Font SMALL = new Font("Serif", Font.PLAIN, 13);

    private static int bookingCounter = 1001;

    private final String bookingId;

    public InvoiceFrame() {
        super("Azores Salon");

        bookingId = "AZ-" + bookingCounter++;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);

        add(buildInvoiceScreen());

        saveInvoiceToFile();
    }

    private JPanel buildInvoiceScreen() {
        JPanel shell = screenShell();

        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(16, 26, 22, 26));

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("Booking Confirmed");
        title.setFont(TITLE);
        title.setForeground(Color.BLACK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel bookingIdLabel = new JLabel("Booking ID: " + bookingId);
        bookingIdLabel.setFont(BOLD);
        bookingIdLabel.setForeground(GRAY_TEXT);
        bookingIdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(8));
        titleBlock.add(bookingIdLabel);

        content.add(titleBlock, BorderLayout.NORTH);

        JPanel middle = new JPanel();
        middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
        middle.setOpaque(false);

        List<CartFrame.CartItem> cart = CartFrame.getCart();

        middle.add(buildOrderTable(cart));
        middle.add(Box.createVerticalStrut(14));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel totalText = new JLabel("Total");
        totalText.setFont(BOLD);

        JLabel totalPrice = new JLabel(CartFrame.getTotal() + " SAR");
        totalPrice.setFont(BOLD);

        totalRow.add(totalText, BorderLayout.WEST);
        totalRow.add(totalPrice, BorderLayout.EAST);

        middle.add(totalRow);
        middle.add(Box.createVerticalStrut(16));

        JLabel status = new JLabel("Status: Confirmed", SwingConstants.CENTER);
        status.setFont(BOLD);
        status.setForeground(Color.WHITE);
        status.setBackground(GREEN);
        status.setOpaque(true);
        status.setAlignmentX(Component.LEFT_ALIGNMENT);
        status.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        status.setPreferredSize(new Dimension(100, 36));
        status.setBorder(new EmptyBorder(6, 0, 6, 0));

        middle.add(status);
        middle.add(Box.createVerticalStrut(14));

        JButton backToServices = makeSmallSecondaryButton("Back to Services", "/images/back.png");
        backToServices.setPreferredSize(new Dimension(155, 36));
        backToServices.setMaximumSize(new Dimension(155, 36));
        backToServices.setAlignmentX(Component.LEFT_ALIGNMENT);

        backToServices.addActionListener(e -> {
            CartFrame.clearCart();
            new ServicesFrame().setVisible(true);
            dispose();
        });

        middle.add(backToServices);

        content.add(middle, BorderLayout.CENTER);
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
        button.setPreferredSize(new Dimension(100, 36));

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

    private void saveInvoiceToFile() {
        String customerName = SignUpFrame.getCurrentCustomerName();

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("invoices.txt", true));

            writer.write("========== Azores Salon Invoice ==========");
            writer.newLine();
            writer.write("Booking ID: " + bookingId);
            writer.newLine();
            writer.write("Customer Name: " + customerName);
            writer.newLine();
            writer.write("------------------------------------------");
            writer.newLine();

            for (CartFrame.CartItem item : CartFrame.getCart()) {
                writer.write("Service: " + item.name);
                writer.newLine();
                writer.write("Date: " + item.date);
                writer.newLine();
                writer.write("Time: " + item.time);
                writer.newLine();
                writer.write("Price: " + item.price + " SAR");
                writer.newLine();
                writer.write("------------------------------------------");
                writer.newLine();
            }

            writer.write("Total: " + CartFrame.getTotal() + " SAR");
            writer.newLine();
            writer.write("Status: Confirmed");
            writer.newLine();
            writer.write("==========================================");
            writer.newLine();
            writer.newLine();

            writer.close();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving invoice.", "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
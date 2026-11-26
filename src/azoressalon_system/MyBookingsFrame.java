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
import java.io.IOException;
import java.util.List;
import java.net.URL;


public class MyBookingsFrame extends JFrame {
    
    private static final Color BROWN = new Color(0x73, 0x4B, 0x3A);
    private static final Color BEIGE = new Color(0xE9, 0xDF, 0xDA);
    private static final Font BOLD = new Font("Serif", Font.BOLD, 16);
    private static final Font NORMAL = new Font("Serif", Font.PLAIN, 15);
    private final JPanel bookings = new JPanel();

    public MyBookingsFrame() {
        super("Azores Salon");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        add(buildScreen());
        loadBookings();
    }

    // Creates the booking history screen and scrollable content
    private JPanel buildScreen() {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(BEIGE);
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(BROWN);
        header.setPreferredSize(new Dimension(10, 55));
        JLabel salon = new JLabel("Azores Salon");
        salon.setFont(new Font("Serif", Font.BOLD, 22));
        salon.setForeground(Color.WHITE);
        header.add(salon);
        shell.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(20, 35, 25, 35));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JButton back = new JButton("Back");
        back.setFont(BOLD);
        back.setBackground(Color.WHITE);
        back.setForeground(BROWN);
        back.setBorder(new LineBorder(BROWN));
        back.setFocusPainted(false);
        back.setPreferredSize(new Dimension(90, 34));
        
        // Adds the back arrow icon to match the other screens
        ImageIcon backIcon = loadIcon("/images/back.png", 15, 15);
        if (backIcon != null) {
        back.setIcon(backIcon); }
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        back.addActionListener(e -> {
            new ServicesFrame().setVisible(true);
            dispose();
        });
        JLabel title = new JLabel("My Bookings", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 30));
        title.setForeground(BROWN);
        top.add(back, BorderLayout.WEST);
        top.add(title, BorderLayout.CENTER);
        top.add(Box.createHorizontalStrut(90), BorderLayout.EAST);
        content.add(top, BorderLayout.NORTH);

        bookings.setLayout(new BoxLayout(bookings, BoxLayout.Y_AXIS));
        bookings.setBackground(BEIGE);
        JScrollPane scroll = new JScrollPane(bookings);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        content.add(scroll, BorderLayout.CENTER);
        shell.add(content, BorderLayout.CENTER);
        return shell;
    }

    // Retrieves booking records without blocking the GUI
    private void loadBookings() {
        bookings.add(makeLabel("Loading bookings...", NORMAL, BROWN));
        final String token = SignUpFrame.getCurrentSessionToken();
        Runnable loadingTask = () -> {
            try {
                List<String> records = SalonClient.getMyBookings(token);
                if (records.size() % 8 != 0) {
                    throw new IOException("Invalid booking history received from the server.");
                }

                // Updates Swing components on the event dispatch thread
                SwingUtilities.invokeLater(() -> {
                    if (!isDisplayable()) {
                        return;
                    }
                    bookings.removeAll();
                    if (records.isEmpty()) {
                        bookings.add(makeLabel("You have no bookings yet.", NORMAL, BROWN));
                    } else {
                        displayBookings(records);
                    }
                    bookings.revalidate();
                    bookings.repaint();
                });
            } catch (IOException ex) {
                // Displays connection or server errors safely on the GUI
                SwingUtilities.invokeLater(() -> {
                    if (!isDisplayable()) {
                        return;
                    }
                    bookings.removeAll();
                    showError(ex.getMessage());
                    bookings.revalidate();
                    bookings.repaint();
                });
            }
        };

        Thread loadingThread = new Thread(loadingTask);
        loadingThread.start();
    }

    // Groups service records by booking ID and displays one card per booking
    private void displayBookings(List<String> records) {
        int i = 0;
        while (i < records.size()) {
            String id = records.get(i);
            String status = records.get(i + 2);
            String total = records.get(i + 3);
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(Color.WHITE);
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.setBorder(new CompoundBorder(new LineBorder(BROWN), new EmptyBorder(12, 15, 12, 15)));
            card.add(makeLabel("Booking ID: " + id, BOLD, BROWN));
            
            card.add(makeLabel("Status: " + status, BOLD, "Confirmed".equalsIgnoreCase(status) ? new Color(0x2E, 0x7D, 0x32) : BROWN));
            card.add(Box.createVerticalStrut(8));
            while (i < records.size() && id.equals(records.get(i))) {
                card.add(new JSeparator());
                card.add(makeLabel(records.get(i + 4) + " - " + records.get(i + 7) + " SAR", BOLD, Color.BLACK));
                card.add(makeLabel(records.get(i + 5) + " | " + records.get(i + 6), NORMAL, BROWN));
                card.add(Box.createVerticalStrut(8));
                i += 8;
            }
            card.add(new JSeparator());
            card.add(makeLabel("Total: " + total + " SAR", BOLD, BROWN));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
            bookings.add(card);
            bookings.add(Box.createVerticalStrut(12));
        }
    }

    // Displays a descriptive loading error
    private void showError(String message) {
        bookings.add(makeLabel("Unable to load bookings.", NORMAL, BROWN));
        JOptionPane.showMessageDialog(this, message, "Booking History Error", JOptionPane.ERROR_MESSAGE);
    }

    // Creates a label matching the salon interface
    private JLabel makeLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    // Loads and resizes an image icon
    private ImageIcon loadIcon(String path, int width, int height) {

    URL imageURL = getClass().getResource(path);

    if (imageURL == null) {
        return null;
    }

    ImageIcon icon = new ImageIcon(imageURL);
    Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);

    return new ImageIcon(image);
    }
}

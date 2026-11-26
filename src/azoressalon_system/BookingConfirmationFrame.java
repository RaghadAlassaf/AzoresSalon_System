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
import java.util.ArrayList;
import java.io.IOException;

// Handles the booking review screen
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

    // Displays the selected services before the booking is confirmed
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

        // Starts the booking process when the customer confirms the booking
        confirm.addActionListener(e -> confirmBooking(confirm, back));

        buttons.add(back, BorderLayout.WEST);
        buttons.add(confirm, BorderLayout.EAST);
        bottom.add(buttons);
        content.add(bottom, BorderLayout.SOUTH);
        shell.add(content, BorderLayout.CENTER);

        return shell;
    }

    // Sends a booking request to the server for the logged-in customer
    private void confirmBooking(JButton confirm, JButton back) {

        // Retrieves the session token of the authenticated customer
        final String sessionToken = SignUpFrame.getCurrentSessionToken();

        // Checks that the customer has an active session
        if (sessionToken == null || sessionToken.isEmpty()) {
            showBookingError("You must log in before confirming a booking.");
            return;
        }

        // Checks that the cart contains at least one service
        if (CartFrame.getCart().isEmpty()) {
            showBookingError("Your cart is empty. Please select a service first.");
            return;
        }

        // Creates a separate copy of the selected services
        final List<CartFrame.CartItem> items = new ArrayList<>();

        for (CartFrame.CartItem item : CartFrame.getCart()) {
            items.add(new CartFrame.CartItem(item.name, item.date, item.time, item.price));
        }

        // Prevents duplicate submissions while the booking is being processed
        confirm.setEnabled(false);
        back.setEnabled(false);

        // Prevents closing the application while the booking is being saved
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // Sends the booking request on a separate thread
        Runnable bookingTask = () -> {
            try {
                List<String> response = SalonClient.book(sessionToken, items);
                String bookingId = response.get(0);
                int total = Integer.parseInt(response.get(2));
                String invoiceWarning = response.get(3);

                List<CartFrame.CartItem> confirmedItems = new ArrayList<>();
                boolean pricesChanged = false;

                // Uses the confirmed service prices returned by the server
                for (int i = 0; i < items.size(); i++) {
                    CartFrame.CartItem item = items.get(i);
                    int price = Integer.parseInt(response.get(4 + i));
                    if (price != item.price) {
                        pricesChanged = true;
                    }
                    confirmedItems.add(new CartFrame.CartItem(item.name, item.date, item.time, price));
                }

                final boolean showPriceWarning = pricesChanged;

                // Updates the GUI on the Event Dispatch Thread
                SwingUtilities.invokeLater(() -> {
                    if (!isDisplayable()) {
                        return;
                    }
                    setDefaultCloseOperation(EXIT_ON_CLOSE);

                    if (!invoiceWarning.isEmpty()) {
                        JOptionPane.showMessageDialog(this, invoiceWarning, "Invoice Warning", JOptionPane.WARNING_MESSAGE);
                    }

                    if (showPriceWarning) {
                        JOptionPane.showMessageDialog(this, "Service prices have changed. The confirmed total is " + total + " SAR.", "Price Update", JOptionPane.WARNING_MESSAGE);
                    }

                    // Opens the invoice using the confirmed prices
                    new InvoiceFrame(bookingId, confirmedItems, total).setVisible(true);
                    dispose();
                });

            } catch (IOException ex) {
                System.err.println("Booking request error: " + ex.getMessage());
                SwingUtilities.invokeLater(() -> restoreBookingControls(confirm, back, ex.getMessage() + "\nPlease check My Bookings before submitting another request."));

            } catch (RuntimeException ex) {
                System.err.println("Booking processing error: " + ex.getMessage());
                SwingUtilities.invokeLater(() -> restoreBookingControls(confirm, back, "Could not process the booking response. Please check My Bookings before trying again."));
            }
        };

        Thread bookingThread = new Thread(bookingTask);
        bookingThread.start();
    }

    // Restores the booking controls after an unsuccessful request
    private void restoreBookingControls(JButton confirm, JButton back, String message) {
        if (!isDisplayable()) {
            return;
        }
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        confirm.setEnabled(true);
        back.setEnabled(true);
        showBookingError(message);
    }

    // Displays a descriptive booking error message
    private void showBookingError(String message) {
        JOptionPane.showMessageDialog(this, message, "Booking Error", JOptionPane.ERROR_MESSAGE);
    }

    // Creates the table of selected services
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

    // Creates one row of the booking summary
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

    // Creates the main screen container
    private JPanel screenShell() {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(BEIGE);
        shell.add(createHeader(), BorderLayout.NORTH);
        return shell;
    }

    // Creates the salon header
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

    // Creates a main action button
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

    // Creates a small navigation button
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

    // Creates a label with the given font and color
    private JLabel label(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);

        return label;
    }

    // Loads and resizes an image icon
    private ImageIcon loadIcon(String path, int width, int height) {
        URL imageURL = getClass().getResource(path);

        if (imageURL == null) return null;

        ImageIcon icon = new ImageIcon(imageURL);
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);

        return new ImageIcon(image);
    }
}
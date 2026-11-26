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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Handles the services list for a selected category
public class CategoryServicesFrame extends JFrame {

    static final Color BROWN = new Color(0x73, 0x4B, 0x3A);
    static final Color BEIGE = new Color(0xE9, 0xDF, 0xDA);
    static final Font TITLE = new Font("Serif", Font.BOLD, 30);
    static final Font BOLD = new Font("Serif", Font.BOLD, 16);
    static final Font NORMAL = new Font("Serif", Font.PLAIN, 15);

    private final String category;

    public CategoryServicesFrame(String category) {
        super("Azores Salon");
        this.category = category;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);

        add(buildCategoryScreen());
    }

    // Displays the services for the selected category
    private JPanel buildCategoryScreen() {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(BEIGE);

        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(20, 55, 30, 55));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JButton back = makeSecondaryButton("Back", "/images/back.png");
        JButton cart = makeSecondaryButton("Cart", "/images/cart.png");

        back.addActionListener(e -> {
            new ServicesFrame().setVisible(true);
            dispose();
        });

        cart.addActionListener(e -> {
            new CartFrame().setVisible(true);
            dispose();
        });

        JLabel title = new JLabel(category + " Services", SwingConstants.CENTER);
        title.setFont(TITLE);
        title.setForeground(BROWN);

        top.add(back, BorderLayout.WEST);
        top.add(title, BorderLayout.CENTER);
        top.add(cart, BorderLayout.EAST);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        content.add(top, BorderLayout.NORTH);
        content.add(list, BorderLayout.CENTER);

        shell.add(createHeader(), BorderLayout.NORTH);
        shell.add(content, BorderLayout.CENTER);

        // Loads the selected services without blocking the GUI
        loadServices(list);

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

    // Creates a card showing a service and its booking button
    private JPanel serviceRow(String name, String description, int price, int duration) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(new CompoundBorder(new LineBorder(BROWN, 1), new EmptyBorder(10, 15, 10, 15)));
        row.setPreferredSize(new Dimension(560, 92));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(BOLD);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(NORMAL);
        descriptionLabel.setForeground(BROWN);

        JLabel details = new JLabel(price + " SAR     |     " + duration + " min");
        details.setFont(NORMAL);

        info.add(nameLabel);
        info.add(Box.createVerticalStrut(4));
        info.add(descriptionLabel);
        info.add(Box.createVerticalStrut(6));
        info.add(details);

        JButton book = makeMainButton("Book Now");

        book.addActionListener(e -> {
            new AppointmentFrame(category, name, description, price, duration).setVisible(true);
            dispose();
        });

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(book);

        row.add(info, BorderLayout.CENTER);
        row.add(buttonPanel, BorderLayout.EAST);

        return row;
    }

    // Loads services from the server in a background thread
    private void loadServices(JPanel list) {

        // Displays a temporary message while waiting for the server
        JLabel loading = new JLabel("Loading services...");
        loading.setFont(NORMAL);
        loading.setForeground(BROWN);
        loading.setAlignmentX(Component.CENTER_ALIGNMENT);

        list.add(loading);

        Runnable loadingTask = () -> {
            try {
                    // Requests and validates services in the background
                Object[][] services = getServices();

                // Updates Swing components on the event dispatch thread
                SwingUtilities.invokeLater(() -> {
                    if (!isDisplayable()) {
                        return;
                    }

                    list.removeAll();

                    if (services.length == 0) {
                        JLabel empty = new JLabel("No services available in this category.");
                        empty.setFont(NORMAL);
                        empty.setForeground(BROWN);
                        empty.setAlignmentX(Component.CENTER_ALIGNMENT);
                        list.add(empty);
                    } else {
                        // Displays each service using the existing card design
                        for (Object[] service : services) {
                            list.add(serviceRow((String) service[0], (String) service[1], (int) service[2], (int) service[3]));
                            list.add(Box.createVerticalStrut(8));
                        }
                    }

                    list.revalidate();
                    list.repaint();
                });

            } catch (IOException ex) {
                // Displays network or invalid service information errors
                SwingUtilities.invokeLater(() -> displayLoadingError(list, ex.getMessage()));

            } catch (RuntimeException ex) {
                System.err.println("Service loading error: " + ex.getMessage());
                SwingUtilities.invokeLater(() -> displayLoadingError(list, "An unexpected error occurred while loading services."));
            }
        };

        Thread loadingThread = new Thread(loadingTask);
        loadingThread.start();
    }

    // Refreshes the services panel after a loading failure
    private void displayLoadingError(JPanel list, String message) {
        if (!isDisplayable()) {
            return;
        }

        list.removeAll();
        showLoadError(list, message);
        list.revalidate();
        list.repaint();
    }

    // Requests and validates the selected services received from the server
    private Object[][] getServices() throws IOException {

        ArrayList<Object[]> services = new ArrayList<>();

        // Receives service information through the client/server connection
        List<String> response = SalonClient.getServices(category);

        // Each service contains a name, description, price and duration
        if (response.size() % 4 != 0) {
            throw new IOException("Incomplete service information received from the server.");
        }

        // Converts the received information into service records
        for (int i = 0; i < response.size(); i += 4) {

            String name = response.get(i).trim();
            String description = response.get(i + 1).trim();

            int price;
            int duration;

            try {

                // Converts the received numeric values
                price = Integer.parseInt(response.get(i + 2).trim());
                duration = Integer.parseInt(response.get(i + 3).trim());

            } catch (NumberFormatException ex) {

                throw new IOException("Unable to load services: price and duration must be numbers.", ex);
            }

            // Validates the received service information
            if (name.isEmpty() || description.isEmpty() || price < 0 || duration <= 0) {
                throw new IOException("Invalid service information received from the server.");
            }

            services.add(new Object[]{name, description, price, duration});
        }

        return services.toArray(new Object[0][]);
    }

    // Displays a descriptive error when services cannot be loaded
    private void showLoadError(JPanel list, String message) {

        JLabel error = new JLabel("Unable to load salon services.");
        error.setFont(NORMAL);
        error.setForeground(BROWN);
        error.setAlignmentX(Component.CENTER_ALIGNMENT);

        list.add(error);

        JOptionPane.showMessageDialog(this, message, "Service Loading Error", JOptionPane.ERROR_MESSAGE);
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
        button.setPreferredSize(new Dimension(115, 36));
        return button;
    }

    // Creates a secondary navigation button
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

    // Loads and resizes an image icon
    private ImageIcon loadIcon(String path, int width, int height) {
        URL imageURL = getClass().getResource(path);
        if (imageURL == null) return null;

        ImageIcon icon = new ImageIcon(imageURL);
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);

        return new ImageIcon(image);
    }
}
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
import java.io.*;
import java.util.ArrayList;

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

        Object[][] services = getServices();

        for (Object[] service : services) {
            list.add(serviceRow((String) service[0], (String) service[1], (int) service[2], (int) service[3]));
            list.add(Box.createVerticalStrut(8));
        }

        content.add(top, BorderLayout.NORTH);
        content.add(list, BorderLayout.CENTER);

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

    private Object[][] getServices() {

        ArrayList<Object[]> services = new ArrayList<>();

    try {
        BufferedReader reader = new BufferedReader(new FileReader("services.txt"));

        String line;
        boolean readingCategory = false;

        while ((line = reader.readLine()) != null) {

            line = line.trim();

            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith("[") && line.endsWith("]")) {
                String fileCategory = line.substring(1, line.length() - 1);
                readingCategory = fileCategory.equalsIgnoreCase(category);
                continue;
            }

            if (readingCategory) {
                String[] parts = line.split("\\|");

                if (parts.length == 4) {
                    String name = parts[0];
                    String description = parts[1];
                    int price = Integer.parseInt(parts[2]);
                    int duration = Integer.parseInt(parts[3]);

                    services.add(new Object[]{name, description, price, duration});
                }
            }
        }

        reader.close();

    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error reading services.txt:\n" + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Error: Price or duration in services.txt is not a number.", "File Error", JOptionPane.ERROR_MESSAGE);
    }

    return services.toArray(new Object[0][]);
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
        button.setPreferredSize(new Dimension(115, 36));
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
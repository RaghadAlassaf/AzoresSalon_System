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
        setSize(820, 680);
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
            list.add(Box.createVerticalStrut(12));
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
        header.setPreferredSize(new Dimension(10, 65));

        JLabel salonName = new JLabel("Azores Salon");
        salonName.setFont(new Font("Serif", Font.BOLD, 24));
        salonName.setForeground(Color.WHITE);

        header.add(salonName);
        return header;
    }

    private JPanel serviceRow(String name, String description, int price, int duration) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(new CompoundBorder(new LineBorder(BROWN, 1), new EmptyBorder(10, 15, 10, 15)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

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
        if (category.equals("Hair")) {
            return new Object[][]{
                {"Blow Dry", "Professional blow dry service", 100, 45},
                {"Hair Dye", "Professional hair coloring service", 150, 90},
                {"Hair Cut", "Professional hair cutting service", 80, 30},
                {"Hair Styling", "Hair styling for different occasions", 120, 60}
            };
        }

        if (category.equals("Makeup")) {
            return new Object[][]{
                {"Full Makeup", "Complete makeup look", 170, 60},
                {"Soft Makeup", "Simple and soft makeup look", 150, 50},
                {"Eye Makeup", "Professional eye makeup", 80, 30},
                {"Bridal Makeup", "Complete bridal makeup", 300, 120}
            };
        }

        if (category.equals("Nails")) {
            return new Object[][]{
                {"Manicure", "Basic nail care for hands", 60, 30},
                {"Pedicure", "Basic nail care for feet", 70, 40},
                {"Gel Polish", "Long-lasting gel nail polish", 100, 45},
                {"Nail Extensions", "Professional nail extensions", 180, 90}
            };
        }

        return new Object[][]{
            {"Facial", "Basic facial treatment", 80, 45},
            {"Deep Cleansing", "Deep skin cleansing treatment", 120, 60},
            {"Hydrating Facial", "Hydrating facial treatment", 130, 60},
            {"Skin Treatment", "Special skin care treatment", 150, 75}
        };
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
        button.setPreferredSize(new Dimension(125, 38));
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
        button.setPreferredSize(new Dimension(100, 36));

        if (imagePath != null) {
            ImageIcon icon = loadIcon(imagePath, 17, 17);
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
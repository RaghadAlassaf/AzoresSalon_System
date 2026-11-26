/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package azoressalon_system;

/**
 *
 * @author ragha
 */
import javax.swing.*;

public class AzoresSalon_System {

    /**
     * @param args the command line arguments
     */
    
    // Main entry point of the Azores Salon client application.
    public static void main(String[] args) {
    
    // Sets a consistent appearance for the graphical user interface
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            System.err.println("Could not initialize the interface appearance: " + ex.getMessage());
        }

        // Starts the client GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
      
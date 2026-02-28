package furniture_app;

import javax.swing.*;
import java.awt.*;

public class FurnitureDesignSystem extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;

    public FurnitureDesignSystem() {
        setTitle("LUMION PRO - Furniture Design System 3D");
        setMinimumSize(new Dimension(800, 600));
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize window for full responsiveness
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Initialize UI panels
        LoginPanel loginPanel = new LoginPanel(this);
        DashboardPanel dashboardPanel = new DashboardPanel(this);

        // Add to CardLayout container
        mainContainer.add(loginPanel, "Login");
        mainContainer.add(dashboardPanel, "Dashboard");

        add(mainContainer);
        showLogin();
    }

    public void showLogin() {
        cardLayout.show(mainContainer, "Login");
    }

    public void showDashboard() {
        cardLayout.show(mainContainer, "Dashboard");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set System Look and Feel.");
        }
        
        SwingUtilities.invokeLater(() -> {
            new FurnitureDesignSystem().setVisible(true);
        });
    }
}

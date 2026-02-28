package furniture_app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginPanel extends JPanel {
    
    private final Color ACCENT_COL = new Color(41, 121, 255); 
    
    public LoginPanel(FurnitureDesignSystem mainFrame) {
        setLayout(new GridBagLayout());
        setBackground(new Color(15, 17, 22)); // Deep dark background

        // Central Glass Panel
        JPanel formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Glassmorphism background
                g2.setColor(new Color(30, 33, 40, 240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                // Highlight border
                g2.setColor(new Color(255, 255, 255, 20));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 30, 30);
                super.paintComponent(g);
            }
        };
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(50, 60, 50, 60));
        
        // Brand Title
        JLabel lblTitle = new JLabel("LUMION PRO");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        
        JLabel lblSub = new JLabel("DESIGN STUDIO", SwingConstants.CENTER);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSub.setForeground(ACCENT_COL);
        
        java.util.Map<java.awt.font.TextAttribute, Object> attr = new java.util.HashMap<>();
        attr.put(java.awt.font.TextAttribute.TRACKING, 0.2f);
        lblSub.setFont(lblSub.getFont().deriveFont(attr));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setOpaque(false);
        
        JLabel uLbl = new JLabel("USERNAME"); uLbl.setForeground(new Color(150, 160, 170)); uLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        JTextField txtU = createStyledField(); txtU.setText("admin");

        JLabel pLbl = new JLabel("PASSWORD"); pLbl.setForeground(new Color(150, 160, 170)); pLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        JPasswordField txtP = new JPasswordField("admin") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 22, 28));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        txtP.setOpaque(false);
        txtP.setForeground(Color.WHITE);
        txtP.setCaretColor(Color.WHITE);
        txtP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtP.setBorder(new EmptyBorder(10, 15, 10, 15));
        txtP.setMaximumSize(new Dimension(300, 40));
        
        uLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtU.setAlignmentX(Component.CENTER_ALIGNMENT);
        pLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtP.setAlignmentX(Component.CENTER_ALIGNMENT);

        inputPanel.add(uLbl);
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(txtU);
        inputPanel.add(Box.createVerticalStrut(20));
        inputPanel.add(pLbl);
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(txtP);
        
        JButton btnLogin = new JButton("ACCESS DASHBOARD");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(ACCENT_COL);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setFocusPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setMaximumSize(new Dimension(300, 45));
        
        btnLogin.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnLogin.setBackground(ACCENT_COL.brighter()); }
            public void mouseExited(MouseEvent e) { btnLogin.setBackground(ACCENT_COL); }
        });
        
        btnLogin.addActionListener(e -> mainFrame.showDashboard());
        
        // Form Wrapper for custom button paint
        JPanel btnWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(btnLogin.getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        btnWrapper.setOpaque(false);
        btnWrapper.setMaximumSize(new Dimension(300, 45));
        btnWrapper.add(btnLogin, BorderLayout.CENTER);

        formPanel.add(lblTitle);
        formPanel.add(lblSub);
        formPanel.add(Box.createVerticalStrut(40));
        formPanel.add(inputPanel);
        formPanel.add(Box.createVerticalStrut(35));
        formPanel.add(btnWrapper);

        add(formPanel);
    }
    
    private JTextField createStyledField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 22, 28));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        field.setOpaque(false);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new EmptyBorder(10, 15, 10, 15));
        field.setMaximumSize(new Dimension(300, 40));
        return field;
    }
}

package furniture_app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class DashboardPanel extends JPanel {
    private DesignCanvas canvas;
    private JSlider rotationSlider;
    private JSlider scaleSlider;

    // Theme Colors (Premium Dark/Modern Theme)
    private final Color BG_MAIN = new Color(20, 22, 28);
    private final Color PANEL_BG = new Color(30, 33, 40);
    private final Color TEXT_MAIN = new Color(240, 245, 250);
    private final Color TEXT_SUB = new Color(170, 180, 190);
    private final Color ACCENT_COL = new Color(41, 121, 255); // Vibrant Blue
    private final Color DANGER_COL = new Color(255, 82, 82);

    Font titleFont = new Font("Segoe UI", Font.BOLD, 15);
    Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
    
    public DashboardPanel(FurnitureDesignSystem mainFrame) {
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        canvas = new DesignCanvas();
        // Give canvas a slightly darker border to blend in
        JPanel canvasWrapper = new JPanel(new BorderLayout());
        canvasWrapper.setBackground(BG_MAIN);
        canvasWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Give the canvas a nice rounded wrapper effect by placing a border
        canvas.setBorder(BorderFactory.createLineBorder(new Color(60, 65, 75), 2));
        canvasWrapper.add(canvas, BorderLayout.CENTER);
        
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent evt) {
                updateToolsFromSelection();
            }
        });

        add(canvasWrapper, BorderLayout.CENTER);

        // WEST SIDEBAR
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(300, 0));
        sidebar.setBackground(BG_MAIN);
        sidebar.setBorder(new EmptyBorder(15, 15, 15, 5));

        // -- Room Blueprint Panel --
        RoundedPanel roomPanel = new RoundedPanel(20, PANEL_BG);
        roomPanel.setLayout(new BoxLayout(roomPanel, BoxLayout.Y_AXIS));
        roomPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel roomTitle = new JLabel("Room Blueprint");
        roomTitle.setFont(titleFont);
        roomTitle.setForeground(TEXT_MAIN);
        roomTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel dimPanel = new JPanel(new GridLayout(2, 2, 10, 15));
        dimPanel.setBackground(PANEL_BG);
        dimPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel wLbl = new JLabel("Width (m):"); wLbl.setForeground(TEXT_SUB); wLbl.setFont(labelFont);
        JLabel lLbl = new JLabel("Length (m):"); lLbl.setForeground(TEXT_SUB); lLbl.setFont(labelFont);
        
        JTextField txtW = createStyledTextField("10");
        JTextField txtL = createStyledTextField("10");
        
        dimPanel.add(wLbl); dimPanel.add(txtW);
        dimPanel.add(lLbl); dimPanel.add(txtL);
        
        JPanel btnPanelBox = new JPanel(new GridLayout(1, 2, 10, 10));
        btnPanelBox.setBackground(PANEL_BG);
        btnPanelBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        ModernButton btnUpdateRoom = new ModernButton("Update Grid", new Color(60, 65, 80), TEXT_MAIN);
        btnUpdateRoom.addActionListener(e -> {
            try { canvas.setRoomSize(Integer.parseInt(txtW.getText()), Integer.parseInt(txtL.getText())); } catch(Exception ex) { }
        });
        
        ModernButton btnColor = new ModernButton("Floor Color", new Color(60, 65, 80), TEXT_MAIN);
        btnColor.addActionListener(e -> {
            Color color = JColorChooser.showDialog(this, "Choose Floor Color", Color.LIGHT_GRAY);
            if (color != null) canvas.setWallColor(color);
        });
        
        btnPanelBox.add(btnUpdateRoom);
        btnPanelBox.add(btnColor);

        roomPanel.add(roomTitle);
        roomPanel.add(Box.createVerticalStrut(15));
        roomPanel.add(dimPanel);
        roomPanel.add(Box.createVerticalStrut(15));
        roomPanel.add(btnPanelBox);

        // -- Catalog Panel --
        RoundedPanel catalogPanel = new RoundedPanel(20, PANEL_BG);
        catalogPanel.setLayout(new BoxLayout(catalogPanel, BoxLayout.Y_AXIS));
        catalogPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel catTitle = new JLabel("Furniture Catalog");
        catTitle.setFont(titleFont);
        catTitle.setForeground(TEXT_MAIN);
        catTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel gridCatalog = new JPanel(new GridLayout(3, 2, 10, 10));
        gridCatalog.setBackground(PANEL_BG);
        gridCatalog.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] types = {"Dining Table", "Chair", "Modern Sofa", "Bed", "Cabinet", "Plant"};
        for(String t : types) {
            ModernButton b = new ModernButton(t, new Color(45, 52, 65), TEXT_MAIN);
            b.addActionListener(e -> canvas.addFurniture(t));
            gridCatalog.add(b);
        }
        
        catalogPanel.add(catTitle);
        catalogPanel.add(Box.createVerticalStrut(15));
        catalogPanel.add(gridCatalog);

        sidebar.add(roomPanel);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(catalogPanel);
        
        JScrollPane westScroll = new JScrollPane(sidebar);
        westScroll.setBorder(null);
        westScroll.getVerticalScrollBar().setUnitIncrement(16);
        westScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(westScroll, BorderLayout.WEST);

        // EAST SIDEBAR
        JPanel customToolsPanel = new JPanel();
        customToolsPanel.setLayout(new BoxLayout(customToolsPanel, BoxLayout.Y_AXIS));
        customToolsPanel.setPreferredSize(new Dimension(300, 0));
        customToolsPanel.setBackground(BG_MAIN);
        customToolsPanel.setBorder(new EmptyBorder(15, 5, 15, 15));

        // -- Properties Panel --
        RoundedPanel propPanel = new RoundedPanel(20, PANEL_BG);
        propPanel.setLayout(new BoxLayout(propPanel, BoxLayout.Y_AXIS));
        propPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel propTitle = new JLabel("Object Properties");
        propTitle.setFont(titleFont);
        propTitle.setForeground(TEXT_MAIN);
        propTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblScale = new JLabel("Scale (%)");
        lblScale.setForeground(TEXT_SUB);
        lblScale.setFont(labelFont);
        lblScale.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        scaleSlider = new JSlider(50, 200, 100);
        styleSlider(scaleSlider);

        scaleSlider.addChangeListener(e -> {
            FurnitureItem selected = canvas.getSelectedItem();
            if (selected != null) {
                double factor = scaleSlider.getValue() / 100.0;
                selected.width = (int)(selected.originalWidth * factor);
                selected.height = (int)(selected.originalHeight * factor);
                canvas.repaint();
            }
        });
        
        JLabel lblRot = new JLabel("Rotation (Degrees)");
        lblRot.setForeground(TEXT_SUB);
        lblRot.setFont(labelFont);
        lblRot.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        rotationSlider = new JSlider(0, 360, 0);
        rotationSlider.setMajorTickSpacing(90);
        rotationSlider.setPaintTicks(true);
        styleSlider(rotationSlider);

        rotationSlider.addChangeListener(e -> {
            FurnitureItem selected = canvas.getSelectedItem();
            if (selected != null) {
                selected.rotation = rotationSlider.getValue();
                canvas.repaint();
            }
        });

        ModernButton btnItemColor = new ModernButton("Pick Primary Color", ACCENT_COL, Color.WHITE);
        btnItemColor.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnItemColor.setMaximumSize(new Dimension(200, 40));
        btnItemColor.addActionListener(e -> {
            FurnitureItem selected = canvas.getSelectedItem();
            if (selected != null) {
                canvas.saveStateToUndo();
                Color c = JColorChooser.showDialog(this, "Choose Primary Color", selected.color);
                if (c != null) { selected.color = c; canvas.repaint(); }
            } else {
                JOptionPane.showMessageDialog(this, "Select an item in 2D mode first!", "Notice", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        ModernButton btnSecondaryColor = new ModernButton("Pick Secondary Color", new Color(70, 130, 180), Color.WHITE);
        btnSecondaryColor.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSecondaryColor.setMaximumSize(new Dimension(200, 40));
        btnSecondaryColor.addActionListener(e -> {
            FurnitureItem selected = canvas.getSelectedItem();
            if (selected != null) {
                canvas.saveStateToUndo();
                Color c = JColorChooser.showDialog(this, "Choose Part/Secondary Color", selected.secondaryColor != null ? selected.secondaryColor : selected.color);
                if (c != null) { selected.secondaryColor = c; canvas.repaint(); }
            } else {
                JOptionPane.showMessageDialog(this, "Select an item in 2D mode first!", "Notice", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        ModernButton btnDelete = new ModernButton("Delete Selected", DANGER_COL, Color.WHITE);
        btnDelete.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDelete.setMaximumSize(new Dimension(200, 40));
        btnDelete.addActionListener(e -> canvas.deleteSelected());

        propPanel.add(propTitle);
        propPanel.add(Box.createVerticalStrut(25));
        propPanel.add(lblScale);
        propPanel.add(Box.createVerticalStrut(5));
        propPanel.add(scaleSlider);
        propPanel.add(Box.createVerticalStrut(25));
        propPanel.add(lblRot);
        propPanel.add(Box.createVerticalStrut(5));
        propPanel.add(rotationSlider);
        propPanel.add(Box.createVerticalStrut(20));
        propPanel.add(btnItemColor);
        propPanel.add(Box.createVerticalStrut(10));
        propPanel.add(btnSecondaryColor);
        propPanel.add(Box.createVerticalStrut(15));
        propPanel.add(btnDelete);
        
        // -- Global Panel --
        RoundedPanel globalPanel = new RoundedPanel(20, PANEL_BG);
        globalPanel.setLayout(new BoxLayout(globalPanel, BoxLayout.Y_AXIS));
        globalPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JCheckBox chkShadow = new JCheckBox(" Apply Global Shadows", true);
        chkShadow.setBackground(PANEL_BG);
        chkShadow.setFont(labelFont);
        chkShadow.setForeground(TEXT_MAIN);
        chkShadow.setFocusPainted(false);
        chkShadow.addActionListener(e -> canvas.setApplyShadows(chkShadow.isSelected()));
        
        globalPanel.add(chkShadow);
        
        customToolsPanel.add(propPanel);
        customToolsPanel.add(Box.createVerticalStrut(20));
        customToolsPanel.add(globalPanel);

        JScrollPane eastScroll = new JScrollPane(customToolsPanel);
        eastScroll.setBorder(null);
        eastScroll.getVerticalScrollBar().setUnitIncrement(16);
        eastScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(eastScroll, BorderLayout.EAST);

        // NORTH TOOLBAR
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(15, 17, 22));
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(40, 45, 55)),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoPanel.setOpaque(false);
        
        // Faux Logo Graphic
        JLabel logoBox = new JLabel(" 3D ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT_COL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        logoBox.setForeground(Color.WHITE);
        logoBox.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoBox.setPreferredSize(new Dimension(40, 30));
        logoBox.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel lblHeader = new JLabel("LUMION PRO DESIGN");
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        setLetterSpacing(lblHeader, 0.1f);

        logoPanel.add(logoBox);
        logoPanel.add(lblHeader);
        topBar.add(logoPanel, BorderLayout.WEST);

        JPanel rightTopBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightTopBar.setOpaque(false);
        
        ModernButton btnNew = new ModernButton("New", new Color(45, 52, 65), TEXT_MAIN);
        btnNew.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to start a new design?\nAny unsaved changes will be lost.", "New Design", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                canvas.clearDesign();
            }
        });
        
        ModernButton btnSave = new ModernButton("Save", new Color(45, 52, 65), TEXT_MAIN);
        Action saveAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] options = {"Database", "Local File"};
                int choice = JOptionPane.showOptionDialog(DashboardPanel.this, "Where do you want to save the design?", "Save Design",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                
                if (choice == 0) {
                    String name = JOptionPane.showInputDialog(DashboardPanel.this, "Enter Design Name to Save:", "Save to DB", JOptionPane.PLAIN_MESSAGE);
                    if (name != null && !name.trim().isEmpty()) {
                        canvas.saveDesignToDB(name.trim());
                    }
                } else if (choice == 1) {
                    JFileChooser jfc = new JFileChooser();
                    if(jfc.showSaveDialog(DashboardPanel.this) == JFileChooser.APPROVE_OPTION) {
                        java.io.File file = jfc.getSelectedFile();
                        if (!file.getName().endsWith(".lumion")) file = new java.io.File(file.getAbsolutePath() + ".lumion");
                        canvas.saveDesign(file);
                    }
                }
            }
        };
        btnSave.addActionListener(saveAction);
        
        InputMap im = this.getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "save");
        am.put("save", saveAction);
        
        ModernButton btnLoad = new ModernButton("Load", new Color(45, 52, 65), TEXT_MAIN);
        btnLoad.addActionListener(e -> {
            String[] options = {"Database", "Local File"};
            int choice = JOptionPane.showOptionDialog(this, "Where do you want to load the design from?", "Load Design",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            
            if (choice == 0) {
                String name = JOptionPane.showInputDialog(this, "Enter Design Name to Load:", "Load from DB", JOptionPane.PLAIN_MESSAGE);
                if (name != null && !name.trim().isEmpty()) {
                    canvas.loadDesignFromDB(name.trim());
                }
            } else if (choice == 1) {
                JFileChooser jfc = new JFileChooser();
                if(jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) canvas.loadDesign(jfc.getSelectedFile());
            }
        });
        
        ModernButton toggle3D = new ModernButton("RENDER 3D", ACCENT_COL, Color.WHITE);
        toggle3D.addActionListener(new java.awt.event.ActionListener() {
            boolean is3D = false;
            public void actionPerformed(java.awt.event.ActionEvent e) {
                is3D = !is3D;
                toggle3D.setText(is3D ? "BACK TO 2D" : "RENDER 3D");
                toggle3D.setBackground(is3D ? new Color(255, 140, 0) : ACCENT_COL);
                canvas.setViewMode(is3D);
            }
        });

        ModernButton btnLogout = new ModernButton("Exit", new Color(60, 20, 30), new Color(255, 100, 100));
        btnLogout.addActionListener(e -> mainFrame.showLogin());

        rightTopBar.add(btnNew);
        rightTopBar.add(btnSave);
        rightTopBar.add(btnLoad);
        
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 30));
        sep.setForeground(new Color(60, 65, 75));
        rightTopBar.add(sep);
        
        rightTopBar.add(toggle3D);
        rightTopBar.add(btnLogout);
        
        topBar.add(rightTopBar, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);
    }
    
    private void updateToolsFromSelection() {
        FurnitureItem selected = canvas.getSelectedItem();
        if (selected != null) {
            rotationSlider.setValue((int)selected.rotation);
            int scale = (int)(((double)selected.width / selected.originalWidth) * 100);
            scaleSlider.setValue(scale);
        }
    }

    private void styleSlider(JSlider slider) {
        slider.setBackground(PANEL_BG);
        slider.setForeground(TEXT_SUB);
    }

    private JTextField createStyledTextField(String text) {
        JTextField field = new JTextField(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15, 17, 22));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        field.setOpaque(false);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Segoe UI", Font.BOLD, 14));
        field.setBorder(new EmptyBorder(5, 10, 5, 10));
        field.setHorizontalAlignment(JTextField.CENTER);
        return field;
    }

    private void setLetterSpacing(JLabel label, float tracking) {
        java.util.Map<java.awt.font.TextAttribute, Object> attributes = new java.util.HashMap<>();
        attributes.put(java.awt.font.TextAttribute.TRACKING, tracking);
        label.setFont(label.getFont().deriveFont(attributes));
    }

    // --- Custom UI Components ---

    class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color bgColor;

        public RoundedPanel(int radius, Color bgColor) {
            super();
            this.cornerRadius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            
            // Subtle top highlight for glass feel
            g2.setColor(new Color(255, 255, 255, 15));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius);
        }
    }

    class ModernButton extends JButton {
        private Color normalColor;
        private Color hoverColor;

        public ModernButton(String text, Color bg, Color fg) {
            super(text);
            this.normalColor = bg;
            this.hoverColor = bg.brighter();
            
            setForeground(fg);
            setBackground(bg);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(hoverColor);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(normalColor);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            super.paintComponent(g);
        }
    }
}

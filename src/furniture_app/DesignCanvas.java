package furniture_app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class DesignCanvas extends JPanel {
    private ArrayList<FurnitureItem> items = new ArrayList<>();
    private FurnitureItem selectedItem = null;
    private int offsetX, offsetY;
    private Color wallColor = new Color(245, 245, 250);
    private boolean applyShadows = true;
    private boolean is3DMode = false;
    
    public int roomWidthMeters = 10;
    public int roomLengthMeters = 10;
    private final int PIXELS_PER_METER = 50; 

    public DesignCanvas() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mx = e.getX();
                int my = e.getY();
                
                if (is3DMode) {
                    int ox = getWidth() / 2;
                    int oy = getHeight() / 4;
                    double A = (mx - ox) / 0.866;
                    double B = (my - oy) * 2.0;
                    mx = (int) ((A + B) / 2.0);
                    my = (int) ((B - A) / 2.0);
                }

                for (int i = items.size() - 1; i >= 0; i--) {
                    FurnitureItem item = items.get(i);
                    // For tall 3D items, we might need a looser hit test, but footprint mapping works for the base
                    if (item.contains(mx, my)) {
                        if (selectedItem != null) selectedItem.isSelected = false;
                        selectedItem = item;
                        selectedItem.isSelected = true;
                        offsetX = mx - item.x;
                        offsetY = my - item.y;
                        repaint();
                        return;
                    }
                }
                if (selectedItem != null) {
                    selectedItem.isSelected = false;
                    selectedItem = null;
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedItem != null) {
                    int mx = e.getX();
                    int my = e.getY();
                    
                    if (is3DMode) {
                        int ox = getWidth() / 2;
                        int oy = getHeight() / 4;
                        double A = (mx - ox) / 0.866;
                        double B = (my - oy) * 2.0;
                        mx = (int) ((A + B) / 2.0);
                        my = (int) ((B - A) / 2.0);
                    }

                    selectedItem.x = mx - offsetX;
                    selectedItem.y = my - offsetY;
                    repaint();
                }
            }
        });
    }

    public void addFurniture(String type) {
        if ("Dining Table".equals(type)) items.add(new FurnitureItem(type, 100, 100, 120, 80, new Color(139, 69, 19)));
        else if ("Chair".equals(type)) items.add(new FurnitureItem(type, 150, 150, 45, 45, new Color(70, 130, 180)));
        else if ("Modern Sofa".equals(type)) items.add(new FurnitureItem(type, 200, 100, 160, 70, new Color(105, 105, 105)));
        else if ("Bed".equals(type)) items.add(new FurnitureItem(type, 300, 100, 140, 180, new Color(160, 82, 45)));
        else if ("Cabinet".equals(type)) items.add(new FurnitureItem(type, 100, 300, 120, 40, new Color(205, 133, 63)));
        else if ("Plant".equals(type)) items.add(new FurnitureItem(type, 50, 50, 40, 40, new Color(34, 139, 34)));
        repaint();
    }

    public void deleteSelected() {
        if (selectedItem != null) {
            items.remove(selectedItem);
            selectedItem = null;
            repaint();
        }
    }

    public void saveDesign(File file) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(items);
            JOptionPane.showMessageDialog(this, "Design Saved Successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error Saving: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadDesign(File file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            items = (ArrayList<FurnitureItem>) ois.readObject();
            selectedItem = null;
            repaint();
            JOptionPane.showMessageDialog(this, "Design Loaded Successfully!", "Loaded", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error Loading: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setRoomSize(int width, int length) {
        this.roomWidthMeters = width;
        this.roomLengthMeters = length;
        repaint();
    }

    public void setViewMode(boolean is3D) {
        this.is3DMode = is3D;
        repaint();
    }

    public void setWallColor(Color color) {
        this.wallColor = color;
        repaint();
    }
    
    public void setApplyShadows(boolean applyShadows) {
        this.applyShadows = applyShadows;
        repaint();
    }
    
    public FurnitureItem getSelectedItem() {
        return selectedItem;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int rw = roomWidthMeters * PIXELS_PER_METER;
        int rl = roomLengthMeters * PIXELS_PER_METER;

        if (is3DMode) {
            GradientPaint gp = new GradientPaint(0, 0, new Color(20, 30, 45), 0, getHeight(), new Color(60, 70, 90));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            int ox = getWidth() / 2;
            int oy = getHeight() / 4;
            
            drawIsoFloor(g2d, ox, oy, rw, rl);

            items.sort((a, b) -> Integer.compare(a.x + a.y, b.x + b.y));

            for (FurnitureItem item : items) {
                item.draw3D(g2d, ox, oy, applyShadows);
            }
        } else {
            setBackground(new Color(235, 240, 245));
            g2d.setColor(wallColor);
            g2d.fillRect(50, 50, rw, rl);
            
            g2d.setColor(new Color(200, 200, 200, 150));
            for(int i = 0; i <= rw; i += PIXELS_PER_METER) {
                g2d.drawLine(50 + i, 50, 50 + i, 50 + rl);
            }
            for(int i = 0; i <= rl; i += PIXELS_PER_METER) {
                g2d.drawLine(50, 50 + i, 50 + rw, 50 + i);
            }
            
            g2d.setColor(Color.DARK_GRAY);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(50, 50, rw, rl);
            g2d.setStroke(new BasicStroke(1));
            
            for (FurnitureItem item : items) {
                item.draw2D(g2d, applyShadows);
            }
        }
    }
    
    private void drawIsoFloor(Graphics2D g2d, int ox, int oy, int w, int d) {
        int[] xs = new int[4], ys = new int[4];
        Point p1 = getIsoPoint(0, 0, 0, ox, oy);
        Point p2 = getIsoPoint(w, 0, 0, ox, oy);
        Point p3 = getIsoPoint(w, d, 0, ox, oy);
        Point p4 = getIsoPoint(0, d, 0, ox, oy);
        xs[0] = p1.x; ys[0] = p1.y; xs[1] = p2.x; ys[1] = p2.y; xs[2] = p3.x; ys[2] = p3.y; xs[3] = p4.x; ys[3] = p4.y;
        
        g2d.setColor(wallColor); 
        g2d.fillPolygon(xs, ys, 4);
        g2d.setColor(new Color(255, 255, 255, 100)); 
        g2d.drawPolygon(xs, ys, 4);
        
        g2d.setColor(new Color(200, 200, 200, 50));
        for(int i=0; i<=w; i+=PIXELS_PER_METER) {
            Point start = getIsoPoint(i, 0, 0, ox, oy);
            Point end = getIsoPoint(i, d, 0, ox, oy);
            g2d.drawLine(start.x, start.y, end.x, end.y);
        }
        for(int i=0; i<=d; i+=PIXELS_PER_METER) {
            Point start = getIsoPoint(0, i, 0, ox, oy);
            Point end = getIsoPoint(w, i, 0, ox, oy);
            g2d.drawLine(start.x, start.y, end.x, end.y);
        }
    }

    private Point getIsoPoint(int px, int py, int pz, int offsetX, int offsetY) {
        int isoX = offsetX + (int)((px - py) * 0.866);
        int isoY = offsetY + (px + py) / 2 - pz;
        return new Point(isoX, isoY);
    }
}

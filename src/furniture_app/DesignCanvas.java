package furniture_app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class DesignCanvas extends JPanel {
    private ArrayList<FurnitureItem> items = new ArrayList<>();
    private FurnitureItem selectedItem = null;
    private int offsetX, offsetY;
    private Color wallColor = new Color(245, 245, 250);
    private boolean applyShadows = true;
    private boolean is3DMode = false;
    private boolean isWireframeMode = false;
    private double viewRotation = 0;
    
    private int resizingHandle = 0;
    private int resizeStartW, resizeStartH, resizeStartX, resizeStartY, mouseStartX, mouseStartY;
    private double originalDragRotation;
    
    private Stack<byte[]> undoStack = new Stack<>();
    private Stack<byte[]> redoStack = new Stack<>();
    
    public int roomWidthMeters = 10;
    public int roomLengthMeters = 10;
    private final int PIXELS_PER_METER = 50; 

    public DesignCanvas() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder());
        setFocusable(true);
        requestFocusInWindow();

        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteItem");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "deleteItem");
        am.put("deleteItem", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedItem != null) {
                    saveStateToUndo();
                    deleteSelected();
                }
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undo");
        am.put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "redo");
        am.put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redo();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (is3DMode) {
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        viewRotation = (viewRotation + 5) % 360;
                        repaint();
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        viewRotation = (viewRotation - 5 + 360) % 360;
                        repaint();
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                int mx = e.getX();
                int my = e.getY();
                
                if (is3DMode) {
                    int ox = getWidth() / 2;
                    int oy = getHeight() / 4;
                    double A = (mx - ox) / 0.866;
                    double B = (my - oy) * 2.0;
                    double camX = (A + B) / 2.0;
                    double camY = (B - A) / 2.0;
                    
                    double rad = Math.toRadians(-viewRotation);
                    double cosV = Math.cos(rad);
                    double sinV = Math.sin(rad);
                    double rwx = camX * cosV - camY * sinV;
                    double rwy = camX * sinV + camY * cosV;
                    
                    double roomCx = (roomWidthMeters * PIXELS_PER_METER) / 2.0;
                    double roomCy = (roomLengthMeters * PIXELS_PER_METER) / 2.0;
                    mx = (int)(rwx + roomCx);
                    my = (int)(rwy + roomCy);
                }

                if (!is3DMode && selectedItem != null) {
                    resizingHandle = selectedItem.getResizeHandle(mx, my);
                    if (resizingHandle != 0) {
                        saveStateToUndo();
                        resizeStartW = selectedItem.width;
                        resizeStartH = selectedItem.height;
                        resizeStartX = selectedItem.x;
                        resizeStartY = selectedItem.y;
                        mouseStartX = mx;
                        mouseStartY = my;
                        originalDragRotation = selectedItem.rotation;
                        return;
                    }
                }

                for (int i = items.size() - 1; i >= 0; i--) {
                    FurnitureItem item = items.get(i);
                    // For tall 3D items, we might need a looser hit test, but footprint mapping works for the base
                    if (item.contains(mx, my)) {
                        if (selectedItem != null) selectedItem.isSelected = false;
                        saveStateToUndo();
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
            public void mouseMoved(MouseEvent e) {
                if (is3DMode) return;
                boolean needRepaint = false;
                boolean foundHover = false;
                for (int i = items.size() - 1; i >= 0; i--) {
                    FurnitureItem item = items.get(i);
                    boolean hover = !foundHover && item.contains(e.getX(), e.getY());
                    if (hover) foundHover = true;
                    if (item.isHovered != hover) {
                        item.isHovered = hover;
                        needRepaint = true;
                    }
                }
                if (needRepaint) repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedItem != null) {
                    if (resizingHandle != 0) {
                        double rad = Math.toRadians(-originalDragRotation);
                        double cos = Math.cos(rad);
                        double sin = Math.sin(rad);
                        double localDx = (e.getX() - mouseStartX) * cos - (e.getY() - mouseStartY) * sin;
                        double localDy = (e.getX() - mouseStartX) * sin + (e.getY() - mouseStartY) * cos;
                        
                        int newW = resizeStartW;
                        int newH = resizeStartH;
                        if (resizingHandle == 3) { newW += localDx * 2; newH += localDy * 2; } 
                        else if (resizingHandle == 4) { newW -= localDx * 2; newH += localDy * 2; } 
                        else if (resizingHandle == 2) { newW += localDx * 2; newH -= localDy * 2; } 
                        else if (resizingHandle == 1) { newW -= localDx * 2; newH -= localDy * 2; }
                        
                        if (newW < 20) newW = 20;
                        if (newH < 20) newH = 20;

                        double cx = resizeStartX + resizeStartW / 2.0;
                        double cy = resizeStartY + resizeStartH / 2.0;
                        int nx = (int)(cx - newW / 2.0);
                        int ny = (int)(cy - newH / 2.0);
                        
                        // Bounds checking for resizing
                        int rw = roomWidthMeters * PIXELS_PER_METER;
                        int rl = roomLengthMeters * PIXELS_PER_METER;
                        if (nx < 50) { newW -= (50 - nx); nx = 50; }
                        if (ny < 50) { newH -= (50 - ny); ny = 50; }
                        if (nx + newW > 50 + rw) { newW = 50 + rw - nx; }
                        if (ny + newH > 50 + rl) { newH = 50 + rl - ny; }

                        selectedItem.width = newW;
                        selectedItem.height = newH;
                        selectedItem.x = nx;
                        selectedItem.y = ny;
                        repaint();
                        return;
                    }

                    int mx = e.getX();
                    int my = e.getY();
                    
                    if (is3DMode) {
                        int ox = getWidth() / 2;
                        int oy = getHeight() / 4;
                        double A = (mx - ox) / 0.866;
                        double B = (my - oy) * 2.0;
                        double camX = (A + B) / 2.0;
                        double camY = (B - A) / 2.0;
                        
                        double rad = Math.toRadians(-viewRotation);
                        double cosV = Math.cos(rad);
                        double sinV = Math.sin(rad);
                        double rwx = camX * cosV - camY * sinV;
                        double rwy = camX * sinV + camY * cosV;
                        
                        double roomCx = (roomWidthMeters * PIXELS_PER_METER) / 2.0;
                        double roomCy = (roomLengthMeters * PIXELS_PER_METER) / 2.0;
                        mx = (int)(rwx + roomCx);
                        my = (int)(rwy + roomCy);
                    }

                    selectedItem.x = mx - offsetX;
                    selectedItem.y = my - offsetY;
                    
                    if (!is3DMode) {
                        int rw = roomWidthMeters * PIXELS_PER_METER;
                        int rl = roomLengthMeters * PIXELS_PER_METER;
                        int rightBound = 50 + rw - selectedItem.width;
                        int bottomBound = 50 + rl - selectedItem.height;
                        
                        if (selectedItem.x < 50) selectedItem.x = 50;
                        if (selectedItem.y < 50) selectedItem.y = 50;
                        if (selectedItem.x > rightBound) selectedItem.x = rightBound;
                        if (selectedItem.y > bottomBound) selectedItem.y = bottomBound;
                    }

                    repaint();
                }
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                resizingHandle = 0;
            }
        });
    }

    public void addFurniture(String type) {
        saveStateToUndo();
        // default items are placed at x:60, y:60 within bounds
        int startX = 60, startY = 60;
        if ("Dining Table".equals(type)) items.add(new FurnitureItem(type, startX, startY, 120, 80, new Color(139, 69, 19)));
        else if ("Chair".equals(type)) items.add(new FurnitureItem(type, startX, startY, 45, 45, new Color(70, 130, 180)));
        else if ("Modern Sofa".equals(type)) items.add(new FurnitureItem(type, startX, startY, 160, 70, new Color(105, 105, 105)));
        else if ("Bed".equals(type)) items.add(new FurnitureItem(type, startX, startY, 140, 180, new Color(160, 82, 45)));
        else if ("Cabinet".equals(type)) items.add(new FurnitureItem(type, startX, startY, 120, 40, new Color(205, 133, 63)));
        else if ("Plant".equals(type)) items.add(new FurnitureItem(type, startX, startY, 40, 40, new Color(34, 139, 34)));
        repaint();
    }

    public void deleteSelected() {
        if (selectedItem != null) {
            items.remove(selectedItem);
            selectedItem = null;
            repaint();
        }
    }

    public void clearDesign() {
        saveStateToUndo();
        items.clear();
        selectedItem = null;
        repaint();
    }

    public void saveDesign(File file) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(items);
            JOptionPane.showMessageDialog(this, "Design Saved Successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error Saving: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveDesignToDB(String designName) {
        DBConnection.saveDesignToDatabase(designName, this);
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

    public void loadDesignFromDB(String designName) {
        boolean success = DBConnection.loadDesignFromDatabase(designName, this);
        if (success) {
            saveStateToUndo();
        }
    }
    
    public ArrayList<FurnitureItem> getItems() { return items; }
    public void setItems(ArrayList<FurnitureItem> items) { this.items = items; }
    public Color getWallColor() { return wallColor; }
    public boolean isApplyShadows() { return applyShadows; }
    
    public void setWireframeMode(boolean wireframe) {
        this.isWireframeMode = wireframe;
        repaint();
    }
    public boolean isWireframeMode() { return isWireframeMode; }

    public void saveStateToUndo() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(items);
            oos.close();
            undoStack.push(baos.toByteArray());
            redoStack.clear();
        } catch(Exception e) {}
    }

    @SuppressWarnings("unchecked")
    public void undo() {
        if (!undoStack.isEmpty()) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(items);
                oos.close();
                redoStack.push(baos.toByteArray());
                
                byte[] state = undoStack.pop();
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(state));
                items = (ArrayList<FurnitureItem>) ois.readObject();
                selectedItem = null;
                repaint();
            } catch(Exception e) {}
        }
    }
    
    @SuppressWarnings("unchecked")
    public void redo() {
        if (!redoStack.isEmpty()) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(items);
                oos.close();
                undoStack.push(baos.toByteArray());
                
                byte[] state = redoStack.pop();
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(state));
                items = (ArrayList<FurnitureItem>) ois.readObject();
                selectedItem = null;
                repaint();
            } catch(Exception e) {}
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
        double roomCx = rw / 2.0;
        double roomCy = rl / 2.0;

        if (is3DMode) {
            GradientPaint gp = new GradientPaint(0, 0, new Color(20, 30, 45), 0, getHeight(), new Color(60, 70, 90));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            int ox = getWidth() / 2;
            int oy = getHeight() / 4;
            
            ArrayList<FurnitureItem.Face3D> allFaces = new ArrayList<>();
            
            double radView = Math.toRadians(viewRotation);
            double cosV = Math.cos(radView);
            double sinV = Math.sin(radView);
            
            double distance = 1000;
            double height = 700;
            double focal = 1200;
            double L = Math.sqrt(distance*distance + height*height);
            double upY = height / L;
            double upZ = -distance / L;
            double fwdY = distance / L;
            double fwdZ = -height / L;
            
            double[][] floorV = {
                {0, 0, 0}, {rw, 0, 0}, {rw, rl, 0}, {0, rl, 0}
            };
            FurnitureItem.Face3D floorFace = new FurnitureItem.Face3D();
            floorFace.color = wallColor;
            for(int j=0; j<4; j++) {
                double rwx = floorV[j][0] - roomCx;
                double rwy = floorV[j][1] - roomCy;
                double camX = rwx * cosV - rwy * sinV;
                double camY = rwx * sinV + rwy * cosV;
                floorFace.camX[j] = camX;
                floorFace.camY[j] = camY;
                floorFace.camZ[j] = 0;
                
                double viewX = camX;
                double viewY = (camY + distance) * upY + (0 - height) * upZ;
                double viewZ = (camY + distance) * fwdY + (0 - height) * fwdZ;
                
                if (viewZ > 1) {
                    floorFace.isoX[j] = ox + (int)(focal * viewX / viewZ);
                    floorFace.isoY[j] = oy - (int)(focal * viewY / viewZ);
                }
            }
            floorFace.depth = -999999.0;
            floorFace.isFloor = true;
            allFaces.add(floorFace);

            for (FurnitureItem item : items) {
                allFaces.addAll(item.get3DFaces(ox, oy, roomCx, roomCy, viewRotation));
            }
            
            // Drop shadows
            if (applyShadows) {
                ArrayList<FurnitureItem.Face3D> shadows = new ArrayList<>();
                double lx = -0.5, ly = -0.7, lz = 1.0; 
                for (FurnitureItem.Face3D f : allFaces) {
                    if (f.isFloor) continue;
                    FurnitureItem.Face3D shadow = new FurnitureItem.Face3D();
                    shadow.color = new Color(0, 0, 0, 50);
                    shadow.depth = -999998.0; 
                    double avgDepth = 0;
                    for (int j = 0; j < 4; j++) {
                        double sx = f.camX[j] - f.camZ[j] * (lx / lz);
                        double sy = f.camY[j] - f.camZ[j] * (ly / lz);
                        double sz = 0;
                        
                        double viewX = sx;
                        double viewY = (sy + distance) * upY + (sz - height) * upZ;
                        double viewZ = (sy + distance) * fwdY + (sz - height) * fwdZ;
                        
                        if (viewZ > 1) {
                            shadow.isoX[j] = ox + (int)(focal * viewX / viewZ);
                            shadow.isoY[j] = oy - (int)(focal * viewY / viewZ);
                        } else {
                            shadow.isoX[j] = ox; // Default to origin if behind camera
                            shadow.isoY[j] = oy;
                        }
                        avgDepth += viewZ;
                    }
                    shadow.depth = avgDepth / 4.0;
                    shadows.add(shadow);
                }
                allFaces.addAll(shadows);
            }
            
            Collections.sort(allFaces);
            
            // Pre-generate textures
            BufferedImage woodTex = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
            Graphics2D wg = woodTex.createGraphics();
            wg.setColor(new Color(180, 100, 40));
            wg.fillRect(0, 0, 128, 128);
            wg.setColor(new Color(130, 70, 20));
            for(int k=0; k<40; k++) {
                wg.drawLine(0, (int)(Math.random()*128), 128, (int)(Math.random()*128));
            }
            wg.dispose();
            TexturePaint woodPaint = new TexturePaint(woodTex, new Rectangle(0, 0, 128, 128));
            
            BufferedImage floorTex = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
            Graphics2D fg = floorTex.createGraphics();
            fg.setColor(wallColor);
            fg.fillRect(0, 0, 64, 64);
            fg.setColor(wallColor.darker());
            fg.drawRect(0, 0, 32, 32);
            fg.drawRect(32, 32, 32, 32);
            fg.dispose();
            TexturePaint floorPaint = new TexturePaint(floorTex, new Rectangle(0, 0, 64, 64));

            for (FurnitureItem.Face3D f : allFaces) {
                double cur = (f.isoX[1] - f.isoX[0]) * (f.isoY[2] - f.isoY[1]) - (f.isoY[1] - f.isoY[0]) * (f.isoX[2] - f.isoX[1]);
                if (cur >= 0 || f.color == wallColor || f.depth < -999990.0) { 
                    Color shadeC = f.color;
                    
                    if (f.depth > -999990.0 && f.color != wallColor) {
                        double v1x = f.camX[1] - f.camX[0];
                        double v1y = f.camY[1] - f.camY[0];
                        double v1z = f.camZ[1] - f.camZ[0];
                        double v2x = f.camX[2] - f.camX[0];
                        double v2y = f.camY[2] - f.camY[0];
                        double v2z = f.camZ[2] - f.camZ[0];

                        double nx = v1y * v2z - v1z * v2y;
                        double ny = v1z * v2x - v1x * v2z;
                        double nz = v1x * v2y - v1y * v2x;

                        double len = Math.sqrt(nx*nx + ny*ny + nz*nz);
                        if (len > 0) { nx /= len; ny /= len; nz /= len; }

                        // Phong Shading Model
                        double lx = -0.5, ly = -0.7, lz = 1.0; 
                        double llen = Math.sqrt(lx*lx + ly*ly + lz*lz);
                        lx /= llen; ly /= llen; lz /= llen;

                        double dot = nx * lx + ny * ly + nz * lz; 
                        if (dot < 0) dot = 0;

                        // Camera vector (view space roughly z)
                        double valX = 0, valY = 1000, valZ = 700;
                        double vlen = Math.sqrt(valX*valX + valY*valY + valZ*valZ);
                        valX/=vlen; valY/=vlen; valZ/=vlen;

                        double rx = 2 * dot * nx - lx;
                        double ry = 2 * dot * ny - ly;
                        double rz = 2 * dot * nz - lz;
                        double rlen = Math.sqrt(rx*rx+ry*ry+rz*rz);
                        if(rlen>0){ rx/=rlen; ry/=rlen; rz/=rlen; }

                        double specDot = rx*-valX + ry*-valY + rz*valZ;
                        if(specDot < 0) specDot = 0;
                        
                        double specular = Math.pow(specDot, 12) * 0.4;
                        double ambient = 0.3;
                        double diffuse = 0.6 * dot;
                        double intensity = ambient + diffuse;

                        int r = (int)(f.color.getRed() * intensity + specular * 255);
                        int gCol = (int)(f.color.getGreen() * intensity + specular * 255);
                        int bCol = (int)(f.color.getBlue() * intensity + specular * 255);
                        r = Math.min(255, Math.max(0, r));
                        gCol = Math.min(255, Math.max(0, gCol));
                        bCol = Math.min(255, Math.max(0, bCol));

                        shadeC = new Color(r, gCol, bCol, f.color.getAlpha());
                        
                        if (f.isSelected) {
                            shadeC = new Color((r+255)/2, (gCol+100)/2, (bCol+100)/2, f.color.getAlpha());
                        }
                    }
                    
                    if(isWireframeMode) {
                        g2d.setColor(new Color(0, 255, 255, 180)); 
                        g2d.setStroke(new BasicStroke(2));
                        g2d.drawPolygon(f.isoX, f.isoY, 4);
                        g2d.setStroke(new BasicStroke(1));
                    } else {
                        // Apply Texture Paint
                        if (f.isFloor && f.color == wallColor) {
                            g2d.setPaint(floorPaint);
                        } else if (f.depth > -999990.0) {
                            g2d.setPaint(woodPaint);
                        } else {
                            g2d.setColor(shadeC);
                        }
                        
                        g2d.fillPolygon(f.isoX, f.isoY, 4);
                        
                        // Overlay Shading onto Texture
                        if (f.depth > -999990.0 || f.isFloor) {
                            g2d.setColor(new Color(shadeC.getRed(), shadeC.getGreen(), shadeC.getBlue(), 180));
                            g2d.fillPolygon(f.isoX, f.isoY, 4);
                        }
                    }
                }
            }

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2d.drawString("\u2190 \u2192 Use Left/Right Arrows to rotate 3D view smoothly", 20, 30);
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
}

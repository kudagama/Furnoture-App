package furniture_app;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class FurnitureItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public int x, y, width, height;
    public int originalWidth, originalHeight;
    public String type;
    public Color color;
    public Color secondaryColor;
    public boolean isSelected = false;
    public boolean isHovered = false;
    public double rotation = 0; 
    
    public FurnitureItem(String type, int x, int y, int width, int height, Color color) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.originalWidth = width;
        this.originalHeight = height;
        this.color = color;
        this.secondaryColor = color.brighter();
    }

    public void draw2D(Graphics2D g2d, boolean applyShadows) {
        AffineTransform oldTx = g2d.getTransform();
        g2d.rotate(Math.toRadians(rotation), x + width / 2.0, y + height / 2.0);

        if (applyShadows) {
            g2d.setColor(new Color(0, 0, 0, 60));
            g2d.fillRoundRect(x + 10, y + 10, width, height, 15, 15);
        }

        GradientPaint gp = new GradientPaint(x, y, color.brighter(), x + width, y + height, color.darker());
        g2d.setPaint(gp);
        
        if ("Chair".equals(type)) {
            g2d.fillRoundRect(x, y, width, height, 15, 15);
            g2d.setColor(color.darker().darker());
            g2d.drawRoundRect(x, y, width, height, 15, 15);
            // Add backrest detail
            GradientPaint gpBack = new GradientPaint(x, y, color, x, y + 15, color.darker().darker());
            g2d.setPaint(gpBack);
            g2d.fillRoundRect(x + 5, y + 5, width - 10, 15, 10, 10);
            g2d.setColor(new Color(255, 255, 255, 50));
            g2d.drawRoundRect(x + 6, y + 6, width - 12, 13, 8, 8);
        } else if ("Plant".equals(type)) {
            GradientPaint gpPlant = new GradientPaint(x, y, color.brighter(), x + width, y + height, color.darker().darker());
            g2d.setPaint(gpPlant);
            g2d.fillOval(x, y, width, height);
            g2d.setColor(color.darker().darker());
            g2d.drawOval(x, y, width, height);
            // Inner leaves
            g2d.setColor(color.brighter());
            g2d.fillOval(x + width/4, y + height/4, width/2, height/2);
            for(int i=0; i<360; i+=45) {
                g2d.fillArc(x + 5, y + 5, width - 10, height - 10, i, 20);
            }
        } else if ("Modern Sofa".equals(type)) {
            g2d.fillRoundRect(x, y, width, height, 10, 10);
            g2d.setColor(color.darker().darker());
            g2d.drawRoundRect(x, y, width, height, 10, 10);
            // Cushions
            GradientPaint cushionGp = new GradientPaint(x + 15, y + 15, color, x + width - 15, y + height - 15, color.darker());
            g2d.setPaint(cushionGp);
            g2d.fillRoundRect(x + 15, y + 15, width - 30, height - 25, 5, 5);
            // Split cushions
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.drawLine(x + width/2, y + 15, x + width/2, y + height - 10);
        } else if ("Bed".equals(type)) {
            g2d.fillRoundRect(x, y, width, height, 8, 8);
            g2d.setColor(color.darker());
            g2d.drawRoundRect(x, y, width, height, 8, 8);
            // Blanket
            GradientPaint blanketGp = new GradientPaint(x + 5, y + height/3, Color.WHITE, x + width - 5, y + height - 5, new Color(220, 220, 230));
            g2d.setPaint(blanketGp);
            g2d.fillRoundRect(x + 5, y + height/4, width - 10, (height*3)/4 - 10, 5, 5);
            // Fold in blanket
            g2d.setColor(new Color(200, 200, 210));
            g2d.fillRoundRect(x + 5, y + height/4, width - 10, 15, 5, 5);
            // Pillows
            GradientPaint pillowGp = new GradientPaint(x, y, Color.WHITE, x, y + 20, new Color(230, 230, 230));
            g2d.setPaint(pillowGp);
            g2d.fillRoundRect(x + 15, y + 10, width/2 - 20, height/5, 8, 8);
            g2d.fillRoundRect(x + width/2 + 5, y + 10, width/2 - 20, height/5, 8, 8);
            g2d.setColor(new Color(0, 0, 0, 30));
            g2d.drawRoundRect(x + 15, y + 10, width/2 - 20, height/5, 8, 8);
            g2d.drawRoundRect(x + width/2 + 5, y + 10, width/2 - 20, height/5, 8, 8);
        } else if ("Dining Table".equals(type)) {
            g2d.fillRoundRect(x, y, width, height, 15, 15);
            g2d.setColor(color.darker().darker());
            g2d.drawRoundRect(x, y, width, height, 15, 15);
            // Inner wood grain or reflection
            GradientPaint woodGp = new GradientPaint(x, y, new Color(255, 255, 255, 40), x + width, y + height, new Color(0, 0, 0, 40));
            g2d.setPaint(woodGp);
            g2d.fillRoundRect(x + 8, y + 8, width - 16, height - 16, 10, 10);
            g2d.setColor(new Color(255, 255, 255, 60));
            g2d.drawLine(x + 15, y + 15, x + width - 15, y + 15);
        } else if ("Cabinet".equals(type)) {
            g2d.fillRoundRect(x, y, width, height, 5, 5);
            g2d.setColor(color.darker().darker());
            g2d.drawRoundRect(x, y, width, height, 5, 5);
            g2d.setColor(new Color(0, 0, 0, 80));
            g2d.drawLine(x + width/2, y + 5, x + width/2, y + height - 5);
            // Knobs
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillOval(x + width/2 - 8, y + height/2 - 4, 6, 8);
            g2d.fillOval(x + width/2 + 2, y + height/2 - 4, 6, 8);
        } else {
            g2d.fillRoundRect(x, y, width, height, 5, 5);
            g2d.setColor(color.darker());
            g2d.drawRoundRect(x, y, width, height, 5, 5);
        }
        
        if (isHovered && !isSelected) {
            Stroke oldStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(new Color(50, 150, 255, 180)); 
            g2d.drawRect(x - 2, y - 2, width + 4, height + 4);
            g2d.setStroke(oldStroke);
        }
        
        if (isSelected) {
            Stroke oldStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
            g2d.setColor(new Color(255, 69, 0)); 
            g2d.drawRect(x - 3, y - 3, width + 6, height + 6);
            
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(1));
            int rS = 8;
            g2d.fillRect(x - 4, y - 4, rS, rS);
            g2d.fillRect(x + width - 4, y - 4, rS, rS);
            g2d.fillRect(x + width - 4, y + height - 4, rS, rS);
            g2d.fillRect(x - 4, y + height - 4, rS, rS);
            g2d.setColor(Color.RED);
            g2d.drawRect(x - 4, y - 4, rS, rS);
            g2d.drawRect(x + width - 4, y - 4, rS, rS);
            g2d.drawRect(x + width - 4, y + height - 4, rS, rS);
            g2d.drawRect(x - 4, y + height - 4, rS, rS);

            g2d.setStroke(oldStroke);
            
            g2d.setColor(Color.BLUE);
            g2d.fillOval(x + width/2 - 4, y - 15, 8, 8);
            g2d.drawLine(x + width/2, y - 11, x + width/2, y);
        }
        g2d.setTransform(oldTx);
    }
    
    public int getResizeHandle(int px, int py) {
        if (!isSelected) return 0;
        double cx = x + width / 2.0;
        double cy = y + height / 2.0;
        double dx = px - cx;
        double dy = py - cy;
        double rad = Math.toRadians(-rotation);
        double rx = dx * Math.cos(rad) - dy * Math.sin(rad);
        double ry = dx * Math.sin(rad) + dy * Math.cos(rad);
        
        int rSize = 10;
        double hw = width / 2.0;
        double hh = height / 2.0;
        if (rx >= -hw - rSize && rx <= -hw + rSize && ry >= -hh - rSize && ry <= -hh + rSize) return 1; 
        if (rx >= hw - rSize && rx <= hw + rSize && ry >= -hh - rSize && ry <= -hh + rSize) return 2; 
        if (rx >= hw - rSize && rx <= hw + rSize && ry >= hh - rSize && ry <= hh + rSize) return 3; 
        if (rx >= -hw - rSize && rx <= -hw + rSize && ry >= hh - rSize && ry <= hh + rSize) return 4; 
        return 0;
    }

    public static class Face3D implements Comparable<Face3D> {
        public double[] camX = new double[4];
        public double[] camY = new double[4];
        public double[] camZ = new double[4];
        public int[] isoX = new int[4];
        public int[] isoY = new int[4];
        public Color color;
        public double depth;
        public boolean isSelected;
        public boolean isFloor = false;

        @Override
        public int compareTo(Face3D o) {
            return Double.compare(this.depth, o.depth);
        }
    }

    private class Box3D {
        double px, py, pz, w, d, h;
        Color c;
        public Box3D(double px, double py, double pz, double w, double d, double h, Color c) {
            this.px = px; this.py = py; this.pz = pz;
            this.w = w; this.d = d; this.h = h; this.c = c;
        }
    }

    public ArrayList<Face3D> get3DFaces(int offsetX, int offsetY, double roomCx, double roomCy, double viewRot) {
        ArrayList<Box3D> parts = new ArrayList<>();
        buildParts(parts, width, height);

        ArrayList<Face3D> faces = new ArrayList<>();
        double cx = x + width / 2.0;
        double cy = y + height / 2.0;
        
        double radItem = Math.toRadians(rotation);
        double cosI = Math.cos(radItem);
        double sinI = Math.sin(radItem);
        
        double radView = Math.toRadians(viewRot);
        double cosV = Math.cos(radView);
        double sinV = Math.sin(radView);

        for (Box3D b : parts) {
            double[][] v = {
                {b.px, b.py, b.pz},
                {b.px + b.w, b.py, b.pz},
                {b.px + b.w, b.py + b.d, b.pz},
                {b.px, b.py + b.d, b.pz},
                {b.px, b.py, b.pz + b.h},
                {b.px + b.w, b.py, b.pz + b.h},
                {b.px + b.w, b.py + b.d, b.pz + b.h},
                {b.px, b.py + b.d, b.pz + b.h}
            };
            
            int[][] fIdx = {
                {0, 3, 2, 1}, // Bottom
                {4, 5, 6, 7}, // Top
                {0, 1, 5, 4}, // Front
                {1, 2, 6, 5}, // Right
                {2, 3, 7, 6}, // Back
                {3, 0, 4, 7}  // Left
            };
            
            for (int i = 0; i < 6; i++) {
                Face3D face = new Face3D();
                face.color = b.c;
                face.isSelected = this.isSelected;
                double avgDepth = 0;
                for (int j = 0; j < 4; j++) {
                    double vx = v[fIdx[i][j]][0];
                    double vy = v[fIdx[i][j]][1];
                    double vz = v[fIdx[i][j]][2];
                    
                    double rlx = vx * cosI - vy * sinI;
                    double rly = vx * sinI + vy * cosI;
                    
                    double wx = rlx + cx;
                    double wy = rly + cy;
                    
                    double rwx = wx - roomCx;
                    double rwy = wy - roomCy;
                    double camX = rwx * cosV - rwy * sinV;
                    double camY = rwx * sinV + rwy * cosV;
                    
                    face.camX[j] = camX;
                    face.camY[j] = camY;
                    face.camZ[j] = vz;
                    
                    face.isoX[j] = offsetX + (int)((camX - camY) * 0.866);
                    face.isoY[j] = offsetY + (int)((camX + camY) * 0.5 - vz);
                    
                    avgDepth += (camX + camY + (vz * 2.0)); 
                }
                face.depth = avgDepth / 4.0;
                faces.add(face);
            }
        }
        return faces;
    }

    private void buildParts(ArrayList<Box3D> parts, double w, double d) {
        double hw = w / 2.0;
        double hd = d / 2.0;

        switch (type) {
            case "Dining Table":
                double legW = Math.min(6, Math.min(w, d) * 0.1);
                double tableH = 35;
                Color legCol = color;
                parts.add(new Box3D(-hw + 2, -hd + 2, 0, legW, legW, tableH, legCol));
                parts.add(new Box3D(hw - legW - 2, -hd + 2, 0, legW, legW, tableH, legCol));
                parts.add(new Box3D(-hw + 2, hd - legW - 2, 0, legW, legW, tableH, legCol));
                parts.add(new Box3D(hw - legW - 2, hd - legW - 2, 0, legW, legW, tableH, legCol));
                // Add a thicker top for realism
                parts.add(new Box3D(-hw - 4, -hd - 4, tableH - 2, w + 8, d + 8, 6, secondaryColor)); 
                // Add an elegant inner top layer
                parts.add(new Box3D(-hw, -hd, tableH + 4, w, d, 2, secondaryColor.brighter()));
                break;

            case "Chair":
                double seatH = 22;
                double backH = 48;
                double cLeg = Math.min(4, Math.min(w, d) * 0.1);
                Color pColor = secondaryColor;
                parts.add(new Box3D(-hw + 1, -hd + 1, 0, cLeg, cLeg, seatH, pColor));
                parts.add(new Box3D(hw - cLeg - 1, -hd + 1, 0, cLeg, cLeg, seatH, pColor));
                parts.add(new Box3D(-hw + 1, hd - cLeg - 1, 0, cLeg, cLeg, seatH, pColor));
                parts.add(new Box3D(hw - cLeg - 1, hd - cLeg - 1, 0, cLeg, cLeg, seatH, pColor));
                // Seat with a bit of cushion height
                parts.add(new Box3D(-hw - 2, -hd - 2, seatH, w + 4, d + 4, 3, color.darker())); 
                parts.add(new Box3D(-hw - 1, -hd - 1, seatH + 3, w + 2, d + 2, 4, color)); 
                // Backrest with some curve
                parts.add(new Box3D(-hw + 1, -hd - 1, seatH + 6, w - 2, 6, backH - seatH - 6, color)); 
                break;

            case "Bed":
                double frameH = 10;
                double mattH = 14;
                double headH = 35;
                parts.add(new Box3D(-hw, -hd, 0, w, d, frameH, secondaryColor)); // Frame
                parts.add(new Box3D(-hw, -hd, 0, w, 6, headH, secondaryColor)); // Headboard
                parts.add(new Box3D(-hw + 3, -hd + 6, frameH, w - 6, d - 9, mattH, Color.WHITE)); // Mattress
                parts.add(new Box3D(-hw + 3, hd - (d*0.6), frameH + 1, w - 6, d*0.6, mattH + 2, color)); // Blanket
                // Two Pillows
                parts.add(new Box3D(-hw + 10, -hd + 12, frameH + mattH, (w/2) - 15, Math.max(12, d*0.15), 6, Color.WHITE)); 
                parts.add(new Box3D(hw - 10 - ((w/2)-15), -hd + 12, frameH + mattH, (w/2) - 15, Math.max(12, d*0.15), 6, Color.WHITE)); 
                break;

            case "Modern Sofa":
                double sSeatH = 15;
                double sArmH = 30;
                double sBackH = 42;
                double armT = Math.max(10, w * 0.15);
                Color baseCol = Color.DARK_GRAY;
                // Base
                parts.add(new Box3D(-hw, -hd, 0, w, d, 4, baseCol));
                // Left arm
                parts.add(new Box3D(-hw, -hd, 4, armT, d, sArmH - 4, color.darker())); 
                // Right arm
                parts.add(new Box3D(hw - armT, -hd, 4, armT, d, sArmH - 4, color.darker())); 
                // Back rest
                parts.add(new Box3D(-hw + armT, -hd, 4, w - armT*2, 12, sBackH - 4, color.darker())); 
                // Seat Cushion 1
                parts.add(new Box3D(-hw + armT + 1, -hd + 12, 4, (w - armT*2)/2 - 1, d - 12, sSeatH + 4, color)); 
                // Seat Cushion 2
                parts.add(new Box3D(0, -hd + 12, 4, (w - armT*2)/2 - 1, d - 12, sSeatH + 4, color)); 
                break;

            case "Cabinet":
                double cabH = 80;
                parts.add(new Box3D(-hw, -hd, 0, w, d, cabH, color));
                // Add doors division
                parts.add(new Box3D(-0.5, hd, 4, 1, 1, cabH - 8, secondaryColor.darker()));
                // Handles
                parts.add(new Box3D(-w*0.15, hd + 1, cabH*0.6, 3, 2, 15, Color.LIGHT_GRAY));
                parts.add(new Box3D(w*0.15 - 3, hd + 1, cabH*0.6, 3, 2, 15, Color.LIGHT_GRAY));
                // Top rim
                parts.add(new Box3D(-hw - 1, -hd - 1, cabH, w + 2, d + 2, 3, secondaryColor));
                break;

            case "Plant":
                double potH = 22;
                parts.add(new Box3D(-hw + 8, -hd + 8, 0, w - 16, d - 16, potH, secondaryColor)); // Pot base
                parts.add(new Box3D(-hw + 6, -hd + 6, potH - 4, w - 12, d - 12, 5, secondaryColor.brighter())); // Pot rim
                Color leafC = color;
                parts.add(new Box3D(-hw + 4, -hd + 4, potH + 1, w - 8, d - 8, 10, leafC)); // Lower leaves
                parts.add(new Box3D(-hw + 8, -hd + 8, potH + 11, w - 16, d - 16, 12, leafC.brighter())); // Mid leaves
                parts.add(new Box3D(-2, -2, potH + 23, 4, 4, 8, leafC.brighter().brighter())); // Top leaves
                break;

            default:
                parts.add(new Box3D(-hw, -hd, 0, w, d, 40, color));
                break;
        }
    }

    private Color blend(Color c1, Color c2, float ratio) {
        float r = c1.getRed() * (1 - ratio) + c2.getRed() * ratio;
        float g = c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio;
        float b = c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio;
        return new Color((int)r, (int)g, (int)b, c1.getAlpha());
    }

    public boolean contains(int px, int py) {
        double cx = x + width / 2.0;
        double cy = y + height / 2.0;
        double dx = px - cx;
        double dy = py - cy;
        double rad = Math.toRadians(-rotation);
        double rx = dx * Math.cos(rad) - dy * Math.sin(rad);
        double ry = dx * Math.sin(rad) + dy * Math.cos(rad);
        return (rx >= -width/2.0 && rx <= width/2.0 && ry >= -height/2.0 && ry <= height/2.0);
    }
}

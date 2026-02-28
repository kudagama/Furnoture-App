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
    public boolean isSelected = false;
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
        
        if (isSelected) {
            Stroke oldStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
            g2d.setColor(new Color(255, 69, 0)); 
            g2d.drawRect(x - 3, y - 3, width + 6, height + 6);
            g2d.setStroke(oldStroke);
            
            g2d.setColor(Color.BLUE);
            g2d.fillOval(x + width/2 - 4, y - 15, 8, 8);
            g2d.drawLine(x + width/2, y - 11, x + width/2, y);
        }
        g2d.setTransform(oldTx);
    }

    private class Box3D implements Comparable<Box3D> {
        double px, py, pz, w, d, h;
        Color c;
        public Box3D(double px, double py, double pz, double w, double d, double h, Color c) {
            this.px = px; this.py = py; this.pz = pz;
            this.w = w; this.d = d; this.h = h; this.c = c;
        }
        @Override
        public int compareTo(Box3D o) {
            double m1 = (this.px + this.w) + (this.py + this.d) + (this.pz + this.h);
            double m2 = (o.px + o.w) + (o.py + o.d) + (o.pz + o.h);
            if (Math.abs(m1 - m2) > 0.01) {
                return Double.compare(m1, m2);
            }
            double min1 = this.px + this.py + this.pz;
            double min2 = o.px + o.py + o.pz;
            return Double.compare(min1, min2);
        }
    }

    public void draw3D(Graphics2D g2d, int offsetX, int offsetY, boolean applyShadows) {
        ArrayList<Box3D> parts = new ArrayList<>();
        double bw = width;
        double bd = height;

        buildParts(parts, bw, bd);

        // Snap rotation to nearest 90
        int rot = (int) Math.round(rotation / 90.0) * 90;
        rot = (rot % 360 + 360) % 360;

        ArrayList<Box3D> rotatedParts = new ArrayList<>();
        for (Box3D b : parts) {
            double nx = b.px, ny = b.py, nw = b.w, nd = b.d;
            if (rot == 90) {
                nx = -b.py - b.d;
                ny = b.px;
                nw = b.d;
                nd = b.w;
            } else if (rot == 180) {
                nx = -b.px - b.w;
                ny = -b.py - b.d;
            } else if (rot == 270) {
                nx = b.py;
                ny = -b.px - b.w;
                nw = b.d;
                nd = b.w;
            }
            rotatedParts.add(new Box3D(nx, ny, b.pz, nw, nd, b.h, b.c));
        }

        // Shift origin to exact center of the bounding box
        double cx = x + width / 2.0;
        double cy = y + height / 2.0;
        
        for (Box3D b : rotatedParts) {
            b.px += cx;
            b.py += cy;
        }

        // Apply 3D Painter's Algorithm topological sort!
        Collections.sort(rotatedParts);

        if (applyShadows) {
            double shW = (rot == 90 || rot == 270) ? bd : bw;
            double shD = (rot == 90 || rot == 270) ? bw : bd;
            drawIsoPolygon(g2d, offsetX, offsetY, cx - shW/2.0, cy - shD/2.0, 0, shW, shD, 0, new Color(0, 0, 0, 60), new Color(0, 0, 0, 10));
        }

        for (Box3D b : rotatedParts) {
            drawBoxFaces(g2d, offsetX, offsetY, b);
        }
    }

    private void buildParts(ArrayList<Box3D> parts, double w, double d) {
        double hw = w / 2.0;
        double hd = d / 2.0;

        switch (type) {
            case "Dining Table":
                double legW = Math.min(6, Math.min(w, d) * 0.1);
                double tableH = 35;
                Color legCol = new Color(60, 40, 25);
                parts.add(new Box3D(-hw + 2, -hd + 2, 0, legW, legW, tableH, legCol));
                parts.add(new Box3D(hw - legW - 2, -hd + 2, 0, legW, legW, tableH, legCol));
                parts.add(new Box3D(-hw + 2, hd - legW - 2, 0, legW, legW, tableH, legCol));
                parts.add(new Box3D(hw - legW - 2, hd - legW - 2, 0, legW, legW, tableH, legCol));
                // Add a thicker top for realism
                parts.add(new Box3D(-hw - 4, -hd - 4, tableH - 2, w + 8, d + 8, 6, color)); 
                // Add an elegant inner top layer
                parts.add(new Box3D(-hw, -hd, tableH + 4, w, d, 2, color.brighter()));
                break;

            case "Chair":
                double seatH = 22;
                double backH = 48;
                double cLeg = Math.min(4, Math.min(w, d) * 0.1);
                Color pColor = blend(color, Color.BLACK, 0.5f);
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
                parts.add(new Box3D(-hw, -hd, 0, w, d, frameH, color.darker().darker())); // Frame
                parts.add(new Box3D(-hw, -hd, 0, w, 6, headH, color)); // Headboard
                Color mattressCol = new Color(245, 245, 250);
                parts.add(new Box3D(-hw + 3, -hd + 6, frameH, w - 6, d - 9, mattH, mattressCol)); // Mattress
                parts.add(new Box3D(-hw + 3, hd - (d*0.6), frameH + 1, w - 6, d*0.6, mattH + 2, new Color(220, 220, 230))); // Blanket
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
                parts.add(new Box3D(-0.5, hd, 4, 1, 1, cabH - 8, color.darker().darker()));
                // Handles
                parts.add(new Box3D(-w*0.15, hd + 1, cabH*0.6, 3, 2, 15, Color.LIGHT_GRAY));
                parts.add(new Box3D(w*0.15 - 3, hd + 1, cabH*0.6, 3, 2, 15, Color.LIGHT_GRAY));
                // Top rim
                parts.add(new Box3D(-hw - 1, -hd - 1, cabH, w + 2, d + 2, 3, color.brighter()));
                break;

            case "Plant":
                double potH = 22;
                Color potC = new Color(200, 180, 150);
                parts.add(new Box3D(-hw + 8, -hd + 8, 0, w - 16, d - 16, potH, potC)); // Pot base
                parts.add(new Box3D(-hw + 6, -hd + 6, potH - 4, w - 12, d - 12, 5, potC.brighter())); // Pot rim
                Color leafC = new Color(34, 139, 34);
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

    private void drawBoxFaces(Graphics2D g2d, int ox, int oy, Box3D b) {
        Color topColor = b.c;
        Color leftColor = blend(b.c, Color.BLACK, 0.15f);
        Color rightColor = blend(b.c, Color.BLACK, 0.35f);
        
        // Front-left face
        drawIsoWallX(g2d, ox, oy, b.px, b.py + b.d, b.pz, b.w, b.h, leftColor, blend(leftColor, Color.BLACK, 0.2f));
        // Front-right face
        drawIsoWallY(g2d, ox, oy, b.px + b.w, b.py, b.pz, b.d, b.h, rightColor, blend(rightColor, Color.BLACK, 0.2f));
        // Top face
        drawIsoPolygon(g2d, ox, oy, b.px, b.py, b.pz + b.h, b.w, b.d, b.pz + b.h, topColor, blend(topColor, Color.WHITE, 0.1f));
    }

    private void drawIsoWallX(Graphics2D g2d, int ox, int oy, double px, double py, double pz, double w, double h, Color c1, Color c2) {
        int[] xs = new int[4], ys = new int[4];
        Point p1 = getIsoPoint(px, py, pz, ox, oy);
        Point p2 = getIsoPoint(px + w, py, pz, ox, oy);
        Point p3 = getIsoPoint(px + w, py, pz + h, ox, oy);
        Point p4 = getIsoPoint(px, py, pz + h, ox, oy);
        xs[0]=p1.x; ys[0]=p1.y; xs[1]=p2.x; ys[1]=p2.y; xs[2]=p3.x; ys[2]=p3.y; xs[3]=p4.x; ys[3]=p4.y;
        
        GradientPaint gp = new GradientPaint(p4.x, p4.y, c1, p2.x, p2.y, c2);
        g2d.setPaint(gp);
        g2d.fillPolygon(xs, ys, 4);
        g2d.setColor(new Color(0, 0, 0, 20)); g2d.drawPolygon(xs, ys, 4);
    }

    private void drawIsoWallY(Graphics2D g2d, int ox, int oy, double px, double py, double pz, double d, double h, Color c1, Color c2) {
        int[] xs = new int[4], ys = new int[4];
        Point p1 = getIsoPoint(px, py, pz, ox, oy);
        Point p2 = getIsoPoint(px, py + d, pz, ox, oy);
        Point p3 = getIsoPoint(px, py + d, pz + h, ox, oy);
        Point p4 = getIsoPoint(px, py, pz + h, ox, oy);
        xs[0]=p1.x; ys[0]=p1.y; xs[1]=p2.x; ys[1]=p2.y; xs[2]=p3.x; ys[2]=p3.y; xs[3]=p4.x; ys[3]=p4.y;
        
        GradientPaint gp = new GradientPaint(p4.x, p4.y, c1, p1.x, p1.y, c2); // top to bottom shading
        g2d.setPaint(gp);
        g2d.fillPolygon(xs, ys, 4);
        g2d.setColor(new Color(0, 0, 0, 20)); g2d.drawPolygon(xs, ys, 4);
    }

    private void drawIsoPolygon(Graphics2D g2d, int ox, int oy, double px, double py, double pz1, double w, double d, double pz2, Color c1, Color c2) {
        int[] xs = new int[4], ys = new int[4];
        Point p1 = getIsoPoint(px, py, pz1, ox, oy);
        Point p2 = getIsoPoint(px + w, py, pz1, ox, oy);
        Point p3 = getIsoPoint(px + w, py + d, pz2, ox, oy);
        Point p4 = getIsoPoint(px, py + d, pz2, ox, oy);
        xs[0]=p1.x; ys[0]=p1.y; xs[1]=p2.x; ys[1]=p2.y; xs[2]=p3.x; ys[2]=p3.y; xs[3]=p4.x; ys[3]=p4.y;
        
        GradientPaint gp = new GradientPaint(p1.x, p1.y, c1, p3.x, p3.y, c2); // corner to corner shading
        g2d.setPaint(gp);
        g2d.fillPolygon(xs, ys, 4);
        g2d.setColor(new Color(0, 0, 0, 20)); g2d.drawPolygon(xs, ys, 4);
    }

    private Point getIsoPoint(double px, double py, double pz, int offsetX, int offsetY) {
        int isoX = offsetX + (int)((px - py) * 0.866);
        int isoY = offsetY + (int)((px + py) / 2.0 - pz);
        return new Point(isoX, isoY);
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

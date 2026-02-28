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
            g2d.setColor(new Color(0, 0, 0, 80));
            g2d.fillRoundRect(x + 8, y + 8, width, height, 10, 10);
        }

        g2d.setColor(color);
        if ("Chair".equals(type) || "Plant".equals(type)) {
            g2d.fillOval(x, y, width, height);
            g2d.setColor(color.darker());
            g2d.drawOval(x, y, width, height);
        } else {
            g2d.fillRoundRect(x, y, width, height, 5, 5);
            g2d.setColor(color.darker());
            g2d.drawRoundRect(x, y, width, height, 5, 5);
            
            if ("Modern Sofa".equals(type)) {
                g2d.setColor(color.darker().darker());
                g2d.drawRect(x + 10, y + 10, width - 20, height - 20);
            } else if ("Bed".equals(type)) {
                g2d.setColor(Color.WHITE);
                g2d.fillRect(x + 5, y + 5, width - 10, height/3); 
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawRect(x + 5, y + 5, width - 10, height/3);
            }
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
            drawIsoPolygon(g2d, offsetX, offsetY, cx - shW/2.0, cy - shD/2.0, 0, shW, shD, 0, new Color(0, 0, 0, 50));
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
                parts.add(new Box3D(-hw, -hd, 0, legW, legW, tableH, legCol));
                parts.add(new Box3D(hw - legW, -hd, 0, legW, legW, tableH, legCol));
                parts.add(new Box3D(-hw, hd - legW, 0, legW, legW, tableH, legCol));
                parts.add(new Box3D(hw - legW, hd - legW, 0, legW, legW, tableH, legCol));
                parts.add(new Box3D(-hw - 2, -hd - 2, tableH, w + 4, d + 4, 4, color)); // Top
                break;

            case "Chair":
                double seatH = 20;
                double backH = 45;
                double cLeg = Math.min(4, Math.min(w, d) * 0.1);
                Color pColor = color.darker();
                parts.add(new Box3D(-hw, -hd, 0, cLeg, cLeg, seatH, pColor));
                parts.add(new Box3D(hw - cLeg, -hd, 0, cLeg, cLeg, seatH, pColor));
                parts.add(new Box3D(-hw, hd - cLeg, 0, cLeg, cLeg, seatH, pColor));
                parts.add(new Box3D(hw - cLeg, hd - cLeg, 0, cLeg, cLeg, seatH, pColor));
                parts.add(new Box3D(-hw - 1, -hd - 1, seatH, w + 2, d + 2, 4, color)); // Seat
                parts.add(new Box3D(-hw, -hd, seatH + 4, w, Math.max(4, d*0.1), backH - seatH - 4, color)); // Backrest at back
                break;

            case "Bed":
                double frameH = 12;
                double mattH = 10;
                double headH = 30;
                parts.add(new Box3D(-hw, -hd, 0, w, d, frameH, color.darker())); // Frame
                parts.add(new Box3D(-hw + 2, -hd + 5, frameH, w - 4, d - 7, mattH, Color.WHITE)); // Mattress
                parts.add(new Box3D(-hw, -hd, 0, w, 8, headH, color)); // Headboard
                parts.add(new Box3D(-hw + 10, -hd + 15, frameH + mattH, w - 20, Math.max(10, d*0.15), 5, Color.LIGHT_GRAY)); // Pillow
                break;

            case "Modern Sofa":
                double sSeatH = 18;
                double sArmH = 32;
                double sBackH = 40;
                double armT = Math.max(8, w * 0.15);
                parts.add(new Box3D(-hw + armT, -hd + 10, 0, w - armT*2, d - 10, sSeatH, color)); // Middle Seat
                parts.add(new Box3D(-hw, -hd, 0, w, 10, sBackH, color.darker())); // Back rest
                parts.add(new Box3D(-hw, -hd, 0, armT, d, sArmH, color.darker())); // Left arm
                parts.add(new Box3D(hw - armT, -hd, 0, armT, d, sArmH, color.darker())); // Right arm
                break;

            case "Cabinet":
                double cabH = 75;
                parts.add(new Box3D(-hw, -hd, 0, w, d, cabH, color));
                // Add tiny knobs on front face (positive y)
                parts.add(new Box3D(-w*0.2, hd, cabH*0.6, 4, 3, 4, color.darker().darker()));
                parts.add(new Box3D(w*0.2 - 4, hd, cabH*0.6, 4, 3, 4, color.darker().darker()));
                // Add front face line separator to imitate doors
                parts.add(new Box3D(-1, hd, 2, 2, 1, cabH - 4, color.darker().darker()));
                break;

            case "Plant":
                double potH = 20;
                parts.add(new Box3D(-hw + 5, -hd + 5, 0, w - 10, d - 10, potH, new Color(180, 130, 90))); // Pot
                parts.add(new Box3D(-hw, -hd, potH, w, d, 25, color)); // Leaves block
                break;

            default:
                parts.add(new Box3D(-hw, -hd, 0, w, d, 40, color));
                break;
        }
    }

    private void drawBoxFaces(Graphics2D g2d, int ox, int oy, Box3D b) {
        Color topColor = b.c;
        Color leftColor = b.c.darker();
        Color rightColor = b.c.darker().darker();
        
        // Front-left face
        drawIsoWallX(g2d, ox, oy, b.px, b.py + b.d, b.pz, b.w, b.h, leftColor);
        // Front-right face
        drawIsoWallY(g2d, ox, oy, b.px + b.w, b.py, b.pz, b.d, b.h, rightColor);
        // Top face
        drawIsoPolygon(g2d, ox, oy, b.px, b.py, b.pz + b.h, b.w, b.d, b.pz + b.h, topColor);
    }

    private void drawIsoWallX(Graphics2D g2d, int ox, int oy, double px, double py, double pz, double w, double h, Color c) {
        int[] xs = new int[4], ys = new int[4];
        Point p1 = getIsoPoint(px, py, pz, ox, oy);
        Point p2 = getIsoPoint(px + w, py, pz, ox, oy);
        Point p3 = getIsoPoint(px + w, py, pz + h, ox, oy);
        Point p4 = getIsoPoint(px, py, pz + h, ox, oy);
        xs[0]=p1.x; ys[0]=p1.y; xs[1]=p2.x; ys[1]=p2.y; xs[2]=p3.x; ys[2]=p3.y; xs[3]=p4.x; ys[3]=p4.y;
        g2d.setColor(c); g2d.fillPolygon(xs, ys, 4);
        g2d.setColor(new Color(0, 0, 0, 60)); g2d.drawPolygon(xs, ys, 4);
    }

    private void drawIsoWallY(Graphics2D g2d, int ox, int oy, double px, double py, double pz, double d, double h, Color c) {
        int[] xs = new int[4], ys = new int[4];
        Point p1 = getIsoPoint(px, py, pz, ox, oy);
        Point p2 = getIsoPoint(px, py + d, pz, ox, oy);
        Point p3 = getIsoPoint(px, py + d, pz + h, ox, oy);
        Point p4 = getIsoPoint(px, py, pz + h, ox, oy);
        xs[0]=p1.x; ys[0]=p1.y; xs[1]=p2.x; ys[1]=p2.y; xs[2]=p3.x; ys[2]=p3.y; xs[3]=p4.x; ys[3]=p4.y;
        g2d.setColor(c); g2d.fillPolygon(xs, ys, 4);
        g2d.setColor(new Color(0, 0, 0, 60)); g2d.drawPolygon(xs, ys, 4);
    }

    private void drawIsoPolygon(Graphics2D g2d, int ox, int oy, double px, double py, double pz1, double w, double d, double pz2, Color c) {
        int[] xs = new int[4], ys = new int[4];
        Point p1 = getIsoPoint(px, py, pz1, ox, oy);
        Point p2 = getIsoPoint(px + w, py, pz1, ox, oy);
        Point p3 = getIsoPoint(px + w, py + d, pz2, ox, oy);
        Point p4 = getIsoPoint(px, py + d, pz2, ox, oy);
        xs[0]=p1.x; ys[0]=p1.y; xs[1]=p2.x; ys[1]=p2.y; xs[2]=p3.x; ys[2]=p3.y; xs[3]=p4.x; ys[3]=p4.y;
        g2d.setColor(c); g2d.fillPolygon(xs, ys, 4);
        g2d.setColor(new Color(0, 0, 0, 60)); g2d.drawPolygon(xs, ys, 4);
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

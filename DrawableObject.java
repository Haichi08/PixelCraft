import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;

class DrawableObject {
    Shape shape;
    BufferedImage image;
    double x, y, width, height, angle;
    boolean isImage;

    public DrawableObject(Shape shape, double x, double y, double w, double h) {
        this.shape = shape;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.angle = 0;
        this.isImage = false;
    }

    public DrawableObject(BufferedImage image, double x, double y, double w, double h) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.angle = 0;
        this.isImage = true;
    }

    public void draw(Graphics2D g2) {
        g2.translate(x + width / 2, y + height / 2);
        g2.rotate(Math.toRadians(angle));
        if (isImage && image != null) {
            g2.drawImage(image, (int) -width / 2, (int) -height / 2, (int) width, (int) height, null);
        } else {
            g2.setColor(Color.BLACK);
            g2.draw(shape);
        }
        g2.rotate(-Math.toRadians(angle));
        g2.translate(-(x + width / 2), -(y + height / 2));
    }

    public boolean contains(Point2D p) {
        AffineTransform at = new AffineTransform();
        at.translate(x + width / 2, y + height / 2);
        at.rotate(Math.toRadians(angle));
        at.translate(-width / 2, -height / 2);

        Shape s = isImage ? new Rectangle2D.Double(0, 0, width, height) : shape;
        Shape transformed = at.createTransformedShape(s);
        return transformed.contains(p);
    }
}

class ObjectCanvas extends JPanel {
    private java.util.List<DrawableObject> objects = new ArrayList<>();
    private DrawableObject selected = null;
    private Point lastMouse;

    public ObjectCanvas() {
        setBackground(Color.WHITE);
        MouseAdapter ma = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();
                for (int i = objects.size() - 1; i >= 0; i--) {
                    if (objects.get(i).contains(p)) {
                        selected = objects.get(i);
                        lastMouse = p;
                        return;
                    }
                }
                selected = null;
            }

            public void mouseDragged(MouseEvent e) {
                if (selected != null && lastMouse != null) {
                    int dx = e.getX() - lastMouse.x;
                    int dy = e.getY() - lastMouse.y;
                    selected.x += dx;
                    selected.y += dy;
                    lastMouse = e.getPoint();
                    repaint();
                }
            }

            public void mouseWheelMoved(MouseWheelEvent e) {
                if (selected != null) {
                    selected.angle += e.getWheelRotation() * 5;
                    repaint();
                }
            }

            public void mouseReleased(MouseEvent e) {
                selected = null;
                lastMouse = null;
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
        addMouseWheelListener(ma);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (DrawableObject obj : objects) {
            obj.draw(g2);
        }
    }

    public void addShape(String shapeType) {
        Shape shape;
        switch (shapeType) {
            case "Rectangle":
                shape = new Rectangle2D.Double(0, 0, 100, 60);
                break;
            case "Oval":
                shape = new Ellipse2D.Double(0, 0, 100, 60);
                break;
            case "Triangle":
                Polygon triangle = new Polygon(
                        new int[]{50, 0, 100},
                        new int[]{0, 100, 100},
                        3
                );
                shape = triangle;
                break;
            default:
                return;
        }
        objects.add(new DrawableObject(shape, 100, 100, 100, 100));
        repaint();
    }

    public void importImage(File file) {
        try {
            BufferedImage img = ImageIO.read(file);
            objects.add(new DrawableObject(img, 100, 100, img.getWidth() / 2, img.getHeight() / 2));
            repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load image.");
        }
    }

    public void clearCanvas() {
        objects.clear();
        repaint();
    }
}

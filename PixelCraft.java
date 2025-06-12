import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PixelCraft extends JPanel {
    private int lastX, lastY;
    private Image image;
    private Graphics2D g2;
    private Color currentColor = Color.BLACK;
    private int brushSize = 5;

    public PixelCraft() {
        setDoubleBuffered(false);
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                if (g2 != null) {
                    g2.setStroke(new BasicStroke(brushSize));
                    g2.setPaint(currentColor);
                    g2.drawLine(lastX, lastY, x, y);
                    repaint();
                    lastX = x;
                    lastY = y;
                }
            }
        });
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) {
            image = createImage(getWidth(), getHeight());
            g2 = (Graphics2D) image.getGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            clear();
        }
        g.drawImage(image, 0, 0, null);
    }

    public void clear() {
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setPaint(currentColor);
        repaint();
    }

    public void setBrushColor(Color color) {
        this.currentColor = color;
    }

    public void setBrushSize(int size) {
        this.brushSize = size;
    }

    // Main method with two canvases and shared tool panel
    public static void main(String[] args) {
        JFrame frame = new JFrame("Double Canvas Drawing App");

        ObjectCanvas canvas1 = new ObjectCanvas(); // Left: Shape & Image
        PixelCraft canvas2 = new PixelCraft();     // Right: Freehand drawing

        // Tool panel
        JPanel tools = new JPanel();
        tools.setLayout(new BoxLayout(tools, BoxLayout.Y_AXIS));
        tools.setPreferredSize(new Dimension(150, 0));

        // Shape buttons
        JButton rectBtn = new JButton("Add Rectangle");
        JButton ovalBtn = new JButton("Add Oval");
        JButton triBtn = new JButton("Add Triangle");

        rectBtn.addActionListener(e -> canvas1.addShape("Rectangle"));
        ovalBtn.addActionListener(e -> canvas1.addShape("Oval"));
        triBtn.addActionListener(e -> canvas1.addShape("Triangle"));

        JButton importImg = new JButton("Import Image");
        importImg.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                canvas1.importImage(chooser.getSelectedFile());
            }
        });

        // Color buttons for freehand drawing
        JButton black = new JButton("Black");
        JButton red = new JButton("Red");
        JButton blue = new JButton("Blue");
        JButton green = new JButton("Green");

        ActionListener colorListener = e -> {
            Color color = switch (((JButton) e.getSource()).getText()) {
                case "Red" -> Color.RED;
                case "Blue" -> Color.BLUE;
                case "Green" -> Color.GREEN;
                default -> Color.BLACK;
            };
            canvas2.setBrushColor(color);
        };

        black.addActionListener(colorListener);
        red.addActionListener(colorListener);
        blue.addActionListener(colorListener);
        green.addActionListener(colorListener);

        // Brush size for right canvas
        JLabel sizeLabel = new JLabel("Brush Size");
        JSlider sizeSlider = new JSlider(1, 20, 5);
        sizeSlider.setMajorTickSpacing(5);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);
        sizeSlider.addChangeListener(e -> canvas2.setBrushSize(sizeSlider.getValue()));

        // Clear buttons
        JButton clearBoth = new JButton("Clear Both");
        clearBoth.addActionListener(e -> {
            canvas1.clearCanvas();
            canvas2.clear();
        });

        // Add all tool components
        tools.add(Box.createRigidArea(new Dimension(0, 10)));
        tools.add(importImg);
        tools.add(rectBtn);
        tools.add(ovalBtn);
        tools.add(triBtn);
        tools.add(Box.createRigidArea(new Dimension(0, 20)));
        tools.add(black);
        tools.add(red);
        tools.add(blue);
        tools.add(green);
        tools.add(Box.createRigidArea(new Dimension(0, 20)));
        tools.add(sizeLabel);
        tools.add(sizeSlider);
        tools.add(Box.createRigidArea(new Dimension(0, 20)));
        tools.add(clearBoth);

        // Layout for canvases
        JPanel canvasPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        canvasPanel.add(canvas1);
        canvasPanel.add(canvas2);

        frame.setLayout(new BorderLayout());
        frame.add(tools, BorderLayout.WEST);
        frame.add(canvasPanel, BorderLayout.CENTER);
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

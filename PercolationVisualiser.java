import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class PercolationVisualiser extends JPanel {
    private static final int CELL_SIZE = 15;
    private Percolation percolation;
    private int n;
    private final Set<Point> opened = new HashSet<>();
    private final JLabel statusLabel;
    private final JSlider speedSlider;
    private int delay = 10;

    public PercolationVisualiser(int n, JLabel statusLabel, JSlider speedSlider) {
        this.n = n;
        this.percolation = new Percolation(n);
        this.statusLabel = statusLabel;
        this.speedSlider = speedSlider;

        setPreferredSize(new Dimension(n * CELL_SIZE, n * CELL_SIZE));
        setBackground(Color.BLACK);

        speedSlider.addChangeListener(e -> delay = 100 - speedSlider.getValue());

        new Thread(this::simulate).start();
    }

    private void simulate() {
        while (!percolation.percolates()) {
            int row = (int) (Math.random() * n);
            int col = (int) (Math.random() * n);
            if (!percolation.isOpen(row, col)) {
                percolation.open(row, col);
                opened.add(new Point(row, col));
                repaint();
                updateStatus();

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ignored) {}
            }
        }
        updateStatus();
    }

    private void updateStatus() {
        double threshold = (double) percolation.numberOfOpenSites() / (n * n);
        boolean doesPercolate = percolation.percolates();
        statusLabel.setText(String.format(
                "Open Sites: %d | Percolates: %s | Threshold: %.4f",
                percolation.numberOfOpenSites(),
                doesPercolate ? "Yes" : "No",
                threshold
        ));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;

                if (percolation.isFull(row, col)) {
                    g.setColor(Color.BLUE);
                } else if (percolation.isOpen(row, col)) {
                    g.setColor(Color.WHITE);
                } else {
                    g.setColor(Color.BLACK);
                }
                g.fillRoundRect(x, y, CELL_SIZE, CELL_SIZE, 4, 4);
                g.setColor(Color.GRAY);
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    public static void main(String[] args) {
        boolean isDark = true;
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            UIManager.put("Button.arc", 999);
            UIManager.put("Component.arc", 12);
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf.");
        }

        JFrame frame = new JFrame("Percolation Visualiser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel statusLabel = new JLabel("Simulation starting...", SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(statusLabel, BorderLayout.SOUTH);

        JSlider speedSlider = new JSlider(1, 99, 90);
        speedSlider.setMajorTickSpacing(20);
        speedSlider.setMinorTickSpacing(5);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setBorder(BorderFactory.createTitledBorder("Speed"));

        JLabel gridSizeLabel = new JLabel("Grid Size:");
        JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(20, 5, 100, 1));
        sizeSpinner.setPreferredSize(new Dimension(60, 25));


        JButton resetButton = new JButton("Reset");

        JCheckBox themeToggle = new JCheckBox("Dark Mode", true);
        themeToggle.addActionListener(e -> {
            try {
                if (themeToggle.isSelected()) {
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                } else {
                    UIManager.setLookAndFeel(new FlatLightLaf());
                }
                SwingUtilities.updateComponentTreeUI(frame);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(speedSlider);
        topPanel.add(gridSizeLabel);
        topPanel.add(sizeSpinner);
        topPanel.add(resetButton);
        topPanel.add(themeToggle);
        frame.add(topPanel, BorderLayout.NORTH);

        JPanel visualiserHolder = new JPanel(new BorderLayout());
        frame.add(visualiserHolder, BorderLayout.CENTER);

        PercolationVisualiser visualiser = new PercolationVisualiser((int) sizeSpinner.getValue(), statusLabel, speedSlider);
        JPanel paddedGrid = new JPanel(new BorderLayout());
        paddedGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        paddedGrid.add(visualiser, BorderLayout.CENTER);
        visualiserHolder.add(paddedGrid, BorderLayout.CENTER);

        resetButton.addActionListener(e -> {
            visualiserHolder.removeAll();
            int newSize = (int) sizeSpinner.getValue();
            PercolationVisualiser newVisualiser = new PercolationVisualiser(newSize, statusLabel, speedSlider);
            JPanel newPaddedGrid = new JPanel(new BorderLayout());
            newPaddedGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            newPaddedGrid.add(newVisualiser, BorderLayout.CENTER);
            visualiserHolder.add(newPaddedGrid, BorderLayout.CENTER);
            visualiserHolder.revalidate();
            visualiserHolder.repaint();
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

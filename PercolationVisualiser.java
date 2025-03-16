import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class PercolationVisualiser extends JPanel {
    private static final int CELL_SIZE = 15;
    private final Percolation percolation;
    private final int n;
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

        // Update delay dynamically
        speedSlider.addChangeListener(e -> delay = 100 - speedSlider.getValue());

        // Start the simulation
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
        updateStatus(); // Final update
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
                g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.GRAY);
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    public static void main(String[] args) {
        int n = 20;
        if (args.length > 0) {
            n = Integer.parseInt(args[0]);
        }

        // Create window
        JFrame frame = new JFrame("Percolation Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Stats label
        JLabel statusLabel = new JLabel("Simulation starting...", SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(statusLabel, BorderLayout.SOUTH);

        // Speed slider
        JSlider speedSlider = new JSlider(1, 99, 90); // inverse delay
        speedSlider.setMajorTickSpacing(20);
        speedSlider.setMinorTickSpacing(5);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setBorder(BorderFactory.createTitledBorder("Speed"));

        // GUI panel (top)
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(speedSlider, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.NORTH);

        // Simulation panel (center)
        PercolationVisualiser visualiser = new PercolationVisualiser(n, statusLabel, speedSlider);
        frame.add(visualiser, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

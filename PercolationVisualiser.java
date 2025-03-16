import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class PercolationVisualiser extends JPanel {
    private static final int CELL_SIZE = 15;
    private final Percolation percolation;
    private final int n;
    private final Set<Point> opened = new HashSet<>();

    public PercolationVisualiser(int n) {
        this.n = n;
        this.percolation = new Percolation(n);
        setPreferredSize(new Dimension(n * CELL_SIZE, n * CELL_SIZE));
        setBackground(Color.BLACK);

        // Start the simulation in a new thread so GUI stays responsive
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

                try {
                    Thread.sleep(10); // control speed
                } catch (InterruptedException ignored) {}
            }
        }

        System.out.println("Percolates! Open sites: " + percolation.numberOfOpenSites());
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
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE); // cell border
            }
        }
    }

    public static void main(String[] args) {
        int n = 20; // default grid size
        if (args.length > 0) {
            n = Integer.parseInt(args[0]);
        }

        JFrame frame = new JFrame("Percolation Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new PercolationVisualiser(n));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

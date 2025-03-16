import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;

public class PercolationStats {
    private final double[] thresholds;
    private final int trials;

    // Constructor that runs T experiments
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0)
            throw new IllegalArgumentException("n and trials must be > 0");

        this.trials = trials;
        thresholds = new double[trials];

        for (int t = 0; t < trials; t++) {
            Percolation perc = new Percolation(n);
            while (!perc.percolates()) {
                int row = StdRandom.uniformInt(n);
                int col = StdRandom.uniformInt(n);
                perc.open(row, col);
            }
            thresholds[t] = (double) perc.numberOfOpenSites() / (n * n);
        }
    }

    // Statistical methods
    public double mean() {
        return StdStats.mean(thresholds);
    }

    public double stddev() {
        return StdStats.stddev(thresholds);
    }

    public double confidenceLow() {
        return mean() - (1.96 * stddev()) / Math.sqrt(trials);
    }

    public double confidenceHigh() {
        return mean() + (1.96 * stddev()) / Math.sqrt(trials);
    }

    // Main method to run the simulation
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);

        Stopwatch timer = new Stopwatch(); // start timer
        int estimatedBytes = (int)(9 * Math.pow(n, 2));
        System.out.println("Estimated memory usage   = " + estimatedBytes + " bytes (~" + estimatedBytes / 1024 + " KB)");

        PercolationStats stats = new PercolationStats(n, trials);

        double time = timer.elapsedTime(); // stop timer

        System.out.println("mean                    = " + stats.mean());
        System.out.println("stddev                  = " + stats.stddev());
        System.out.println("95% confidence interval = [" +
                stats.confidenceLow() + ", " + stats.confidenceHigh() + "]");
        System.out.println("runtime (seconds)       = " + time);
    }
}
import edu.princeton.cs.algs4.StdStats;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PercolationStatsConcurrent {
    private final double[] thresholds;
    private final int trials;

    public PercolationStatsConcurrent(int n, int trials) throws InterruptedException, ExecutionException {
        if (n <= 0 || trials <= 0)
            throw new IllegalArgumentException("n and trials must be > 0");

        this.trials = trials;
        this.thresholds = new double[trials];

        // Use a thread pool with as many threads as processors
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<Future<Double>> futures = new ArrayList<>();

        for (int t = 0; t < trials; t++) {
            final int trial = t;
            Callable<Double> task = () -> {
                Percolation perc = new Percolation(n);
                while (!perc.percolates()) {
                    int row = (int) (Math.random() * n);
                    int col = (int) (Math.random() * n);
                    perc.open(row, col);
                }
                return (double) perc.numberOfOpenSites() / (n * n);
            };
            futures.add(executor.submit(task));
        }

        for (int i = 0; i < trials; i++) {
            thresholds[i] = futures.get(i).get(); // wait and retrieve result
        }

        executor.shutdown();
    }

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

    public static void main(String[] args) throws Exception {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);

        long start = System.currentTimeMillis();

        PercolationStatsConcurrent stats = new PercolationStatsConcurrent(n, trials);

        long duration = System.currentTimeMillis() - start;

        System.out.println("mean                    = " + stats.mean());
        System.out.println("stddev                  = " + stats.stddev());
        System.out.println("95% confidence interval = [" +
                stats.confidenceLow() + ", " + stats.confidenceHigh() + "]");
        System.out.println("runtime (ms)            = " + duration);
    }
}

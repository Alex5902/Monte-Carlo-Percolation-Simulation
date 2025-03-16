import java.util.*;

public class PercolationProbability {
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        double p = Double.parseDouble(args[1]);

        Percolation perc = new Percolation(n);
        int sitesToOpen = (int) Math.round(p * n * n);
        Set<String> opened = new HashSet<>();

        Random rand = new Random();
        while (opened.size() < sitesToOpen) {
            int row = rand.nextInt(n);
            int col = rand.nextInt(n);
            String key = row + "," + col;
            if (!opened.contains(key)) {
                perc.open(row, col);
                opened.add(key);
            }
        }

        System.out.println(perc.percolates() ? "1" : "0");
    }
}

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final int n;
    private final boolean[][] grid;
    private final WeightedQuickUnionUF uf;
    private final int virtualTop;
    private final int virtualBottom;
    private int openSites;

    public Percolation(int n) {
        if (n <= 0) throw new IllegalArgumentException("Grid size must be > 0");
        this.n = n;
        grid = new boolean[n][n];
        uf = new WeightedQuickUnionUF(n * n + 2); // +2 for virtual top and bottom
        virtualTop = n * n;
        virtualBottom = n * n + 1;
        openSites = 0;
    }

    public void open(int row, int col) {
        checkBounds(row, col);
        if (!isOpen(row, col)) {
            grid[row][col] = true;
            openSites++;

            int index = xyTo1D(row, col);
            if (row == 0) uf.union(index, virtualTop);
            if (row == n - 1) uf.union(index, virtualBottom);

            connectIfOpen(row, col, row - 1, col); // top
            connectIfOpen(row, col, row + 1, col); // bottom
            connectIfOpen(row, col, row, col - 1); // left
            connectIfOpen(row, col, row, col + 1); // right
        }
    }

    public boolean isOpen(int row, int col) {
        checkBounds(row, col);
        return grid[row][col];
    }

    public boolean isFull(int row, int col) {
        checkBounds(row, col);
        return isOpen(row, col) && uf.connected(xyTo1D(row, col), virtualTop);
    }

    public boolean percolates() {
        return uf.connected(virtualTop, virtualBottom);
    }

    public int numberOfOpenSites() {
        return openSites;
    }

    private void connectIfOpen(int row1, int col1, int row2, int col2) {
        if (row2 >= 0 && row2 < n && col2 >= 0 && col2 < n && isOpen(row2, col2)) {
            uf.union(xyTo1D(row1, col1), xyTo1D(row2, col2));
        }
    }

    private int xyTo1D(int row, int col) {
        return row * n + col;
    }

    private void checkBounds(int row, int col) {
        if (row < 0 || row >= n || col < 0 || col >= n)
            throw new IndexOutOfBoundsException("Index out of bounds");
    }
}

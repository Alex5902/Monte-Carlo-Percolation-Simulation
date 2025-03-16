public class PercolationTest {
    public static void main(String[] args) {
        test2x2();
        test3x3();
        testException();
    }

    private static void test2x2() {
        Percolation p = new Percolation(2);
        assert !p.percolates() : "Should not percolate initially";
        p.open(0, 0);
        p.open(1, 0);
        assert p.percolates() : "Should percolate after vertical path";
        assert p.isOpen(1, 0) : "Site (1,0) should be open";
        assert p.isFull(1, 0) : "Site (1,0) should be full (connected to top)";
        System.out.println("test2x2 passed");
    }

    private static void test3x3() {
        Percolation p = new Percolation(3);
        p.open(0, 1);
        p.open(1, 1);
        p.open(2, 1);
        assert p.percolates() : "Should percolate vertically";
        System.out.println("test3x3 passed");
    }

    private static void testException() {
        try {
            new Percolation(0);
            assert false : "Should throw exception for N <= 0";
        } catch (IllegalArgumentException e) {
            System.out.println("testException (constructor) passed");
        }

        try {
            Percolation p = new Percolation(3);
            p.open(-1, 0);
            assert false : "Should throw exception for invalid indices";
        } catch (IndexOutOfBoundsException e) {
            System.out.println("testException (open) passed");
        }
    }
}

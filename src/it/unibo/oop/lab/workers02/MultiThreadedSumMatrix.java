package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix {

    /**
     * @param n
     *              number of workers that will sum the matrix
     */

    private final int nthread;

    public MultiThreadedSumMatrix(final int n) {
        this.nthread = n;
    }

    /**
     * @param matrix
     *                   the matrix to use
     * @return list of every element of the matrix Creates a list with every element
     *         of the matrix.
     */
    private List<Double> createList(final double[][] matrix) {

        final ArrayList<Double> list = new ArrayList<>();
        // Devo capire come sistemare la questione della length delle colonne
        // final int length = (int) (matrix.length / matrix[0].length);
        final int l = matrix.length;

        for (int i = 0; i < l; i++) {
            // list.addAll(matrix[i]);
            // Arrays.asList(null)
            for (int j = 0; j < l; j++) {
                list.add(matrix[i][j]);
            }
        }
        return list;
    }

    /** {@inheritDoc} */
    @Override
    public double sum(final double[][] matrix) {

        final List<Double> list = createList(matrix);

        final int size = list.size() % nthread + list.size() / nthread;
        /*
         * Build a list of workers
         */
        final List<Worker> workers = new ArrayList<>(nthread);

        for (int start = 0; start < list.size(); start += size) {
            workers.add(new Worker(list, start, size));
        }

        /*
         * Start them
         */
        for (final Worker w : workers) {
            w.start();
        }
        /*
         * Wait for every one of them to finish. This operation is _way_ better done by
         * using barriers and latches, and the whole operation would be better done with
         * futures.
         */
        double sum = 0;
        for (final Worker w : workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        /*
         * Return the sum
         */
        return sum;
    }

    private static class Worker extends Thread {

        private final List<Double> list;
        private final int startpos;
        private final int nelem;
        private double res;

        Worker(final List<Double> list, final int startpos, final int nelem) {
            super();
            this.list = list;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        public void run() {
            for (int i = startpos; i < list.size() && i < startpos + nelem; i++) {
                this.res += this.list.get(i);
            }
        }

        /**
         * Returns the result of summing up the integers within the list.
         * 
         * @return the sum of every element in the array
         */
        public double getResult() {
            return this.res;
        }

    }

}

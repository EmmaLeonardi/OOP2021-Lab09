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

    /** {@inheritDoc} */
    @Override
    public double sum(final double[][] matrix) {

        final List<Worker> workers = new ArrayList<>(nthread);
        final int nRow = matrix.length;
        final int nCol = matrix[0].length;
        final int totElements = nRow * nCol;
        final int nElementsThread = (int) Math.ceil(totElements / nthread);
        int elementsLeft = totElements;
        int start = 0;
        double sum = 0;

        for (int i = 0; i < nthread; i++) {
            // The number of elements the thread will sum
            // If the number of remaining elements is less than the standard amount a thread
            // reads,
            // The thread reads all the elements leftover
            final int nEleThread = start + nElementsThread > totElements ? elementsLeft : nElementsThread;
            workers.add(new Worker(matrix, start, nEleThread));
            elementsLeft = elementsLeft - nEleThread;
            start = start + nElementsThread;
        }

        for (final Worker w : workers) {
            w.start();
        }
        for (final Worker w : workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }

        }
        return sum;
    }

    private static class Worker extends Thread {

        private final double matrix[][];
        private final int startpos;
        private final int nelem;
        private double res;

        Worker(final double matrix[][], final int startpos, final int nelem) {
            super();
            this.matrix = matrix.clone();
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        public void run() {
            for (int i = startpos; i < startpos + nelem; i++) {
                this.res += matrix[i / matrix[0].length][i % matrix.length];
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

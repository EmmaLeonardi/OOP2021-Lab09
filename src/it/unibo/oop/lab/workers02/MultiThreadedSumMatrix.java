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
        final int nElements4Thread = (int) Math.ceil((totElements * 1.0) / nthread);
        int elementsLeftover = totElements;
        int start = 0;
        double sum = 0;

        for (int i = 0; i < nthread; i++) {

            /*
             * nElThread is the number of elements that the thread will sum, starting by
             * start. If the number of elements the thread is supposed to sum is less than
             * the elements left in the matrix, the thread sums all the leftover elements
             */
            final int nElThread = start + nElements4Thread > totElements ? elementsLeftover : nElements4Thread;
            workers.add(new Worker(matrix, start, nElThread));
            elementsLeftover = elementsLeftover - nElThread;
            start = start + nElements4Thread;
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

    /**
     * Worker is a class that sums the values of a matrix
     */
    private static class Worker extends Thread {

        private final double matrix[][];
        private final int startpos;
        private final int nelem;
        private double res;

        /**
         * @param matrix
         *                     the matrix to sum
         * @param startpos
         *                     the starting element (as the nth element of the matrix)
         * @param nelem
         *                     the number of elements to sum starting from startpos
         */
        Worker(final double matrix[][], final int startpos, final int nelem) {
            super();
            this.matrix = matrix.clone();
            this.startpos = startpos;
            this.nelem = nelem;
        }

        /**
         * Sums the elements in between the parameters specified in the constructor
         */
        @Override
        public void run() {
            System.out.println("This thread sums the elements from "+startpos+" to "+(startpos+nelem));
            for (int i = startpos; i < startpos + nelem; i++) {
                this.res += matrix[i / matrix[0].length][i % matrix.length];
            }
        }

        /**
         * @return sum of the doubles in between the matrix limits
         */
        public double getResult() {
            return this.res;
        }

    }

}

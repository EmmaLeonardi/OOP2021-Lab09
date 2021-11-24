package it.unibo.oop.lab.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Builds the GUI
 */
public class AnotherConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 3L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel number = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");

    public AnotherConcurrentGUI() {
        super("Test of Another Concurrent GUI");
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel p = new JPanel();
        this.add(p);
        p.add(number);
        p.add(up);
        p.add(down);
        p.add(stop);
        this.setVisible(true);

        final Stopper stopper = new Stopper();
        final var thread = new Thread(stopper);
        final Agent a = new Agent();
        final var agent = new Thread(a);
        thread.start();
        agent.start();

        up.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                a.countUp();
            }
        });

        down.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                a.countDown();
            }
        });
        stop.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                a.stopCounting();
                up.setEnabled(false);
                down.setEnabled(false);
                stop.setEnabled(false);
            }
        });

    }

    /**
     * Agent is a counter.
     */
    private class Agent implements Runnable {
        private volatile boolean stop;
        private volatile int counter;
        private volatile boolean isCountingDown;

        @Override
        /**
         * The main body of the thread
         */
        public void run() {
            while (!this.stop) {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            // This will happen in the EDT: since i'm reading counter it needs to be
                            // volatile.
                            AnotherConcurrentGUI.this.number.setText(Integer.toString(Agent.this.counter));
                        }

                    });

                    if (this.isCountingDown) {
                        this.counter--;
                    } else {
                        this.counter++;
                    }

                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        /**
         * Stops the counter
         */
        public void stopCounting() {
            this.stop = true;
        }

        /**
         * Makes the counter increment
         */
        public void countUp() {
            this.isCountingDown = false;
        }

        /**
         * Makes the counter decrement
         */
        public void countDown() {
            this.isCountingDown = true;
        }

    }

    /**
     * Stopper is a thread that stops agent after 10 seconds
     */
    private class Stopper implements Runnable {

        @Override
        /**
         * The main body of the thread
         */
        public void run() {

            try {
                Thread.sleep(10_000);
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        stop.doClick();
                    }

                });
            } catch (InterruptedException | InvocationTargetException e) {
                e.printStackTrace();

            }

        }
    }
}

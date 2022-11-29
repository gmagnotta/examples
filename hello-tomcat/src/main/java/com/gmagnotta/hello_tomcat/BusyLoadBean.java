package com.gmagnotta.hello_tomcat;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named
@SessionScoped
public class BusyLoadBean implements Serializable {

    public void startBusyLoad() {

        String cores = System.getenv("CORES");
        String threads = System.getenv("THREADS");
        String runtime = System.getenv("DURATION");


        int numCore = (cores != null ? Integer.parseInt(cores) : 2);
        int numThreadsPerCore = (threads != null ? Integer.parseInt(threads) : 2);

        double load = 0.8;
        final long duration = (runtime != null ? Integer.parseInt(runtime) : 100000);


        for (int thread = 0; thread < numCore * numThreadsPerCore; thread++) {
            new BusyThread("Thread" + thread, load, duration).start();
        }
    }

    /**
     * Thread that actually generates the given load
     * 
     * @author Sriram
     * 
     * https://blog.caffinc.com/2016/03/cpu-load-generator/
     */
    private static class BusyThread extends Thread {
        private double load;
        private long duration;
        private final Logger MYLOGGER = Logger.getLogger(BusyThread.class.getName());

        /**
         * Constructor which creates the thread
         * 
         * @param name     Name of this thread
         * @param load     Load % that this thread should generate
         * @param duration Duration that this thread should generate the load for
         */
        public BusyThread(String name, double load, long duration) {
            super(name);
            this.load = load;
            this.duration = duration;
        }

        /**
         * Generates the load when run
         */
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            try {
                // Loop for the given duration
                while (System.currentTimeMillis() - startTime < duration) {
                    // Every 100ms, sleep for the percentage of unladen time
                    if (System.currentTimeMillis() % 100 == 0) {
                        Thread.sleep((long) Math.floor((1 - load) * 100));
                    }
                }
            } catch (InterruptedException e) {
                MYLOGGER.log(Level.SEVERE, "Exceptoin", e);
            }
        }
    }

}

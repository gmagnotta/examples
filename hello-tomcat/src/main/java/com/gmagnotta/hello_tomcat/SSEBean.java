package com.gmagnotta.hello_tomcat;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class SSEBean {

    @Inject
    NotificationListener listener;

    SSEThread ssThread;

    public void startSSE() {

        if (ssThread != null) {
            ssThread.interrupt();
        }

        ssThread = new SSEThread("thread");

        ssThread.start();

    }

    public void stopSSE() {

        if (ssThread.isAlive()) {
            ssThread.interrupt();
        }

    }

    private class SSEThread extends Thread {
        private final Logger MYLOGGER = Logger.getLogger(SSEThread.class.getName());

        /**
         * Constructor which creates the thread
         * 
         * @param name     Name of this thread
         * @param load     Load % that this thread should generate
         * @param duration Duration that this thread should generate the load for
         */
        public SSEThread(String name) {
            super(name);
        }

        /**
         * Generates the load when run
         */
        @Override
        public void run() {
            try {
                while (true) {
                    Random random = new Random();

                    double a = random.nextDouble(100D);
                    double b = random.nextDouble(100D);
                    double c = random.nextDouble(100D);

                    listener.notify(System.currentTimeMillis() + ";" + a + ";" + b + ";" + c);
                    Thread.sleep(1000);
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                MYLOGGER.log(Level.SEVERE, "Exception", e);
            }

        }
    }

}

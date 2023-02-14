package cn.korilweb.task;

import java.io.PrintWriter;

public class ClientHeartBeatTask implements Runnable {

    private final PrintWriter writer;

    public ClientHeartBeatTask(PrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public void run() {
        while (true) {
            writer.println("/heartbeat");
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

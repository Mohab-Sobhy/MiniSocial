package com.example.minisocial.service.LogManagement;

import com.example.minisocial.Interface.IObserver;

import java.io.Console;
import java.util.LinkedList;
import java.util.Queue;

public class LogConsoleService implements IObserver {
    private static final String BLUE = "\033[34m";
    private static final String RESET = "\033[0m";
    private Queue<String> queue = new LinkedList<>();
    private Thread monitoringThread;
    private volatile boolean running = true;

    public LogConsoleService() {
        startMonitoringThread();
    }

    @Override
    public void notify(String message) {
        queue.add(message);
    }

    private void startMonitoringThread() {
        monitoringThread = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(30000);
                    if (!queue.isEmpty()) {
                        System.out.println("[LOG] " + queue.poll());
                    }
                } catch (InterruptedException e) {
                    System.out.println("[LOG] Monitoring thread was interrupted");
                    Thread.currentThread().interrupt();
                }
            }
        });

        monitoringThread.start();
        System.out.println("[LOG] Started stack monitoring thread (60s interval)");
    }

    public void stopMonitoring() {
        running = false;
        monitoringThread.interrupt();
        System.out.println("[LOG] Stopped stack monitoring thread");
    }

    private void log(String message) {
        System.out.println(BLUE + message + RESET);
    }
}
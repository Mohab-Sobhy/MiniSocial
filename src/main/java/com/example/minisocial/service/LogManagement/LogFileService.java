package com.example.minisocial.service.LogManagement;

import com.example.minisocial.Interface.IObserver;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LogFileService implements IObserver {
    private final String logFilePath = "application.log";

    @Override
    public void notify(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}
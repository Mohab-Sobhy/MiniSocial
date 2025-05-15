package com.example.minisocial;

import com.example.minisocial.service.LogManagement.LogConsoleService;
import com.example.minisocial.service.LogManagement.LogFileService;
import com.example.minisocial.service.LogManagement.NotificationService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

@Singleton
@Startup
public class AppStartup {

    @PostConstruct
    public void init() {
        NotificationService notificationService = NotificationService.getInstance();
        notificationService.addObserver(new LogFileService());
        notificationService.addObserver(new LogConsoleService());
    }
}
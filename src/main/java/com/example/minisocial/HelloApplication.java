package com.example.minisocial;

import com.example.minisocial.service.LogManagement.LogConsoleService;
import com.example.minisocial.service.LogManagement.LogFileService;
import com.example.minisocial.service.LogManagement.NotificationService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class  HelloApplication extends Application {

}
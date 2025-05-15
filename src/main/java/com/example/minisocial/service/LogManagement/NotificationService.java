package com.example.minisocial.service.LogManagement;

import com.example.minisocial.Interface.IObserver;

import java.util.HashSet;
import java.util.Set;

public class NotificationService {
    private Set<IObserver> observers = new HashSet<>();
    private static NotificationService notificationService;

    private NotificationService() {}

    public static synchronized NotificationService getInstance() {
        if (notificationService == null) {
            return notificationService = new NotificationService();
        }
        return notificationService;
    }

    public void addObserver(IObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String message) {
        for (IObserver observer : observers) {
            observer.notify(message);
        }
    }


}

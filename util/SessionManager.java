package com.campusevents.util;

import com.campusevents.model.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Thread-safe Singleton session manager for tracking the currently authenticated user.
 */
public class SessionManager {

    public interface SessionListener {
        void onSessionChanged(User currentUser);
    }

    private static final SessionManager INSTANCE = new SessionManager();

    private User currentUser = null;
    private final List<SessionListener> listeners = new ArrayList<>();

    private SessionManager() {}

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public synchronized void login(User user) {
        this.currentUser = user;
        notifyListeners();
    }

    public synchronized void logout() {
        this.currentUser = null;
        notifyListeners();
    }

    public synchronized User getCurrentUser() {
        return currentUser;
    }

    public synchronized boolean isLoggedIn() {
        return currentUser != null;
    }

    public synchronized boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public synchronized boolean isStudent() {
        return currentUser != null && currentUser.isStudent();
    }

    public synchronized void addSessionListener(SessionListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public synchronized void removeSessionListener(SessionListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        List<SessionListener> copy;
        synchronized (this) {
            copy = new ArrayList<>(listeners);
        }
        for (SessionListener l : copy) {
            try {
                l.onSessionChanged(currentUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


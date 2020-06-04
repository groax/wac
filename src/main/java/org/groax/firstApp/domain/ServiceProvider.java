package org.groax.firstApp.domain;

public class ServiceProvider {
    private static final WorldService worldService = new WorldService();

    public static WorldService getWorldService() {
        return worldService;
    }
}

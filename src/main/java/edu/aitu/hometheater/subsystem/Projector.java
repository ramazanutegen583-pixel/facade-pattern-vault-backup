package edu.aitu.hometheater.subsystem;

/** Controls the power and display mode of the simulated projector. */
public final class Projector {
    private boolean on;
    private boolean wideScreen;

    public void turnOn() {
        on = true;
        System.out.println("[Projector] Power ON.");
    }

    public void enableWideScreen() {
        if (!on) throw new IllegalStateException("Turn on the projector first.");
        wideScreen = true;
        System.out.println("[Projector] Widescreen mode enabled.");
    }

    public void turnOff() {
        on = false;
        wideScreen = false;
        System.out.println("[Projector] Power OFF.");
    }

    public boolean isOn() { return on; }
    public boolean isWideScreen() { return wideScreen; }
}

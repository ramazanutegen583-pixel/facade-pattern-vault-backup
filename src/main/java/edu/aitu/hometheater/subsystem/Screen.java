package edu.aitu.hometheater.subsystem;

/** Tracks the position of the projection screen. */
public final class Screen {
    private boolean lowered;

    public void lower() {
        lowered = true;
        System.out.println("[Screen] Lowered for the movie.");
    }

    public void raise() {
        lowered = false;
        System.out.println("[Screen] Raised and stored.");
    }

    public boolean isLowered() { return lowered; }
}

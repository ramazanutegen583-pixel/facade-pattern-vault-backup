package edu.aitu.hometheater.subsystem;

/** Models a popcorn maker with a power prerequisite for preparing popcorn. */
public final class PopcornMaker {
    private boolean on;

    public void turnOn() {
        on = true;
        System.out.println("[Popcorn] Maker ON.");
    }

    public void pop() {
        if (!on) throw new IllegalStateException("Turn on the popcorn maker first.");
        System.out.println("[Popcorn] Fresh popcorn is ready.");
    }

    public void turnOff() {
        on = false;
        System.out.println("[Popcorn] Maker OFF.");
    }

    public boolean isOn() { return on; }
}

package edu.aitu.hometheater.subsystem;

/** Controls the power, volume, and sound mode of the simulated amplifier. */
public final class Amplifier {
    private boolean on;
    private int volume;
    private boolean surroundSound;

    public void turnOn() {
        on = true;
        System.out.println("[Amplifier] Power ON.");
    }

    public void setVolume(int volume) {
        if (volume < 0 || volume > 100) {
            throw new IllegalArgumentException("Volume must be between 0 and 100.");
        }
        requirePower();
        this.volume = volume;
        System.out.println("[Amplifier] Volume set to " + volume + "%.");
    }

    public void enableSurroundSound() {
        requirePower();
        surroundSound = true;
        System.out.println("[Amplifier] Surround sound enabled.");
    }

    public void turnOff() {
        on = false;
        volume = 0;
        surroundSound = false;
        System.out.println("[Amplifier] Power OFF.");
    }

    public boolean isOn() { return on; }
    public int getVolume() { return volume; }
    public boolean isSurroundSoundEnabled() { return surroundSound; }

    private void requirePower() {
        if (!on) throw new IllegalStateException("Turn on the amplifier first.");
    }
}

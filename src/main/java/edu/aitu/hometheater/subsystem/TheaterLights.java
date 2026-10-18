package edu.aitu.hometheater.subsystem;

/** Models adjustable room lighting. */
public final class TheaterLights {
    private int brightness = 100;

    public void dim(int brightness) {
        if (brightness < 0 || brightness > 100) {
            throw new IllegalArgumentException("Brightness must be between 0 and 100.");
        }
        this.brightness = brightness;
        System.out.println("[Lights] Brightness set to " + brightness + "%.");
    }

    public void turnOn() {
        brightness = 100;
        System.out.println("[Lights] Full room lighting restored (100%).");
    }

    public int getBrightness() { return brightness; }
}

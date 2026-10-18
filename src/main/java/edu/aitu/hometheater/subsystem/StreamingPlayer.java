package edu.aitu.hometheater.subsystem;

/** Models playback independently of the other theater devices. */
public final class StreamingPlayer {
    private boolean on;
    private String movie;
    private boolean paused;

    public void turnOn() {
        on = true;
        System.out.println("[Player] Power ON.");
    }

    public void play(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Movie title must not be blank.");
        }
        if (!on) throw new IllegalStateException("Turn on the player first.");
        if (movie != null) throw new IllegalStateException("Stop the current movie first.");
        movie = title.strip();
        paused = false;
        System.out.println("[Player] Playing: " + movie + ".");
    }

    public void pause() {
        requireMovie();
        if (paused) throw new IllegalStateException("The movie is already paused.");
        paused = true;
        System.out.println("[Player] Paused: " + movie + ".");
    }

    public void resume() {
        requireMovie();
        if (!paused) throw new IllegalStateException("The movie is not paused.");
        paused = false;
        System.out.println("[Player] Resumed: " + movie + ".");
    }

    public void stop() {
        requireMovie();
        System.out.println("[Player] Stopped: " + movie + ".");
        movie = null;
        paused = false;
    }

    public void turnOff() {
        on = false;
        movie = null;
        paused = false;
        System.out.println("[Player] Power OFF.");
    }

    public boolean isOn() { return on; }
    public boolean isPlaying() { return movie != null && !paused; }
    public boolean isPaused() { return paused; }

    private void requireMovie() {
        if (movie == null) throw new IllegalStateException("No movie is loaded.");
    }
}

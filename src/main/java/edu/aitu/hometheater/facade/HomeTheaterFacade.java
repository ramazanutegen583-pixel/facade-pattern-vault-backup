package edu.aitu.hometheater.facade;

import edu.aitu.hometheater.subsystem.Amplifier;
import edu.aitu.hometheater.subsystem.PopcornMaker;
import edu.aitu.hometheater.subsystem.Projector;
import edu.aitu.hometheater.subsystem.Screen;
import edu.aitu.hometheater.subsystem.StreamingPlayer;
import edu.aitu.hometheater.subsystem.TheaterLights;

/** Coordinates six independent devices behind a small movie-session API. */
public final class HomeTheaterFacade {
    private final Amplifier amplifier = new Amplifier();
    private final StreamingPlayer player = new StreamingPlayer();
    private final Projector projector = new Projector();
    private final Screen screen = new Screen();
    private final TheaterLights lights = new TheaterLights();
    private final PopcornMaker popcornMaker = new PopcornMaker();
    private String currentMovie;
    private boolean paused;

    public void watchMovie(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Movie title must not be blank.");
        }
        if (currentMovie != null) {
            throw new IllegalStateException("End the current movie session before starting another.");
        }
        String movie = title.strip();
        System.out.println("[Facade] Preparing the theater for " + movie + ".");
        popcornMaker.turnOn();
        popcornMaker.pop();
        lights.dim(10);
        screen.lower();
        projector.turnOn();
        projector.enableWideScreen();
        amplifier.turnOn();
        amplifier.setVolume(35);
        amplifier.enableSurroundSound();
        player.turnOn();
        player.play(movie);
        currentMovie = movie;
        paused = false;
        System.out.println("[Facade] Theater ready. Enjoy the movie!");
    }

    public void pauseMovie() {
        requireSession();
        if (paused) throw new IllegalStateException("The movie is already paused.");
        System.out.println("[Facade] Pausing the movie and lighting the room for a break.");
        player.pause();
        lights.dim(50);
        paused = true;
    }

    public void resumeMovie() {
        requireSession();
        if (!paused) throw new IllegalStateException("The movie is not paused.");
        System.out.println("[Facade] Restoring movie lighting and resuming playback.");
        lights.dim(10);
        player.resume();
        paused = false;
    }

    public void endMovie() {
        requireSession();
        System.out.println("[Facade] Ending the session and shutting down the theater.");
        player.stop();
        player.turnOff();
        amplifier.turnOff();
        projector.turnOff();
        screen.raise();
        lights.turnOn();
        popcornMaker.turnOff();
        currentMovie = null;
        paused = false;
        System.out.println("[Facade] Session finished. Theater is ready for the next movie.");
    }

    public String getStatus() {
        if (currentMovie == null) return "Ready";
        return (paused ? "Paused: " : "Playing: ") + currentMovie;
    }

    private void requireSession() {
        if (currentMovie == null) throw new IllegalStateException("No movie session is active.");
    }
}

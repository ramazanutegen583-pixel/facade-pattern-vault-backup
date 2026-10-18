package edu.aitu.hometheater;

import edu.aitu.hometheater.client.HomeTheaterDemo;
import edu.aitu.hometheater.facade.HomeTheaterFacade;
import edu.aitu.hometheater.subsystem.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/** Checks device states, actual console output, and safe handling of invalid requests. */
class HomeTheaterTest {
    private ByteArrayOutputStream output;
    private PrintStream originalOut;

    @BeforeEach
    void captureOutput() {
        originalOut = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreOutput() { System.setOut(originalOut); }

    @Test
    void completeSessionReturnsToReady() {
        HomeTheaterFacade theater = new HomeTheaterFacade();
        assertEquals("Ready", theater.getStatus());
        theater.watchMovie("Interstellar");
        assertEquals("Playing: Interstellar", theater.getStatus());
        theater.pauseMovie();
        assertEquals("Paused: Interstellar", theater.getStatus());
        theater.resumeMovie();
        assertEquals("Playing: Interstellar", theater.getStatus());
        theater.endMovie();
        assertEquals("Ready", theater.getStatus());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "\t"})
    void invalidTitleDoesNotActivateDevices(String title) {
        HomeTheaterFacade theater = new HomeTheaterFacade();
        assertThrows(IllegalArgumentException.class, () -> theater.watchMovie(title));
        assertEquals("Ready", theater.getStatus());
        assertEquals("", text());
    }

    @Test
    void titleIsTrimmed() {
        HomeTheaterFacade theater = new HomeTheaterFacade();
        theater.watchMovie("  Interstellar  ");
        assertEquals("Playing: Interstellar", theater.getStatus());
    }

    @Test
    void anotherMovieCannotReplaceAnActiveSession() {
        HomeTheaterFacade theater = new HomeTheaterFacade();
        theater.watchMovie("Interstellar");
        String before = text();
        assertThrows(IllegalStateException.class, () -> theater.watchMovie("The Martian"));
        assertEquals("Playing: Interstellar", theater.getStatus());
        assertEquals(before, text());
    }

    @ParameterizedTest
    @ValueSource(strings = {"pause", "resume", "end"})
    void idleOperationsAreRejectedWithoutDeviceChanges(String operation) {
        HomeTheaterFacade theater = new HomeTheaterFacade();
        assertThrows(IllegalStateException.class, () -> {
            switch (operation) {
                case "pause" -> theater.pauseMovie();
                case "resume" -> theater.resumeMovie();
                case "end" -> theater.endMovie();
            }
        });
        assertEquals("Ready", theater.getStatus());
        assertEquals("", text());
    }

    @Test
    void duplicatePauseAndPrematureResumePreserveState() {
        HomeTheaterFacade theater = new HomeTheaterFacade();
        theater.watchMovie("Interstellar");
        assertThrows(IllegalStateException.class, theater::resumeMovie);
        assertEquals("Playing: Interstellar", theater.getStatus());
        theater.pauseMovie();
        String before = text();
        assertThrows(IllegalStateException.class, theater::pauseMovie);
        assertEquals("Paused: Interstellar", theater.getStatus());
        assertEquals(before, text());
    }

    @Test
    void pausedSessionCanEndAndTheSameFacadeCanStartAnother() {
        HomeTheaterFacade theater = new HomeTheaterFacade();
        theater.watchMovie("Interstellar");
        theater.pauseMovie();
        theater.endMovie();
        assertEquals("Ready", theater.getStatus());
        theater.watchMovie("The Martian");
        assertEquals("Playing: The Martian", theater.getStatus());
        theater.endMovie();
        assertEquals("Ready", theater.getStatus());
    }

    @Test
    void startupOrderConfiguresEquipmentBeforePlayback() {
        new HomeTheaterFacade().watchMovie("Interstellar");
        assertOrdered("[Popcorn] Maker ON.", "[Popcorn] Fresh popcorn is ready.",
                "[Lights] Brightness set to 10%.", "[Screen] Lowered for the movie.",
                "[Projector] Power ON.", "[Projector] Widescreen mode enabled.",
                "[Amplifier] Power ON.", "[Amplifier] Volume set to 35%.",
                "[Amplifier] Surround sound enabled.", "[Player] Power ON.",
                "[Player] Playing: Interstellar.");
    }

    @Test
    void shutdownStopsPlaybackBeforeTurningDevicesOff() {
        HomeTheaterFacade theater = new HomeTheaterFacade();
        theater.watchMovie("Interstellar");
        output.reset();
        theater.endMovie();
        assertOrdered("[Player] Stopped: Interstellar.", "[Player] Power OFF.",
                "[Amplifier] Power OFF.", "[Projector] Power OFF.",
                "[Screen] Raised and stored.", "[Lights] Full room lighting restored (100%).",
                "[Popcorn] Maker OFF.");
    }

    @Test
    void pauseAndResumeCoordinateLightingWithPlayback() {
        HomeTheaterFacade theater = new HomeTheaterFacade();
        theater.watchMovie("Interstellar");
        output.reset();
        theater.pauseMovie();
        theater.resumeMovie();
        assertOrdered("[Player] Paused: Interstellar.", "[Lights] Brightness set to 50%.",
                "[Lights] Brightness set to 10%.", "[Player] Resumed: Interstellar.");
    }

    @Test
    void mainProducesTheCompleteEnglishDemoWithoutArguments() {
        HomeTheaterDemo.main(new String[0]);
        assertOrdered("HOME THEATER - FACADE PATTERN DEMONSTRATION", "1. CLIENT REQUEST:",
                "2. CLIENT REQUEST:", "3. CLIENT REQUEST:", "4. CLIENT REQUEST:",
                "5. A SECOND SESSION", "FINAL STATUS: Ready", "DEMONSTRATION COMPLETED SUCCESSFULLY");
        assertTrue(text().contains("[Player] Playing: Interstellar."));
        assertTrue(text().contains("[Player] Playing: The Martian."));
        assertFalse(text().contains("Exception"));
    }

    @Test
    void amplifierCanBeUsedDirectlyAndResetsOnPowerOff() {
        Amplifier amplifier = new Amplifier();
        assertThrows(IllegalStateException.class, () -> amplifier.setVolume(30));
        assertThrows(IllegalStateException.class, amplifier::enableSurroundSound);
        amplifier.turnOn();
        amplifier.setVolume(100);
        amplifier.enableSurroundSound();
        assertTrue(amplifier.isOn());
        assertEquals(100, amplifier.getVolume());
        assertTrue(amplifier.isSurroundSoundEnabled());
        amplifier.turnOff();
        assertFalse(amplifier.isOn());
        assertEquals(0, amplifier.getVolume());
        assertFalse(amplifier.isSurroundSoundEnabled());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 101})
    void invalidVolumeDoesNotChangeThePreviousSetting(int volume) {
        Amplifier amplifier = new Amplifier();
        amplifier.turnOn();
        amplifier.setVolume(35);
        assertThrows(IllegalArgumentException.class, () -> amplifier.setVolume(volume));
        assertEquals(35, amplifier.getVolume());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 50, 100})
    void lightsAcceptBoundaryValuesAndRestoreFullLighting(int brightness) {
        TheaterLights lights = new TheaterLights();
        lights.dim(brightness);
        assertEquals(brightness, lights.getBrightness());
        lights.turnOn();
        assertEquals(100, lights.getBrightness());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 101})
    void invalidBrightnessPreservesThePreviousSetting(int brightness) {
        TheaterLights lights = new TheaterLights();
        assertThrows(IllegalArgumentException.class, () -> lights.dim(brightness));
        assertEquals(100, lights.getBrightness());
    }

    @Test
    void projectorEnforcesPowerAndResetsItsDisplayMode() {
        Projector projector = new Projector();
        assertThrows(IllegalStateException.class, projector::enableWideScreen);
        projector.turnOn();
        projector.enableWideScreen();
        assertTrue(projector.isOn());
        assertTrue(projector.isWideScreen());
        projector.turnOff();
        assertFalse(projector.isOn());
        assertFalse(projector.isWideScreen());
    }

    @Test
    void screenCanBeControlledDirectly() {
        Screen screen = new Screen();
        assertFalse(screen.isLowered());
        screen.lower();
        assertTrue(screen.isLowered());
        screen.raise();
        assertFalse(screen.isLowered());
    }

    @Test
    void popcornMakerRequiresPower() {
        PopcornMaker maker = new PopcornMaker();
        assertThrows(IllegalStateException.class, maker::pop);
        maker.turnOn();
        maker.pop();
        assertTrue(maker.isOn());
        assertTrue(text().contains("Fresh popcorn is ready."));
        maker.turnOff();
        assertFalse(maker.isOn());
    }

    @Test
    void playerSupportsItsOwnLifecycleAndRejectsInvalidTransitions() {
        StreamingPlayer player = new StreamingPlayer();
        assertThrows(IllegalStateException.class, () -> player.play("Interstellar"));
        assertThrows(IllegalStateException.class, player::pause);
        assertThrows(IllegalStateException.class, player::resume);
        assertThrows(IllegalStateException.class, player::stop);
        player.turnOn();
        player.play("Interstellar");
        assertTrue(player.isPlaying());
        assertThrows(IllegalStateException.class, player::resume);
        assertThrows(IllegalStateException.class, () -> player.play("The Martian"));
        player.pause();
        assertTrue(player.isPaused());
        assertFalse(player.isPlaying());
        assertThrows(IllegalStateException.class, player::pause);
        player.resume();
        assertTrue(player.isPlaying());
        player.pause();
        player.turnOff();
        assertFalse(player.isOn());
        assertFalse(player.isPlaying());
        assertFalse(player.isPaused());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "\t"})
    void playerRejectsBlankMovieTitles(String title) {
        StreamingPlayer player = new StreamingPlayer();
        player.turnOn();
        assertThrows(IllegalArgumentException.class, () -> player.play(title));
        assertFalse(player.isPlaying());
    }

    @Test
    void subsystemSourcesDoNotReferenceTheFacade() throws Exception {
        try (var files = Files.list(Path.of("src/main/java/edu/aitu/hometheater/subsystem"))) {
            for (Path source : files.filter(path -> path.toString().endsWith(".java")).toList()) {
                String code = Files.readString(source);
                assertFalse(code.contains("HomeTheaterFacade"), source.toString());
                assertFalse(code.contains("edu.aitu.hometheater.facade"), source.toString());
            }
        }
    }

    @Test
    void clientDoesNotImportOrConstructDevices() throws Exception {
        String client = Files.readString(Path.of(
                "src/main/java/edu/aitu/hometheater/client/HomeTheaterDemo.java"));
        assertFalse(client.contains("hometheater.subsystem"));
        assertTrue(client.contains("new HomeTheaterFacade()"));
        for (String type : new String[]{"Amplifier", "StreamingPlayer", "Projector", "Screen", "TheaterLights", "PopcornMaker"}) {
            assertFalse(client.contains("new " + type + "("));
        }
    }

    private String text() { return output.toString(StandardCharsets.UTF_8); }

    private void assertOrdered(String... messages) {
        int previous = -1;
        for (String message : messages) {
            int position = text().indexOf(message, previous + 1);
            assertTrue(position > previous, "Missing or out-of-order output: " + message);
            previous = position;
        }
    }
}

package edu.aitu.hometheater.client;

import edu.aitu.hometheater.facade.HomeTheaterFacade;

/** The demonstration client uses the facade and never controls devices directly. */
public final class HomeTheaterDemo {
    private HomeTheaterDemo() { }

    public static void main(String[] args) {
        HomeTheaterFacade theater = new HomeTheaterFacade();
        System.out.println("============================================================");
        System.out.println("HOME THEATER - FACADE PATTERN DEMONSTRATION");
        System.out.println("One facade coordinates six independent theater devices.");
        System.out.println("============================================================");
        System.out.println("Initial status: " + theater.getStatus());

        System.out.println("\n1. CLIENT REQUEST: watchMovie(\"Interstellar\")");
        theater.watchMovie("Interstellar");
        System.out.println("STATUS: " + theater.getStatus());

        System.out.println("\n2. CLIENT REQUEST: pauseMovie()");
        theater.pauseMovie();
        System.out.println("STATUS: " + theater.getStatus());

        System.out.println("\n3. CLIENT REQUEST: resumeMovie()");
        theater.resumeMovie();
        System.out.println("STATUS: " + theater.getStatus());

        System.out.println("\n4. CLIENT REQUEST: endMovie()");
        theater.endMovie();
        System.out.println("STATUS: " + theater.getStatus());

        System.out.println("\n5. A SECOND SESSION WITH THE SAME FACADE");
        theater.watchMovie("The Martian");
        System.out.println("STATUS: " + theater.getStatus());
        theater.endMovie();

        System.out.println("\nFINAL STATUS: " + theater.getStatus());
        System.out.println("Both sessions finished; devices are off, the screen is raised, and lights are at 100%.");
        System.out.println("The client requested movie operations; the facade handled all device coordination.");
        System.out.println("DEMONSTRATION COMPLETED SUCCESSFULLY");
    }
}

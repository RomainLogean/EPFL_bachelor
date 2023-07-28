package ch.epfl.tchu.game;

import java.util.List;
import java.util.TreeSet;

import ch.epfl.tchu.Preconditions;

/**
 *
 * @author Romain Logean (327230)
 * @author Shuli Jia (316620)
 *
 */
public final class Ticket implements Comparable<Ticket> {

    private final List<Trip> trips;
    private final String text;

    /**
     * constructs a ticket with a list of trips
     * @param trips: list of trips
     * @throws IllegalArgumentException if the list of trips is empty
     */
    public Ticket(List<Trip> trips) {
        Preconditions.checkArgument(!trips.isEmpty());
        this.trips = List.copyOf(trips);
        text = computeText();
    }

    /**
     * constructs a Ticket with an only trip
     * @param from: the starting station
     * @param to: the arriving station
     * @param points: the number of points of the trip
     */
    public Ticket(Station from, Station to, int points) {
        this.trips = List.of(new Trip(from, to, points));
        text = computeText();
    }

    /**
     *
     * @return the text of the ticket
     */
    public String text() {
        return text;
    }

    /**
     * method to initialize the attribute text
     * @return the text of the ticket
     */
    private String computeText() {
        if(trips.size() == 1) {
            return (trips.get(0).from() + " - "
                    + trips.get(0).to() + " ("
                    + trips.get(0).points() + ")");

        } else {

            TreeSet<String> destinations = new TreeSet<>();
            for (Trip trip : trips) {
                destinations.add(trip.to().name() + " (" + trip.points() + ")");
            }

            return (trips.get(0).from().name()
                    + " - {" + String.join(", ", destinations) + "}");
        }
    }

    /**
     *
     * @param connectivity: connectivity of a player's network
     * @return the number of points of a ticket
     */
    public int points(StationConnectivity connectivity) {

        int points = trips.get(0).points(connectivity);
        for(Trip trip: trips) {
            if(trip.points(connectivity) >= points) {
                points = trip.points(connectivity);
            }
        }
        return points;
    }

    /**
     * @param that: another ticket
     * @return an integer which is negative, positive, or zero depending on the ticket's text
     */
    @Override
    public int compareTo(Ticket that) {
        return this.text.compareTo(that.text());
    }

    /**
     * @return the text of a ticket
     */
    @Override
    public String toString() {
        return text();
    }
    
}

package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;

/**
 *
 * @author Romain Logean (327230)
 * @author Shuli Jia (316620)
 *
 */
public final class Trip {

    private final Station from;
    private final Station to;
    private final int points;

    /**
     *
     * @param from: the departure station
     * @param to: the arrival station
     * @param points: the number of stations between both stations
     * @throws IllegalArgumentException if there aren't any points between the stations
     */
    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;

    }

    /**
     *
     * @param from: list of the departure stations
     * @param to: list of the arrival stations
     * @param points: the number of stations between both stations
     * @throws IllegalArgumentException if there aren't any points between the stations
     * @return a list of stations that have 'points' station between them
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        Preconditions.checkArgument(points > 0);
        Preconditions.checkArgument(!from.isEmpty() && ! to.isEmpty());

        List<Trip> all = new ArrayList<>();

        for(Station fromStation : from) {
            for(Station toStation : to){
                all.add(new Trip(fromStation, toStation, points));
            }
        }
        return all;
    }

    /**
     *
     * @return the station the player's from
     */
    public Station from() {
        return from;
    }

    /**
     *
     * @return the station the player wants to go to
     */
    public Station to() {
        return to;
    }

    /**
     *
     * @return the number of points between the two stations
     */
    public int points() {
        return points;
    }

    /**
     *
     * @param connectivity: connectivity of a player's network
     * @return the number of points that connect to stations (is negative if both stations aren't connected)
     */
    public int points(StationConnectivity connectivity) {
       return connectivity.connected(from,to)
                ? points()
                : -points();
    }
}

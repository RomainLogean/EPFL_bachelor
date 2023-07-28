package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Romain Logean (327230)
 *
 */

public final class Trail {
    //"Le trail vide devrait être stocké comme constante statique"
    private static final Trail NULL_TRAIL = new Trail(null,null,null);

    private final List<Route> routes;
    private final Station station1;
    private final Station station2;
    private int length;

    /**
     * Construct a new Trail between two Stations
     * @param station1 the starting station of the trail
     * @param station2 the arriving station of the trail
     * @param routes the list of routes that are part of the trail
     */
    private Trail(Station station1, Station station2, List<Route> routes){
        this.station1 = station1;
        this.station2 = station2;
        this.routes = routes;

        if(routes == null) {
            this.length = 0;
        }
        else {
            int length = 0;
            for(Route r : routes) {
                length += r.length();
            }
            this.length=length;
        }

    }

    /**
     * determine which is the longest road possible form a list a routes
     * @param routes the list of routes for which we search the longest
     * @return the longest trail possible with the list of routes
     */
    public static Trail longest(List<Route> routes) {
        if (routes.isEmpty()) {
            return NULL_TRAIL;
        }

        //creation of the list of all trail with one Route
        List<Trail> allTrails = new ArrayList<>();
        for (Route r : routes) {
            allTrails.add(new Trail(r.station1(), r.station2(), List.of(r)));
            allTrails.add(new Trail(r.station2(), r.station1(), List.of(r)));
        }

        //initialisation to use maxLength and longestTrail in the for
        int maxLength = 0;
        Trail longestTrail = allTrails.get(0);
        for (Trail at : allTrails) {
            if (at.length() >= maxLength) {
                maxLength = at.length();
                longestTrail = at;
            }
        }
        boolean changes;
        do{
            List<Trail> trailExtension = new ArrayList<>();
            changes = false;

            for (Trail at : allTrails) {
                for (Route r : routes) {
                    if (((r.station1() == at.station2() || r.station2() == at.station2())) && !at.routes.contains(r)) {

                        Station nextStation = r.stationOpposite(at.station2());

                        List<Route> extendedRoutes = new ArrayList<>(at.routes);
                        extendedRoutes.add(r);

                        Trail newTrail = new Trail(at.station1, nextStation, extendedRoutes);
                        trailExtension.add(newTrail);
                        if (newTrail.length() >= maxLength) {
                            maxLength = newTrail.length();
                            longestTrail = newTrail;
                        }

                        changes = true;
                    }
                }
            }
            allTrails = trailExtension;
        } while(changes);

        return longestTrail;
    }

    /**
     *
     * @return the length of a trail
     */
    public int length() {
        return length;
    }

    /**
     *
     * @return the first station a the trail
     */
    public Station station1() {
        return (length() == 0) ? null : station1;
    }

    /**
     *
     * @return the last station of the trail
     */
    public Station station2() {
        return (length() == 0) ? null : station2;
    }

    /**
     *
     * @return the string that is used to represent the trail
     */
    @Override
    public String toString() {
        if(routes == null) {
            return "Ce trajet est vide ! (0)";
        }
        List<String> routesName = new ArrayList<>();
        routesName.add(station1().name());
        for (Route r : routes) {
            if(routesName.contains(r.station1().name())) {
                routesName.add(r.station2().name());
            } else {
                routesName.add(r.station1().name());
            }
        }
        return (String.join(" - ", routesName) + " (" + this.length() + ")");
    }
}

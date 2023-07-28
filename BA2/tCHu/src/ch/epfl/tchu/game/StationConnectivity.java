package ch.epfl.tchu.game;

/**
 *
 * @author Romain Logean (327230)
 * @author Shuli Jia (316620)
 *
 */
public interface StationConnectivity {

    /**
     *
     * @param s1: the station from
     * @param s2: the station to
     * @return true if the two stations are connected
     */
    boolean connected(Station s1, Station s2);

}

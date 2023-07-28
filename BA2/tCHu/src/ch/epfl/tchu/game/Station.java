package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Objects;

/**
 *
 * @author Romain Logean (327230)
 * @author Shuli Jia (316620)
 *
 */
public final class Station {

    private final int id;
    private final String name;

    /**
     *
     * @param id: number that identifies the station
     * @param name: name of the station
     * @throws IllegalArgumentException if the id number is negative
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);
        Objects.requireNonNull(name);
        this.id = id;
        this.name = name;

    }

    /**
     *
     * @return the id of the station
     */
    public int id() {
        return id;
    }

    /**
     *
     * @return the name of the station
     */
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }


}

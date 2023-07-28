package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 *
 * @author Romain Logean (327230)
 * @author Shuli JIA (316620)
 *
 */
public final class StationPartition implements StationConnectivity {

    private final Integer[] links;

    /**
     * private constructor
     * @param links: list of integers that contains the links
     *               between the elements and the subsets
     */
    private StationPartition(Integer[] links) {
        this.links = links;
    }


    @Override
    public boolean connected(Station s1, Station s2) {
        return (links.length <= s1.id() || links.length <= s2.id())
                ? s1.id() == s2.id()
                : links[s1.id()].equals(links[s2.id()]);
    }

    /**
     * represents a builder
     * @author Romain Logean (327230)
     * @author Shuli JIA (316620)
     *
     */
    public static final class Builder {

        private final Integer[] links;
        /**
         * constructs a set of stations that have an identity
         * included between 0 and station count
         * @param stationCount: number of stations
         * @throws IllegalArgumentException if the count of stations is negative
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            this.links = new Integer[stationCount];
            for(int i = 0; i < stationCount; ++i) {
                this.links[i] = i;
            }
        }

        /**
         * connect the subsets that contain the two stations
         * @param s1: station 1
         * @param s2: station 2
         * @return the builder (this)
         */
        public Builder connect(Station s1, Station s2) {
            links[representative(s1.id())] = representative(s2.id());
            return this;
        }

        /**
         *
         * @return the "flattened" partition of the stations
         */
        public StationPartition build() {
            for(int i = 0; i < links.length; ++i) {
                links[i] = representative(i);
            }
            return new StationPartition(links);
        }

        /**
         *
         * @param id the number of the Station
         * @return the representative of the Station with giver id
         */
        private int representative(int id) {
            int rep = id;
            while (links[rep] != rep) {
                rep = links[rep];
            }
            return rep;
        }
    }
}

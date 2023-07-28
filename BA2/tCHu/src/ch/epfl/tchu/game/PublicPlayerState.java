package ch.epfl.tchu.game;

import java.util.List;

import ch.epfl.tchu.Preconditions;

/**
 *
 * @author Shuli JIA (316620)
 *
 */
public class PublicPlayerState {

    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;

    private final int claimPoints;
    private final int carCount;

    /**
     *
     * @param ticketCount: number of tickets the player has
     * @param cardCount: number of cards the player has
     * @param routes: list of all the routes the player claimed
     * @throws IllegalArgumentException if the number of tickets or the number of cards is negative
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument((ticketCount >= 0) && (cardCount >= 0));
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = List.copyOf(routes);

        int claimPoints = 0;
        for(Route r: routes) {
            int length = r.length();
            claimPoints += Constants.ROUTE_CLAIM_POINTS.get(length);
        }
        this.claimPoints = claimPoints;

        int length = 0;
        for(Route r: routes){
            length += r.length();
        }
        this.carCount = Constants.INITIAL_CAR_COUNT - length;

    }

    /**
     *
     * @return the number of tickets the player has
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     *
     * @return the number of cards the player has
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     *
     * @return the list of routes the player has
     */
    public List<Route> routes() {
        return routes;
    }

    /**
     *
     * @return the number of cars
     */
    public int carCount() {
        return carCount;
    }

    /**
     *
     * @return the claim points the player gained
     */
    public int claimPoints() {
        return claimPoints;
    }

}

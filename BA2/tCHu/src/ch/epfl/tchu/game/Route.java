package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import static ch.epfl.tchu.game.Card.LOCOMOTIVE;
import static ch.epfl.tchu.game.Card.CARS;
import static ch.epfl.tchu.game.Constants.ADDITIONAL_TUNNEL_CARDS;
import static ch.epfl.tchu.game.Constants.MAX_ROUTE_LENGTH;
import static ch.epfl.tchu.game.Constants.MIN_ROUTE_LENGTH;
import static ch.epfl.tchu.game.Constants.ROUTE_CLAIM_POINTS;


/**
 *
 * @author Shuli Jia (316620)
 *
 */
public final class Route {

    public enum Level {
        OVERGROUND,
        UNDERGROUND
    }

    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;

    /**
     * constructs a route
     * @param id: the identity
     * @param station1: the departure station
     * @param station2: the arrival station
     * @param length: the length of the route (number of stations on the route)
     * @param level: determines if it is a route on the surface or a tunnel
     * @param color: the color of the route
     * @throws IllegalArgumentException
     *          - if the station1 is equal to the station2
     *          - if the length of the route is not included between the minimum and maximum route length
     * @throws NullPointerException
     *          - if the id, the station1, the station2 or the level of the route is null
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument(!(station1.equals(station2)));
        Preconditions.checkArgument((length >= MIN_ROUTE_LENGTH) && (length <= MAX_ROUTE_LENGTH));

        Objects.requireNonNull(id);
        Objects.requireNonNull(station1);
        Objects.requireNonNull(station2);
        Objects.requireNonNull(level);

        this.id = id;
        this.station1 = station1;
        this.station2 = station2;
        this.length = length;
        this.level = level;
        this.color = color;

    }

    /**
     *
     * @return the identity
     */
    public String id() {
        return id;
    }

    /**
     *
     * @return the departure station
     */
    public Station station1() {
        return station1;
    }

    /**
     *
     * @return the arrival station
     */
    public Station station2() {
        return station2;
    }

    /**
     *
     * @return the length of the route
     */
    public int length() {
        return length;
    }

    /**
     *
     * @return the level of the route
     */
    public Level level() {
        return level;
    }

    /**
     *
     * @return the color of the route (is neutral if there isn't any color)
     */
    public Color color() {
        return color;
    }

    /**
     *
     * @return the list of the two stations of the route in the order in which we put them in the constructor
     */
    public List<Station> stations() {
        List<Station> stations = new ArrayList<>();
        stations.add(station1);
        stations.add(station2);
        return stations;
    }

    /**
     *
     * @param station: one of the two stations
     * @return the other station
     * @throws IllegalArgumentException if the given station is equal to none of the two stations
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(station.equals(station1) || (station.equals(station2)));
        return (station.equals(station1)) ? station2 : station1;
    }

    /**
     *
     * @return the list of the cards the player can play to seize the route
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        List<SortedBag<Card>> possibleClaimCards = new ArrayList<>();

        if(color == null) {
            for(Card card: CARS) {
                possibleClaimCards.add(SortedBag.of(length, card));
            }
        } else {
            possibleClaimCards.add(SortedBag.of(length, Card.of(color)));
        }

        if(level.equals(Level.UNDERGROUND)) {
            for(int i = 1; i < length; ++i) {
                if(color == null) {
                    for(Card card: CARS) {
                        possibleClaimCards.add(SortedBag.of(length - i, card, i, LOCOMOTIVE));
                    }

                } else {
                    possibleClaimCards.add(SortedBag.of(length - i, Card.of(color), i, LOCOMOTIVE));
                }
            }

            possibleClaimCards.add(SortedBag.of(length, LOCOMOTIVE));

        }

        return possibleClaimCards;
    }

    /**
     *
     * @param claimCards: the cards that the player initially played
     * @param drawnCards: the cards that the player drew
     * @return the number of cards to add to seize the route
     * @throws IllegalArgumentException
     *          - if the size of drawn cards is not equal to additional tunnel cards
     *          - if the route is not underground
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument((drawnCards.size() == ADDITIONAL_TUNNEL_CARDS) || this.level.equals(Level.UNDERGROUND));

        int numberPlayableCards = 0;

        for(Card card: CARS) {
            if(claimCards.contains(card)) {
                numberPlayableCards = drawnCards.countOf(card) + drawnCards.countOf(LOCOMOTIVE);
            }
        }

        if(claimCards.size() == claimCards.countOf(LOCOMOTIVE)) {
            numberPlayableCards = drawnCards.countOf(LOCOMOTIVE);
        }

        return numberPlayableCards;
    }

    /**
     *
     * @return the number of construction points a player gets when they seize the route
     */
    public int claimPoints() {
        return ROUTE_CLAIM_POINTS.get(length);
    }

}

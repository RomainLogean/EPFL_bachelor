package ch.epfl.tchu.game;

import static ch.epfl.tchu.game.Card.LOCOMOTIVE;
import static ch.epfl.tchu.game.Constants.INITIAL_CARDS_COUNT;
import static ch.epfl.tchu.game.Constants.ADDITIONAL_TUNNEL_CARDS;

import java.util.*;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * @author Shuli JIA (316620)
 */
public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;

    /**
     * Constructs the state of the player
     * @param routes: the list of all the routes that the player claimed
     * @param tickets: all the tickets the player has
     * @param cards: all the cards the player has
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
    }

    /**
     *
     * @param initialCards: set of initial cards the player has
     * @return the initial state of the player
     * @throws IllegalArgumentException if the size of the set of initial cards
     *         is not equal to 4
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == INITIAL_CARDS_COUNT);

        // the player doesn't have any ticket at the beginning of the game
        SortedBag<Ticket> tickets = SortedBag.of();
        // the player didn't claim any route at the beginning
        List<Route> routes = new LinkedList<>();

        return new PlayerState(tickets, initialCards, routes);

    }

    /**
     *
     * @return the set of tickets of the player
     */
    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    /**
     *
     * @param newTickets: other tickets that are added to the set of tickets of the player
     * @return the state of the player when these tickets are added to their tickets
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        SortedBag<Ticket> withAddedTickets = this.tickets().union(newTickets);
        return new PlayerState(withAddedTickets, this.cards(), this.routes());
    }

    /**
     *
     * @return the cards of the player
     */
    public SortedBag<Card> cards() {
        return cards;
    }

    /**
     *
     * @param card: the card to be added to the set of cards of the player
     * @return the state of the player when this card is added to their cards
     */
    public PlayerState withAddedCard(Card card) {
        SortedBag<Card> withAddedCard = this.cards.union(SortedBag.of(card));
        return new PlayerState(this.tickets(), withAddedCard, this.routes());
    }

    /**
     *
     * @param route: the route the player wants to claim
     * @return true if the player can claim this route
     */
    public boolean canClaimRoute(Route route) {
        return (route.length() <= this.carCount() && this.possibleClaimCards(route).size() != 0);
    }

    /**
     *
     * @param route: the route the player wants to claim
     * @return the set of cards that the player can play to claim the route
     * @throws IllegalArgumentException if the number of cars is smaller than the route length
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(this.carCount() >= route.length());

        List<SortedBag<Card>> allPossibleClaimCards = route.possibleClaimCards();
        List<SortedBag<Card>> possibleClaimCards = new ArrayList<>();

        for (SortedBag<Card> allPossibleClaimCard : allPossibleClaimCards) {
            if(cards.contains(allPossibleClaimCard)) {
                possibleClaimCards.add(allPossibleClaimCard);
            }
        }

        return possibleClaimCards;

    }

    /**
     *
     * @param additionalCardsCount: number of additional cards
     * @param initialCards: the first cards that the player played
     * @return all the cards that the player can play to claim the underground route
     * @throws IllegalArgumentException if
     *          - the number of additional cards is negative or is greater than the number of additional tunnel cards
     *          - the set of initial cards is empty or if its multiplicity is smaller than 2
     *          - the number of drawn cards is not equal to the additional tunnel cards
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount,
                                                         SortedBag<Card> initialCards) {

        Preconditions.checkArgument(additionalCardsCount > 0 && additionalCardsCount <= ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(!(initialCards.isEmpty()) && initialCards.toMap().size() <= 2);

        SortedBag<Card> otherCards = cards.difference(initialCards);
        List<Card> otherCardsList = new LinkedList<>();
        Card wantedCard = LOCOMOTIVE;
        
        for(Card initialCard: initialCards) {
            if (initialCard.color() != null) {
                wantedCard = Card.of(initialCard.color());
            }
        }

        for(Card card : otherCards) {
            if (card.equals(wantedCard) || card.equals(LOCOMOTIVE)) {
                otherCardsList.add(card);
            }
        }

        SortedBag<Card> playableCards = SortedBag.of(otherCardsList);
        if(playableCards.size() < additionalCardsCount){
            return List.of();
        }
        Set<SortedBag<Card>> setOfPlayableCards = playableCards.subsetsOfSize(additionalCardsCount);
        List<SortedBag<Card>> possibleAdditionalCards = new LinkedList<>(setOfPlayableCards);

        possibleAdditionalCards.sort(Comparator.comparingInt(cs -> cs.countOf(LOCOMOTIVE)));

        return possibleAdditionalCards;

    }

    /**
     *
     * @param route: the route that the player just claimed
     * @param claimCards: the cards used to claim the route
     * @return the state of the player when they claimed another route
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {

        List<Route> updatedRoutes = new LinkedList<>(routes());
        updatedRoutes.add(route);

        SortedBag<Card> updatedCards = this.cards.difference(claimCards);

        return new PlayerState(this.tickets(), updatedCards, updatedRoutes);
    }

    /**
     *
     * @return the points gained with the tickets of the player
     */
    public int ticketPoints() {

        int maxId = 0;
        int ticketPoints = 0;

        for(Route route: routes()) {
            List<Station> stations = route.stations();

            for(Station station: stations) {
                if(maxId < station.id()) {
                    maxId = station.id();

                }
            }
        }

        StationPartition.Builder partitionBuilder = new StationPartition.Builder(maxId + 1);
        for(Route route: routes()) {
            partitionBuilder.connect(route.station1(), route.station2());
        }
        StationPartition allConnections = partitionBuilder.build();

        for(Ticket t: tickets) {
            ticketPoints += t.points(allConnections);
        }

        return ticketPoints;
    }

    /**
     *
     * @return the final points of the player
     */
    public int finalPoints() {
        return ticketPoints() + claimPoints();
    }
}

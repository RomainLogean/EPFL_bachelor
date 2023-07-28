package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import static ch.epfl.tchu.game.Constants.INITIAL_CARDS_COUNT;

import java.util.*;

/**
 * @author Romain Logean (327230)
 */
public final class GameState extends PublicGameState {

    private final Map<PlayerId, PlayerState> playerState;
    private final Deck<Ticket> tickets;
    private final CardState cardState;

    /**
     * @param tickets: list of tickets in game
     * @param ticketsCount: number of tickets
     * @param cardState: state of the cards
     * @param currentPlayerId: the id of the current player
     * @param playerState: map of the players and the id
     * @param lastPlayer: the id of the last player
     */
    private GameState(Deck<Ticket> tickets, int ticketsCount, CardState cardState, PlayerId currentPlayerId,
                      Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(ticketsCount, cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);
        this.playerState = playerState;
        this.cardState = cardState;
        this.tickets = tickets;
    }

    /**
     *
     * @param tickets: initial tickets of the game
     * @param rng: a random variable used to choose the first player and shuffle the cards
     * @return an instance of GameState of the beginning of a new game
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {

        Deck<Card> allCards = Deck.of(Constants.ALL_CARDS, rng);

        Map<PlayerId, PlayerState> playerStateMap = new TreeMap<>();
        for (PlayerId Id : PlayerId.ALL) {
            playerStateMap.put(Id,
                    PlayerState.initial(allCards.withoutTopCards(PlayerId.ALL.indexOf(Id)*INITIAL_CARDS_COUNT).topCards(INITIAL_CARDS_COUNT)));
        }

        Deck<Card> statingDeck = allCards.withoutTopCards(INITIAL_CARDS_COUNT*PlayerId.COUNT);
        PlayerId firstPlayer = PlayerId.ALL.get(rng.nextInt(PlayerId.COUNT));

        Deck<Ticket> ticketDeck = Deck.of(tickets, rng);
        int ticketDeckSize = ticketDeck.size();

        CardState initialCardState = CardState.of(statingDeck);

        return new GameState(ticketDeck, ticketDeckSize, initialCardState, firstPlayer, playerStateMap, null);
    }

    /**
     *
     * @param playerId: the id of the player
     * @return the state of the player (including private parts)
     */
    @Override
    public PlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     *
     *  @return the state of the current player (including private parts)
     */
    @Override
    public PlayerState currentPlayerState() {
        return playerState.get(currentPlayerId());
    }

    /**
     *
     * @param count: the number of tickets we want
     * @return the first count tickets on top of the tickets' deck
     * @throws IllegalArgumentException if the number "count" is not included between
     *         0 and the number of tickets
     */
    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= ticketsCount());
        return tickets.topCards(count);
    }

    /**
     *
     * @param count: the number of tickets we want to remove from the ticket's deck
     * @return the actual GameState without the "count" number of top tickets
     * @throws IllegalArgumentException if the number "count" is not included between
     *         0 and the number of tickets
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= ticketsCount());

        Deck<Ticket> newTicketDeck = tickets.withoutTopCards(count);
        int newTicketDeckSize = newTicketDeck.size();

        return new GameState(newTicketDeck, newTicketDeckSize, cardState, currentPlayerId(), playerState, lastPlayer());
    }

    /**
     *
     * @return the first card of the deck
     * @throws IllegalArgumentException if the deck of the card state is empty
     */
    public Card topCard() {
        Preconditions.checkArgument(!cardState().isDeckEmpty());
        return cardState.topDeckCard();
    }

    /**
     *
     * @return the actual GameState without the top card of the deck
     * @throws IllegalArgumentException if the deck of the card state is empty
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(!cardState.isDeckEmpty());

        CardState withoutTopDeckCardState = cardState.withoutTopDeckCard();

        return new GameState(tickets, ticketsCount(), withoutTopDeckCardState,
                currentPlayerId(), playerState, lastPlayer());
    }

    /**
     *
     * @param discardedCards: the cards that are to become discards
     * @return the actual GameState once the given list of card is added to the set of discards
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {

        CardState withMoreDiscardedCardsState = cardState.withMoreDiscardedCards(discardedCards);

        return new GameState(tickets, ticketsCount(), withMoreDiscardedCardsState,
                currentPlayerId(), playerState, lastPlayer());
    }

    /**
     *
     * @param rng: a random number generator
     * @return the actual GameState once a new deck of cards is recreated from the discards if the deck is empty
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {

        if(cardState.isDeckEmpty()) {
            CardState withDeckRecreatedIfNeededState = cardState.withDeckRecreatedFromDiscards(rng);
            return new GameState(tickets,ticketsCount(), withDeckRecreatedIfNeededState, currentPlayerId(),
                    playerState, lastPlayer());
        }

        return this;
    }

    /**
     *
     * @param playerId: the id of the player
     * @param chosenTickets: list of tickets that the player chose to keep
     * @return the actual GameState once the player has new tickets
     * @throws IllegalArgumentException if the player's number of tickets is not 0
     *                                  (if the player still has tickets)
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState.get(playerId).tickets().size() == 0);
        Map<PlayerId,PlayerState> newPlayerState = new TreeMap<>(playerState);
        PlayerState WithMoreTickets = playerState.get(playerId).withAddedTickets(chosenTickets);
        newPlayerState.put(playerId, WithMoreTickets);
        return new GameState(tickets, ticketsCount(), cardState, currentPlayerId(), newPlayerState, lastPlayer());
    }

    /**
     *
     * @param drawnTickets: list of the drawn tickets
     * @param chosenTickets: list of the tickets that the player wants to keep
     * @return the actual GameState once the current player draws and keeps new tickets
     * @throws IllegalArgumentException if the set of drawn tickets doesn't contain
     *         any of the chosen tickets
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        Map<PlayerId,PlayerState> newPlayerState = new TreeMap<>(playerState);
        newPlayerState.put(currentPlayerId(), playerState.get(currentPlayerId()).withAddedTickets(chosenTickets));
        Deck<Ticket> newTickets = tickets.withoutTopCards(drawnTickets.size());

        return new GameState(newTickets, newTickets.size(), cardState, currentPlayerId(), newPlayerState, lastPlayer());
    }

    /**
     *
     * @param slot: the slot of the chosen card
     * @return the actual GameState once the current player took a card in his hand
     * @throws IllegalArgumentException if the player can't draw the cards
     */
    public GameState withDrawnFaceUpCard(int slot) {
        Map<PlayerId, PlayerState> newPlayerState = new TreeMap<>(playerState);
        newPlayerState.put(currentPlayerId(), newPlayerState.get(currentPlayerId()).withAddedCard(cardState.faceUpCard(slot)));
        return new GameState(tickets, ticketsCount(), cardState.withDrawnFaceUpCard(slot), currentPlayerId(), newPlayerState, lastPlayer());
    }

    /**
     *
     * @return the actual GameState once the player took the top card of the deck
     * @throws IllegalArgumentException if the player can't draw the cards
     */
    public GameState withBlindlyDrawnCard() {
        Map<PlayerId, PlayerState> newPlayerState = new TreeMap<>(playerState);
        PlayerState WithMoreCards = playerState.get(currentPlayerId()).withAddedCard(cardState.topDeckCard());
        newPlayerState.put(currentPlayerId(), WithMoreCards);
        return new GameState(tickets, ticketsCount(), cardState.withoutTopDeckCard(), currentPlayerId(), newPlayerState, lastPlayer());
    }

    /**
     *
     * @param route: the route that is claimed
     * @param cards: the list of cards used to claim the route
     * @return the actual GameState once the current player has claimed a new route and discard the given list of card
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards)  {
        Map<PlayerId, PlayerState> newPlayerState = new TreeMap<>(playerState);
        PlayerState WithClaimedRoute = newPlayerState.get(currentPlayerId()).withClaimedRoute(route, cards);
        newPlayerState.put(currentPlayerId(), WithClaimedRoute);
        CardState newCardState = cardState.withMoreDiscardedCards(cards);
        return new GameState(tickets, ticketsCount(), newCardState, currentPlayerId(), newPlayerState, lastPlayer());
    }

    /**
     *
     * @return true if the last turn begins
     */
    public boolean lastTurnBegins(){
        int wagon = playerState.get(currentPlayerId()).carCount();
        return (lastPlayer() == null && wagon <= 2);
    }

    /**
     *
     * @return the actual GameState once the playing player is the player after the actual CurrentPlayer
     */
    public GameState forNextTurn() {
        if(lastTurnBegins()) {
            return new GameState(tickets, ticketsCount(), cardState, currentPlayerId().next(),
                    playerState, currentPlayerId());
        }
        return new GameState(tickets, ticketsCount(), cardState, currentPlayerId().next(), playerState, lastPlayer());
    }

}
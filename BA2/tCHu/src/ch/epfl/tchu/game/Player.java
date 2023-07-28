package ch.epfl.tchu.game;

import java.util.List;
import java.util.Map;

import ch.epfl.tchu.SortedBag;

/**
 *
 * @author Shuli JIA (316620)
 *
 */
public interface Player {

    /**
     * tell the player his id and all of the other's players id
     * @param ownId: id of the player
     * @param playerNames: names of all players
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * information that is communicated to the player
     * @param info: the information that is to be communicated
     */
    void receiveInfo(String info);

    /**
     * update the new state of the player
     * @param newState: the new state of the game to be returned
     * @param ownState: the own state of the player
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * communicate tickets given to the player at the beginning of the game
     * @param tickets: 5 initial tickets given to the player
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);


    void end(String endingWindowString);

    /**
     *
     * @return the tickets the player decided to keep
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     *
     * @return which action the player wants to do before his turn
     */
    TurnKind nextTurn();

    /**
     *
     * @param options: tickets among which the player can choose
     * @return tickets the player wants to keep
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     *
     * @return the slot of the card the player wants to draw
     */
    int drawSlot();

    /**
     *
     * @return the route the player tried to claim
     */
    Route claimedRoute();

    /**
     *
     * @return the cards the player wants to play to claim route
     */
    SortedBag<Card> initialClaimCards();

    /**
     *
     * @param options: additional cards among which the player can choose
     * @return the cards the player chose to claim a route
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);



    /**
     * Enum of the 3 types of actions the player can do
     * @author Shuli JIA (316620)
     */
    enum TurnKind {

        DRAW_TICKETS,
        DRAW_CARDS,
        CLAIM_ROUTE;

        public static final List<TurnKind> ALL = List.of(TurnKind.values());

    }
}

package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;

/**
 * @author Romain Logean (327230)
 * @author Shuli Jia (316620)
 */
public class GraphicalPlayerAdapter implements Player {

    private GraphicalPlayer graphicalPlayer;
    private final BlockingQueue<SortedBag<Ticket>> ticketsQueue;
    private final BlockingQueue<Integer> cardQueue;
    private final BlockingQueue<Route> routeQueue;
    private final BlockingQueue<SortedBag<Card>> claimedCardQueue;

    /**
     * Constructor of the class
     */
    public GraphicalPlayerAdapter() {
        ticketsQueue = new ArrayBlockingQueue<>(1);
        cardQueue = new ArrayBlockingQueue<>(1);
        routeQueue = new ArrayBlockingQueue<>(1);
        claimedCardQueue = new ArrayBlockingQueue<>(1);
    }

    /**
     * tell the player his id and all of the other's players id
     * @param ownId: id of the player
     * @param playerNames: names of all players
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
    }

    /**
     * information that is communicated to the player
     * @param info: the information that is to be communicated
     */
    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    /**
     * update the new state of the player
     * @param newState: the new state of the game to be returned
     * @param ownState: the own state of the player
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    /**
     * communicate tickets given to the player at the beginning of the game
     * @param tickets: 5 initial tickets given to the player
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(() -> graphicalPlayer.chooseTickets(tickets,(initialTickets ->
                new Thread(() -> ticketsQueue.add(initialTickets)).start())));
    }

    @Override
    public void end(String endingWindow) {
        runLater(()-> graphicalPlayer.end(endingWindow));
    }

    /**
     *
     * @return the tickets the player decided to keep
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {

        try{
            return ticketsQueue.take();
        }
        catch(InterruptedException e){
            throw new Error();
        }
    }

    /**
     *
     * @return which action the player wants to do before his turn
     */
    @Override
    public TurnKind nextTurn() {
        BlockingQueue<TurnKind> turnKindQueue = new ArrayBlockingQueue<>(1);
            runLater(() -> graphicalPlayer.startTurn(() ->
                new Thread(() -> turnKindQueue.add(TurnKind.DRAW_TICKETS)).start()
            , (slot) -> new Thread(() ->
            {
                cardQueue.add(slot);
                turnKindQueue.add(TurnKind.DRAW_CARDS);
            }).start(), (route, cards) ->
                            new Thread(() ->
            {
                routeQueue.add(route);
                claimedCardQueue.add(cards);
                turnKindQueue.add(TurnKind.CLAIM_ROUTE);
            }).start()));
            try {
                return turnKindQueue.take();
            }
            catch(InterruptedException e){
                throw new Error();
            }
    }

    /**
     *
     * @param options: tickets among which the player can choose
     * @return tickets the player wants to keep
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {

        runLater(() -> graphicalPlayer.chooseTickets(options, (tickets ->
                new Thread(() -> ticketsQueue.add(tickets)).start())));

        try{
            return ticketsQueue.take();
        }
        catch(InterruptedException e){
            throw new Error();
        }

    }

    /**
     *
     * @return the slot of the card the player wants to draw
     */
    @Override
    public int drawSlot() {
        try{
            if(cardQueue.isEmpty()){
                runLater(()->graphicalPlayer.drawCard((slot) ->
                        new Thread(() -> cardQueue.add(slot)).start()));
            }
            return cardQueue.take();
        }
        catch(InterruptedException e){
            throw new Error();
        }
    }

    /**
     *
     * @return the route the player tried to claim
     */
    @Override
    public Route claimedRoute() {
        try{
            return routeQueue.take();
        }
        catch(InterruptedException e){
            throw new Error();
        }
    }

    /**
     *
     * @return the cards the player wants to play to claim route
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        try{
            return claimedCardQueue.take();
        }
        catch(InterruptedException e){
            throw new Error();
        }
    }

    /**
     *
     * @param options: additional cards among which the player can choose
     * @return the cards the player chose to claim a route
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        runLater(() -> graphicalPlayer.chooseAdditionalCards(options, (cards) ->
                new Thread(() -> {
            if(cards != null){
            claimedCardQueue.add(cards);
            } else {
            claimedCardQueue.add(SortedBag.of());
            }}).start()));
        try{
            return claimedCardQueue.take();
        }
        catch(InterruptedException e){
            throw new Error();
        }
    }
}

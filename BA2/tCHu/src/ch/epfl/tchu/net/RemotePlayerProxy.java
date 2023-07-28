package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.net.MessageId.*;
import static ch.epfl.tchu.net.Serdes.*;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * @author Romain Logean (327230)
 * @author Shuli Jia (316620)
 */
public class RemotePlayerProxy implements Player {

    private final Socket socket;
    private final static String SPACE = " ";

    /**
     *
     * @param socket: socket used to communicate through the network
     */
    public RemotePlayerProxy(Socket socket) {
        this.socket = socket;
    }

    /**
     * tell the player his id and all of the other's players id
     * @param ownId: id of the player
     * @param playerNames: names of all players
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        List<String> ls = new ArrayList<>();
        ls.add(INIT_PLAYERS.name());
        ls.add(PLAYER_ID_SERDE.serialize(ownId));

        String player1name = playerNames.get(PlayerId.PLAYER_1);
        String player2name = playerNames.get(PlayerId.PLAYER_2);
        if (PlayerId.COUNT == Constants.MAXIMAL_PLAYER_COUNT) {
            String player3name = playerNames.get(PlayerId.PLAYER_3);
            ls.add(LIST_STRING_SERDE.serialize(List.of(player1name, player2name,player3name)));
        }
        else {
            ls.add(LIST_STRING_SERDE.serialize(List.of(player1name, player2name)));
        }

        String message = String.join(SPACE, ls);
        send(message);
    }

    /**
     * information that is communicated to the player
     * @param info: the information that is to be communicated
     */
    @Override
    public void receiveInfo(String info) {
        List<String> ls = new ArrayList<>();
        ls.add(RECEIVE_INFO.name());
        ls.add(STRING_SERDE.serialize(info));

        String message = String.join(SPACE, ls);
        send(message);
    }

    /**
     * update the new state of the player
     * @param newState: the new state of the game to be returned
     * @param ownState: the own state of the player
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        List<String> ls = new ArrayList<>();
        ls.add(UPDATE_STATE.name());
        ls.add(PUBLIC_GAME_STATE_SERDE.serialize(newState));
        ls.add(PLAYER_STATE_SERDE.serialize(ownState));

        String message = String.join(SPACE, ls);
        send(message);
    }

    /**
     * communicate tickets given to the player at the beginning of the game
     * @param tickets: 5 initial tickets given to the player
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        List<String> ls = new ArrayList<>();
        ls.add(SET_INITIAL_TICKETS.name());
        ls.add(BAG_TICKET_SERDE.serialize(tickets));

        String message = String.join(SPACE, ls);
        send(message);
    }

    @Override
    public void end(String endingWindowText) {
        List<String> ls = new ArrayList<>();
        ls.add(END.name());
        ls.add(STRING_SERDE.serialize(endingWindowText));

        String message = String.join(SPACE, ls);
        send(message);
        }

    /**
     *
     * @return the tickets the player decided to keep
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets(){
        List<String> ls = new ArrayList<>();
        ls.add(CHOOSE_INITIAL_TICKETS.name());

        String message = String.join(SPACE, ls);
        send(message);

        return BAG_TICKET_SERDE.deserialize(receive());
    }

    /**
     *
     * @return which action the player wants to do before his turn
     */
    @Override
    public TurnKind nextTurn() {
        List<String> ls = new ArrayList<>();
        ls.add(NEXT_TURN.name());

        String message = String.join(SPACE, ls);
        send(message);

        return TURN_KIND_SERDE.deserialize(receive());
    }

    /**
     *
     * @param options: tickets among which the player can choose
     * @return tickets the player wants to keep
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        List<String> ls = new ArrayList<>();
        ls.add(CHOOSE_TICKETS.name());
        ls.add(BAG_TICKET_SERDE.serialize(options));

        String message = String.join(SPACE, ls);
        send(message);

        return BAG_TICKET_SERDE.deserialize(receive());
    }

    /**
     *
     * @return the slot of the card the player wants to draw
     */
    @Override
    public int drawSlot() {
        List<String> ls = new ArrayList<>();
        ls.add(DRAW_SLOT.name());

        String message = String.join(SPACE, ls);
        send(message);

        return INTEGER_SERDE.deserialize(receive());
    }

    /**
     *
     * @return the route the player tried to claim
     */
    @Override
    public Route claimedRoute() {
        List<String> ls = new ArrayList<>();
        ls.add(ROUTE.name());

        String message = String.join(SPACE, ls);
        send(message);

        return ROUTE_SERDE.deserialize(receive());
    }

    /**
     *
     * @return the cards the player wants to play to claim route
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        List<String> ls = new ArrayList<>();
        ls.add(CARDS.name());

        String message = String.join(SPACE, ls);
        send(message);

        return BAG_CARD_SERDE.deserialize(receive());
    }

    /**
     *
     * @param options: additional cards among which the player can choose
     * @return the cards the player chose to claim a route
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        List<String> ls = new ArrayList<>();
        ls.add(CHOOSE_ADDITIONAL_CARDS.name());
        ls.add(LIST_BAG_CARDS_SERDE.serialize(options));

        String message = String.join(SPACE, ls);
        send(message);

        return BAG_CARD_SERDE.deserialize(receive());
    }

    private String receive() {
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
            return r.readLine();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void send(String message){
        try {
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
            w.write(message);
            w.write('\n');
            w.flush();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

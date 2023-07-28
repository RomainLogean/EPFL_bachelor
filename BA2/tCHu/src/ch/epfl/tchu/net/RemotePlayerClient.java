package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static ch.epfl.tchu.net.Serdes.*;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * @author Romain Logean (327230)
 * @author Shuli Jia (316620)
 */
public class RemotePlayerClient {

    private final static String SPACE = " ";
    Player player;
    String name;
    int port;
    boolean endOfGame = false;

    /**
     * 
     * @param player: player to which this class has to provide the distant access
     * @param name: the name used to connect itself to the representative
     * @param port: the port used to connect itself to the representative
     */
    public RemotePlayerClient(Player player, String name, int port) {
        this.player = player;
        this.name = name;
        this.port = port;
    }

    /**
     * loop for which the process is:
     *      - wait for a message from the representative
     *      - split the message with the string space
     *      - determine the type of the message with the first part of the split message
     *      - deserialize the split message and use the player's method:
     *              if there is a result, serialize the message and send it to the representative as a response
     */
    public void run(){

        try (Socket s = new Socket(name, port);
            BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream(), US_ASCII));
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), US_ASCII)))
        {
            while(!endOfGame) {

                String receivedMessage = receive(r);
                String[] splitMessage = receivedMessage.split(Pattern.quote(SPACE), -1);
                String commande = splitMessage[0];

                switch (MessageId.valueOf(commande)) {

                    case INIT_PLAYERS:
                        PlayerId OwnId = PLAYER_ID_SERDE.deserialize(splitMessage[1]);
                        List<String> playerNames = LIST_STRING_SERDE.deserialize(splitMessage[2]);

                        int playersInGame = playerNames.size();
                        PlayerId.initialPlayerNumber(playersInGame);

                        String player1 = playerNames.get(0);
                        String player2 = playerNames.get(1);
                        if(PlayerId.COUNT==Constants.MAXIMAL_PLAYER_COUNT){
                            String player3 = playerNames.get(2);
                            player.initPlayers(OwnId, Map.of(PlayerId.PLAYER_1, player1,
                                    PlayerId.PLAYER_2, player2, PlayerId.PLAYER_3, player3));
                        }
                        else{
                            player.initPlayers(OwnId, Map.of(PlayerId.PLAYER_1, player1,
                                    PlayerId.PLAYER_2, player2));
                        }
                    break;

                    case RECEIVE_INFO :
                        String info = STRING_SERDE.deserialize(splitMessage[1]);
                        player.receiveInfo(info);
                    break;
                
                    case UPDATE_STATE :
                        PublicGameState newGameState = PUBLIC_GAME_STATE_SERDE.deserialize(splitMessage[1]);
                        PlayerState OwnState = PLAYER_STATE_SERDE.deserialize(splitMessage[2]);

                        player.updateState(newGameState, OwnState);
                    break;
                
                    case SET_INITIAL_TICKETS :
                        SortedBag<Ticket> tickets = BAG_TICKET_SERDE.deserialize(splitMessage[1]);
                        player.setInitialTicketChoice(tickets);
                    break;
                
                    case CHOOSE_INITIAL_TICKETS :
                        SortedBag<Ticket> sbTickets = player.chooseInitialTickets();
                        String seriSbTicket = BAG_TICKET_SERDE.serialize(sbTickets);

                        sendBack(seriSbTicket, w);
                    break;
                
                    case NEXT_TURN :
                        Player.TurnKind nextTurn = player.nextTurn();

                        String seriNextTurn = TURN_KIND_SERDE.serialize(nextTurn);
                        sendBack(seriNextTurn,w);
                    break;
                
                    case CHOOSE_TICKETS :
                        SortedBag<Ticket> options = BAG_TICKET_SERDE.deserialize(splitMessage[1]);

                        SortedBag<Ticket> choosen = player.chooseTickets(options);
                        String seriChoosen = BAG_TICKET_SERDE.serialize(choosen);
                        sendBack(seriChoosen,w);
                    break;
                
                    case DRAW_SLOT :
                        int drawSlot = player.drawSlot();

                        String seriDrawSlot = INTEGER_SERDE.serialize(drawSlot);
                        sendBack(seriDrawSlot,w);
                    break;
                
                    case ROUTE :
                        Route claimedRoute = player.claimedRoute();

                        String seriClaimRoute = ROUTE_SERDE.serialize(claimedRoute);
                        sendBack(seriClaimRoute, w);
                    break;
                
                    case CARDS :
                        SortedBag<Card> sbInitialCard = player.initialClaimCards();

                        String seriSbInitCards = BAG_CARD_SERDE.serialize(sbInitialCard);
                        sendBack(seriSbInitCards,w);
                    break;
                
                    case CHOOSE_ADDITIONAL_CARDS :
                        List<SortedBag<Card>> listSbCard = LIST_BAG_CARDS_SERDE.deserialize(splitMessage[1]);
                        SortedBag<Card> sbCard = player.chooseAdditionalCards(listSbCard);

                        String seriSbCard = BAG_CARD_SERDE.serialize(sbCard);
                        sendBack(seriSbCard, w);
                    break;

                    case END :
                        String stats = STRING_SERDE.deserialize(splitMessage[1]);
                        player.end(stats);
                    break;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String receive(BufferedReader r) throws IOException{
        String message = r.readLine();
        if (message==null){
            endOfGame=true;
        }
        return message;
    }

    private void sendBack(String message,BufferedWriter w) throws IOException{
            w.write(message);
            w.write('\n');
            w.flush();
    }

}

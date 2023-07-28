package ch.epfl.tchu.net;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.game.Player.TurnKind;

/**
 * @author Romain Logean (327230)
 * @author Shuli JIA (316620)
 */
public class Serdes {

    private static final String COMMA = ",";
    private static final String SEMICOLON = ";";
    private static final String COLON = ":";

    /**
     * serde for Integer
     */
    public static final Serde<Integer> INTEGER_SERDE = Serde.of(
            i -> Integer.toString(i),
            Integer::parseInt);

    /**
     * serde for String
     */
    public static final Serde<String> STRING_SERDE = Serde.of(
            s -> {
                byte[] c = s.getBytes(StandardCharsets.UTF_8);
                return Base64.getEncoder().encodeToString(c);
            },

            s ->{
                byte[] c = Base64.getDecoder().decode(s);
                return new String(c);
            });

    /**
     * serde of the type PlayerId
     */
    public static final Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ENUM);

    /**
     * serde of the type TurnKind
     */
    public static final Serde<TurnKind> TURN_KIND_SERDE = Serde.oneOf(TurnKind.ALL);

    /**
     * serde of the type Card
     */
    public static final Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);

    /**
     * serde of the type Route
     */
    public static final Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());

    /**
     * serde of the type Ticket
     */
    public static final Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());

    /**
     * serde of the type List<String>
     */
    public static final Serde<List<String>> LIST_STRING_SERDE = Serde.listOf(STRING_SERDE, COMMA);

    /**
     * serde of the List<Card>
     */
    public static final Serde<List<Card>> LIST_CARD_SERDE = Serde.listOf(CARD_SERDE, COMMA);

    /**
     * serde of the List<Route>
     */
    public static final Serde<List<Route>> LIST_ROUTE_SERDE = Serde.listOf(ROUTE_SERDE, COMMA);

    /**
     * serde of the SortedBag<Card>
     */
    public static final Serde<SortedBag<Card>> BAG_CARD_SERDE = Serde.bagOf(CARD_SERDE, COMMA);

    /**
     * serde of the SortedBag<Tickets>
     */
    public static final Serde<SortedBag<Ticket>> BAG_TICKET_SERDE = Serde.bagOf(TICKET_SERDE, COMMA);

    /**
     * serde of the SortedBag<Card>
     */
    public static final Serde<List<SortedBag<Card>>> LIST_BAG_CARDS_SERDE = Serde.listOf(BAG_CARD_SERDE, SEMICOLON);

    /**
     * serde of the PublicCardState
     */
    public static final Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = Serde.of(
            object -> {
                List<String> serialList = new ArrayList<>();
                serialList.add(LIST_CARD_SERDE.serialize(object.faceUpCards()));
                serialList.add(INTEGER_SERDE.serialize(object.deckSize()));
                serialList.add(INTEGER_SERDE.serialize(object.discardsSize()));

                return String.join(SEMICOLON,serialList);
            },

            s -> {
                String[] sToList = s.split(Pattern.quote(SEMICOLON), -1);
                List<Card> faceUpCard = LIST_CARD_SERDE.deserialize(sToList[0]);
                int deckSize = INTEGER_SERDE.deserialize(sToList[1]);
                int discardsSize = INTEGER_SERDE.deserialize(sToList[2]);

                return new PublicCardState(faceUpCard,deckSize,discardsSize);
            });

    /**
     * serde of the PublicPlayerState
     */
    public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = Serde.of(
            object -> {
                List<String> serialList = new ArrayList<>();
                serialList.add(INTEGER_SERDE.serialize(object.ticketCount()));
                serialList.add(INTEGER_SERDE.serialize(object.cardCount()));
                serialList.add(LIST_ROUTE_SERDE.serialize(object.routes()));

                return String.join(SEMICOLON,serialList);
            },

            s -> {
                String[] sToList = s.split(Pattern.quote(SEMICOLON), -1);
                int ticketCount = INTEGER_SERDE.deserialize(sToList[0]);
                int cardCount = INTEGER_SERDE.deserialize(sToList[1]);
                List<Route> routes = LIST_ROUTE_SERDE.deserialize(sToList[2]);

                return new PublicPlayerState(ticketCount, cardCount, routes);
            });

    /**
     * serde of the PlayerState
     */
    public static final Serde<PlayerState> PLAYER_STATE_SERDE = Serde.of(
            object -> {
                List<String> serialList = new ArrayList<>();
                serialList.add(BAG_TICKET_SERDE.serialize(object.tickets()));
                serialList.add(BAG_CARD_SERDE.serialize(object.cards()));
                serialList.add(LIST_ROUTE_SERDE.serialize(object.routes()));

                return String.join(SEMICOLON, serialList);
            },

            s -> {
                String[] sToList = s.split(Pattern.quote(SEMICOLON), -1);
                SortedBag<Ticket> tickets = BAG_TICKET_SERDE.deserialize(sToList[0]);
                SortedBag<Card> cards = BAG_CARD_SERDE.deserialize(sToList[1]);
                List<Route> routes = LIST_ROUTE_SERDE.deserialize(sToList[2]);

                return new PlayerState(tickets, cards, routes);
            });

    /**
     * serde of the PublicGameState
     */
    public static final Serde<PublicGameState> PUBLIC_GAME_STATE_SERDE = Serde.of(
            object -> {
                List<String> serialList = new ArrayList<>();
                serialList.add(INTEGER_SERDE.serialize(object.ticketsCount()));
                serialList.add(PUBLIC_CARD_STATE_SERDE.serialize(object.cardState()));
                serialList.add(PLAYER_ID_SERDE.serialize(object.currentPlayerId()));
                serialList.add(PUBLIC_PLAYER_STATE_SERDE.serialize(object.playerState(PlayerId.PLAYER_1)));
                serialList.add(PUBLIC_PLAYER_STATE_SERDE.serialize(object.playerState(PlayerId.PLAYER_2)));

                if (PlayerId.COUNT==Constants.MAXIMAL_PLAYER_COUNT){
                    serialList.add(PUBLIC_PLAYER_STATE_SERDE.serialize(object.playerState(PlayerId.PLAYER_3)));
                }
                if (object.lastPlayer() != null) {
                    serialList.add(PLAYER_ID_SERDE.serialize(object.lastPlayer()));
                } else {
                    serialList.add("");
                }
                return String.join(COLON, serialList);
            },

            s -> {
                String[] sToList = s.split(Pattern.quote(COLON), -1);
                int ticketsCount = INTEGER_SERDE.deserialize(sToList[0]);
                int lastPlayerSlot = sToList.length-1;

                PublicCardState cardState = PUBLIC_CARD_STATE_SERDE.deserialize(sToList[1]);
                PlayerId currentPlayerId = PLAYER_ID_SERDE.deserialize(sToList[2]);
                PublicPlayerState playerState_player1 = PUBLIC_PLAYER_STATE_SERDE.deserialize(sToList[3]);
                PublicPlayerState playerState_player2 = PUBLIC_PLAYER_STATE_SERDE.deserialize(sToList[4]);
                PlayerId lastPlayer = null;
                if(sToList[lastPlayerSlot].length()!=0){
                    lastPlayer = PLAYER_ID_SERDE.deserialize(sToList[lastPlayerSlot]);
                }
                Map<PlayerId,PublicPlayerState> playerState;
                if (PlayerId.COUNT == Constants.MAXIMAL_PLAYER_COUNT){
                    PublicPlayerState playerState_player3 = PUBLIC_PLAYER_STATE_SERDE.deserialize(sToList[5]);
                    playerState = Map.of(PlayerId.PLAYER_1,playerState_player1,
                            PlayerId.PLAYER_2,playerState_player2,PlayerId.PLAYER_3,playerState_player3);
                }
                else {
                    playerState = Map.of(PlayerId.PLAYER_1,playerState_player1,
                            PlayerId.PLAYER_2,playerState_player2);
                }

                return new PublicGameState(ticketsCount, cardState, currentPlayerId, playerState, lastPlayer);

            });
}

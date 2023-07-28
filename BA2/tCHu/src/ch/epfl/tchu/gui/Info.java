package ch.epfl.tchu.gui;

import java.util.List;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

/**
 * @author Romain Logean (327230)
 * @author Shuli Jia (316620)
 */
public final class Info {
    private final String playerName;

    /**
     *
     * constructs the info that generates text to explain the progress of the game
     * and register the player's name
     * @param playerName: the name of the player
     */
    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     *
     * @param card: the card for which we want the name
     * @param count: the number of same card
     * @return the name of the card (in plural if needed)
     */
    public static String cardName(Card card, int count) {
        String cardName;
        switch (card) {
            case BLACK:
                cardName = StringsFr.BLACK_CARD;
                break;

            case BLUE:
                cardName = StringsFr.BLUE_CARD;
                break;

            case GREEN:
                cardName = StringsFr.GREEN_CARD;
                break;

            case ORANGE:
                cardName = StringsFr.ORANGE_CARD;
                break;

            case RED:
                cardName = StringsFr.RED_CARD;
                break;

            case VIOLET:
                cardName = StringsFr.VIOLET_CARD;
                break;

            case WHITE:
                cardName = StringsFr.WHITE_CARD;
                break;

            case YELLOW:
                cardName = StringsFr.YELLOW_CARD;
                break;

            case LOCOMOTIVE:
                cardName = StringsFr.LOCOMOTIVE_CARD;
                break;

            default:
                cardName = "";

        }
        return cardName += StringsFr.plural(count);
    }

    /**
     *
     * @param playerNames: list of all the players' names
     * @param points: the number of points that the players have
     * @return the final message if there is a tie
     */
    public static String draw(List<String> playerNames, int points) {
        String names;
        if(PlayerId.COUNT == Constants.MAXIMAL_PLAYER_COUNT) {
            names = playerNames.get(0) + StringsFr.COMMA + playerNames.get(1) + StringsFr.AND_SEPARATOR + playerNames.get(2);
        }
        else{
            names = playerNames.get(0) + StringsFr.AND_SEPARATOR + playerNames.get(1);

        }
        return String.format(StringsFr.DRAW, names, points);
    }

    public static String twoWinners(List<String> playerNames,  int winPoints, int losePoints){
        String names = playerNames.get(0) + StringsFr.AND_SEPARATOR + playerNames.get(1);
        return String.format(StringsFr.TWO_WIN, names , winPoints, losePoints);
    }

    /**
     *
     * @return message that says which player plays first
     */
    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST,playerName);
    }

    /**
     *
     * @param count: the number of tickets that are kept
     * @return the message that says how many tickets the player kept
     */
    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     *
     * @return the message that says that the player can play
     */
    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, playerName);
    }

    /**
     *
     * @param count: the number of tickets that are drawn
     * @return the message that says how many tickets the player drew
     */
    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     *
     * @return the message that says that the player has drawn a blind card
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }

    /**
     *
     * @return the message that says which card the player has drawn
     */
    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, cardName(card, 1));
    }

    /**
     *
     * @param route: the route that is claimed
     * @param cards: the cards that are used
     * @return the message that says that the player has claimed a route
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(StringsFr.CLAIMED_ROUTE, playerName, RouteName(route), CardList(cards));
    }

    /**
     *
     * @param route: the route that the player attempts to claim
     * @param initialCards: the cards that the player initially plays
     * @return the message that says the player tries to claim a tunnel
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM,playerName, RouteName(route), CardList(initialCards));
    }

    /**
     *
     * @param drawnCards: the cards that are drawn
     * @param additionalCost: the additional cost to claim a the tunnel
     * @return the number of additional cards that the player has to play to claim a tunnel
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        String additionalCards = String.format(StringsFr.ADDITIONAL_CARDS_ARE, CardList(drawnCards));
        return (additionalCost == 0)
            ? additionalCards+(StringsFr.NO_ADDITIONAL_COST)
            : additionalCards+String.format(StringsFr.SOME_ADDITIONAL_COST, 
                additionalCost, StringsFr.plural(additionalCost));
    }

    /**
     *
     * @param route: the route that is not claimed
     * @return the message that says that the player did not claim the route
     */
    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, RouteName(route));
    }

    /**
     *
     * @param carCount: the number of cards that the player has
     * @return the message that says that the last turn begins
     */
    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount, StringsFr.plural(carCount));
    }

    /**
     *
     * @param longestTrail: the longest trail of the game
     * @return the message that says that the player won some bonus point due to the longest trail
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        String longest = longestTrail.station1() + StringsFr.EN_DASH_SEPARATOR + longestTrail.station2();
        return String.format(StringsFr.GETS_BONUS, playerName, longest);
    }

    /**
     *
     * @param points: the winner's points
     * @param loserPoints: the loser's points
     * @return the message that says the player has won the game
     */
    public String won(int points, int loserPoints){
        return String.format(StringsFr.WINS,playerName,points,StringsFr.plural(points),loserPoints,StringsFr.plural(loserPoints));
    }

    /**
     *
     * @param points: the winner's points
     * @param loser1Points: the loser's points
     * @param loser2Points: the other loser's points
     * @return the message that says the player has won the game
     */
    public String won3Player(int points, int loser1Points, int loser2Points){
        return String.format(StringsFr.WINS_3PLAYERS,playerName,points,StringsFr.plural(points),loser1Points,StringsFr.plural(loser1Points), loser2Points,StringsFr.plural(loser2Points));
    }

    private String RouteName(Route route){
        return route.station1()+StringsFr.EN_DASH_SEPARATOR+route.station2();
    }

    private String CardList(SortedBag<Card> cards) {
        String cardList = "";
        int it = 0;
        
        if(cards.toSet().size() == 1){
            Card c = cards.toList().get(0);
            int n = cards.countOf(c);
            cardList += n + " " + Info.cardName(c, n);
            
        } else {
            for (Card c: cards.toSet()) {
                int n = cards.countOf(c);
                if(++it >= cards.toSet().size()){
                    cardList = cardList.substring(0, cardList.length() - 2) + StringsFr.AND_SEPARATOR
                            + n + " " + Info.cardName(c, n);
                } else {
                    cardList += n + " " + Info.cardName(c, n) + ", ";
                }
            }
        }
        return cardList;
    }
}

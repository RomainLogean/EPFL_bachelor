package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import ch.epfl.tchu.gui.StringsFr;
import javafx.collections.transformation.TransformationList;

import static ch.epfl.tchu.game.PlayerId.COUNT;
import static ch.epfl.tchu.game.PlayerId.PLAYER_1;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Romain Logean (327230)
 * @author Shuli JIA (316620)
 */
public final class Game {

    private Game(){}

    /**
     * this is the method that defined the advancement of the game and the different actions of the players.
     * @param players the player according to their iD
     * @param playerNames the players names according to their iD
     * @param tickets the tickets of the beginning of the game.
     * @param rng a Random variable
     */

    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng){
        Preconditions.checkArgument(players.size() == COUNT && playerNames.size() == COUNT);
        for(PlayerId id : PlayerId.ALL) {
            players.get(id).initPlayers(id, playerNames);
        }

        Map<PlayerId, Info> infoList = new TreeMap<>();
        for(PlayerId id : PlayerId.ALL){
            infoList.put(id ,new Info(playerNames.get(id)));
        }
        GameState gameState = GameState.initial(tickets,rng);


        for (PlayerId Id : PlayerId.ALL) {
            players.get(Id).setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }

        receiveInfo(players, infoList.get(gameState.currentPlayerId()).willPlayFirst());

        updateState(players,gameState);

        List<Integer> ticketsKept = new ArrayList<>();

        for (PlayerId Id : PlayerId.ALL) {
            SortedBag<Ticket> PlayerTickets = players.get(Id).chooseInitialTickets();
            gameState = gameState.withInitiallyChosenTickets(Id,PlayerTickets);
            ticketsKept.add(PlayerTickets.size());
        }

        for (int i = 0; i < COUNT; i++) {
            receiveInfo(players,infoList.get(PlayerId.ALL.get(i)).keptTickets(ticketsKept.get(i)));
        }


        boolean endGame = false;
        do {
            Player player = players.get(gameState.currentPlayerId());
            Info info = infoList.get(gameState.currentPlayerId());

            receiveInfo(players,info.canPlay());
            updateState(players,gameState);

            Player.TurnKind playerAction = player.nextTurn();
            switch (playerAction) {
                case DRAW_TICKETS:
                    receiveInfo(players, info.drewTickets(Constants.IN_GAME_TICKETS_COUNT));
                    SortedBag<Ticket> drawTickets = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                    SortedBag<Ticket> keptTickets = player.chooseTickets(drawTickets);
                    gameState = gameState.withChosenAdditionalTickets(drawTickets, keptTickets);
                    receiveInfo(players, info.keptTickets(keptTickets.size()));
                    break;
                case DRAW_CARDS:

                    gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                    gameState = drawCard(players,info,gameState);

                    updateState(players, gameState);

                    gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                    gameState = drawCard(players,info,gameState);

                    break;

                case CLAIM_ROUTE:
                    Route claimedRoute = player.claimedRoute();
                    SortedBag<Card> usedCards = player.initialClaimCards();
                    if (claimedRoute.level().equals(Route.Level.UNDERGROUND)) {
                        receiveInfo(players, info.attemptsTunnelClaim(claimedRoute, usedCards));

                        SortedBag.Builder<Card> drawnCard = new SortedBag.Builder<>();
                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            drawnCard.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }
                        SortedBag<Card> drawCardBag = drawnCard.build();
                        int addCardNumber = claimedRoute.additionalClaimCardsCount(usedCards, drawCardBag);

                        gameState = gameState.withMoreDiscardedCards(drawCardBag);
                        receiveInfo(players, info.drewAdditionalCards(drawCardBag, addCardNumber));
                        SortedBag<Card> addCardChoice;
                        if (addCardNumber > 0) {
                            List<SortedBag<Card>> addOptions = gameState.playerState(gameState.currentPlayerId()).possibleAdditionalCards(addCardNumber, usedCards);
                            if (addOptions.size() != 0) {
                                addCardChoice = player.chooseAdditionalCards(addOptions);
                                if (addCardChoice == null || addCardChoice.size() == 0) {
                                    receiveInfo(players, info.didNotClaimRoute(claimedRoute));
                                } else {
                                    usedCards = usedCards.union(addCardChoice);
                                    gameState = gameState.withClaimedRoute(claimedRoute, usedCards);
                                    receiveInfo(players, info.claimedRoute(claimedRoute, usedCards));
                                }
                            } else {
                                receiveInfo(players, info.didNotClaimRoute(claimedRoute));
                            }
                        } else {
                            gameState = gameState.withClaimedRoute(claimedRoute, usedCards);
                            receiveInfo(players, info.claimedRoute(claimedRoute, usedCards));
                        }
                    } else {
                        gameState = gameState.withClaimedRoute(claimedRoute, usedCards);
                        receiveInfo(players, info.claimedRoute(claimedRoute, usedCards));
                    }
                    break;
                }
            if(gameState.lastPlayer() == gameState.currentPlayerId()){
                endGame=true;
            }
            if(gameState.lastTurnBegins()){
                receiveInfo(players,info.lastTurnBegins(gameState.playerState(gameState.currentPlayerId()).carCount()));
            }
            gameState = gameState.forNextTurn();
        }while(!endGame);

        Map<PlayerId,Integer> points = new TreeMap<>();
        for (PlayerId Id : PlayerId.ALL) {
            points.put(Id,gameState.playerState(Id).finalPoints());
        }
        Map<PlayerId,Trail> longestTrailList = new TreeMap<>();
        for (PlayerId Id : PlayerId.ALL) {
            longestTrailList.put(Id,Trail.longest(gameState.playerState(Id).routes()));
        }

        int theLongestTrail = 0;
        for (PlayerId Id : PlayerId.ALL) {
            if (longestTrailList.get(Id).length() >= theLongestTrail){
                theLongestTrail = longestTrailList.get(Id).length();
            }
        }

        for (PlayerId Id : PlayerId.ALL) {
            if (longestTrailList.get(Id).length() == theLongestTrail){
                int playerPoints = points.get(Id) + Constants.LONGEST_TRAIL_BONUS_POINTS;
                points.put(Id,playerPoints);
                receiveInfo(players,infoList.get(Id).getsLongestTrailBonus(longestTrailList.get(Id)));
            }
        }

        int winnerPoints = points.get(PLAYER_1);
        for (PlayerId Id : PlayerId.ALL) {
            if (points.get(Id) >= winnerPoints){
                winnerPoints = points.get(Id);
            }
        }
        List<PlayerId> winners =new ArrayList<>();
        for (PlayerId Id : PlayerId.ALL) {
            if (points.get(Id) == winnerPoints){
                winners.add(Id);
            }
        }
        List<PlayerId> losers = new ArrayList<>();
        losers.addAll(PlayerId.ALL);
        losers.removeAll(winners);

        updateState(players,gameState);

        StringBuilder StringForEndingWindow = new StringBuilder();

        List<String> winnerNamesList = new ArrayList<>();
        for (PlayerId Id : winners) {
            winnerNamesList.add(playerNames.get(Id));
        }

        String end;
        if(losers.size()==0){
            end = Info.draw(winnerNamesList, points.get(PLAYER_1));
            receiveInfo(players,end);

        } else if(winners.size()==2 && losers.size()==1){
            end=Info.twoWinners(winnerNamesList,points.get(winners.get(0)),points.get(losers.get(0)));
            receiveInfo(players,end);

        } else if(winners.size()==1 && losers.size()==2){
            end = infoList.get(winners.get(0)).won3Player(points.get(winners.get(0)),points.get(losers.get(0)),points.get(losers.get(1)));
            receiveInfo(players,end);
        }
        else{
            end = infoList.get(winners.get(0)).won(points.get(winners.get(0)),points.get(losers.get(0)));
            receiveInfo(players,end);
        }

        StringForEndingWindow.append(end);

        StringForEndingWindow.append(StringsFr.STATS1);
        for (PlayerId Id : PlayerId.ALL) {
            StringForEndingWindow.append(playerNames.get(Id));
            StringForEndingWindow.append("\t:\t");
            int idPoints = points.get(Id);
            StringForEndingWindow.append(idPoints + StringsFr.STATS2 + StringsFr.plural(idPoints));
            StringForEndingWindow.append("\n");
        }

        StringForEndingWindow.append(StringsFr.STATS3);

        for (PlayerId Id : PlayerId.ALL) {
            StringForEndingWindow.append(playerNames.get(Id));
            StringForEndingWindow.append("\t:\t");
            StringForEndingWindow.append(longestTrailList.get(Id));
            StringForEndingWindow.append("\n");
        }
        StringForEndingWindow.append(StringsFr.STATS4);

        end(players,StringForEndingWindow.toString());
    }

    private static void receiveInfo(Map<PlayerId,Player> players, String info){
        for (Player player : players.values()) {
            player.receiveInfo(info);
        }
    }

    private static void updateState(Map<PlayerId,Player> players, GameState newGameState){
        for (PlayerId id : PlayerId.ALL) {
            Player player = players.get(id);
            player.updateState(newGameState,newGameState.playerState(id));
        }
    }

    private static void end(Map<PlayerId,Player> players, String endingWindow){
        for (Player player : players.values()) {
            player.end(endingWindow);
        }
    }

    private static GameState drawCard(Map<PlayerId,Player> players, Info info, GameState gameState){
        Player player = players.get(gameState.currentPlayerId());
        int drawSlot = player.drawSlot();

        if(drawSlot == Constants.DECK_SLOT){
            receiveInfo(players, info.drewBlindCard());
            return gameState.withBlindlyDrawnCard();

        } else{
            receiveInfo(players, info.drewVisibleCard(gameState.cardState().faceUpCard(drawSlot)));
            return gameState.withDrawnFaceUpCard(drawSlot);
        }
    }
}

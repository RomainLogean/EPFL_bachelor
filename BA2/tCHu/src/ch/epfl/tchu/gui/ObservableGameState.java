package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.tchu.game.Constants.*;

/**
 * @author Romain Logean (327230)
 * @author Shuli Jia (316620)
 */
public class ObservableGameState {

    private final PlayerId player;
    private PublicGameState publicGameState;
    private PlayerState playerState;

    private final IntegerProperty cardsPercentage;
    private final IntegerProperty ticketsPercentage;
    private final List<ObjectProperty<Card>> faceUpCards;
    private final List<ObjectProperty<PlayerId>> routeOwner;

    private final IntegerProperty ticketsCount_p1;
    private final IntegerProperty ticketsCount_p2;
    private final IntegerProperty ticketsCount_p3;
    private final IntegerProperty cardsCount_p1;
    private final IntegerProperty cardsCount_p2;
    private final IntegerProperty cardsCount_p3;
    private final IntegerProperty carsCount_p1;
    private final IntegerProperty carsCount_p2;
    private final IntegerProperty carsCount_p3;
    private final IntegerProperty pointsCount_p1;
    private final IntegerProperty pointsCount_p2;
    private final IntegerProperty pointsCount_p3;

    private final ObservableList<Ticket> ticketObservableList;

    private final ObjectProperty<ObservableList<Ticket>> ticketList;
    private final List<IntegerProperty> cardTypeCount;
    private final List<BooleanProperty> claimable;

    /**
     * Constructor of an ObservableGameState
     * @param player the player for who we want properties
     */
    public ObservableGameState(PlayerId player) {
        this.player = player;

        cardsPercentage = new SimpleIntegerProperty(0);
        ticketsPercentage= new SimpleIntegerProperty(0);
        faceUpCards= new ArrayList<>();
        for (int i = 0 ; i < FACE_UP_CARDS_COUNT ; i++) {
            faceUpCards.add(new SimpleObjectProperty<>(null));
        }
        routeOwner= new ArrayList<>();
        for (int i = 0 ; i < ChMap.routes().size() ; i++) {
            routeOwner.add(new SimpleObjectProperty<>(null));
        }

        ticketsCount_p1 = new SimpleIntegerProperty(0);
        ticketsCount_p2 = new SimpleIntegerProperty(0);
        ticketsCount_p3 = new SimpleIntegerProperty(0);
        cardsCount_p1 = new SimpleIntegerProperty(0);
        cardsCount_p2 = new SimpleIntegerProperty(0);
        cardsCount_p3 = new SimpleIntegerProperty(0);
        carsCount_p1 = new SimpleIntegerProperty(0);
        carsCount_p2 = new SimpleIntegerProperty(0);
        carsCount_p3 = new SimpleIntegerProperty(0);
        pointsCount_p1 = new SimpleIntegerProperty(0);
        pointsCount_p2 = new SimpleIntegerProperty(0);
        pointsCount_p3 = new SimpleIntegerProperty(0);

        ticketObservableList = FXCollections.observableArrayList();
        ticketList = new SimpleObjectProperty<>(ticketObservableList);

        cardTypeCount = new ArrayList<>();
        for (int i = 0; i < Card.COUNT; i++) {
            cardTypeCount.add(new SimpleIntegerProperty(0));
        }
        claimable= new ArrayList<>();
        for (int i = 0 ; i < ChMap.routes().size() ; i++) {
            claimable.add(new SimpleBooleanProperty(false));
        }

    }

    /**
     * Update the different properties of the game and the player
     * @param newGameState the new instance of gameState
     * @param newPlayerState the new playerState of the player attached to this
     */
    public void setState(PublicGameState newGameState, PlayerState newPlayerState){
        this.publicGameState = newGameState;
        this.playerState = newPlayerState;

        cardsPercentage.set((int)(((double)newGameState.cardState().deckSize()/(double)TOTAL_CARDS_COUNT)*100));
        ticketsPercentage.set((int)(((double)newGameState.ticketsCount()/(double)ChMap.tickets().size())*100));
        for (int slot : FACE_UP_CARD_SLOTS) {
            Card newCard = newGameState.cardState().faceUpCard(slot);

            faceUpCards.get(slot).set(newCard);
        }
        for (Route r1 : newGameState.playerState(PlayerId.PLAYER_1).routes()) {
            routeOwner.get(ChMap.routes().indexOf(r1)).set(PlayerId.PLAYER_1);
        }
        for (Route r2 : newGameState.playerState(PlayerId.PLAYER_2).routes()) {
            routeOwner.get(ChMap.routes().indexOf(r2)).set(PlayerId.PLAYER_2);
        }
        if (PlayerId.COUNT == MAXIMAL_PLAYER_COUNT) {
            for (Route r3 : newGameState.playerState(PlayerId.PLAYER_3).routes()) {
                routeOwner.get(ChMap.routes().indexOf(r3)).set(PlayerId.PLAYER_3);
            }

            pointsCount_p3.set(newGameState.playerState(PlayerId.PLAYER_3).claimPoints());
            carsCount_p3.set(newGameState.playerState(PlayerId.PLAYER_3).carCount());
            cardsCount_p3.set(newGameState.playerState(PlayerId.PLAYER_3).cardCount());
            ticketsCount_p3.set(newGameState.playerState(PlayerId.PLAYER_3).ticketCount());
        }

        ticketsCount_p1.set(newGameState.playerState(PlayerId.PLAYER_1).ticketCount());
        ticketsCount_p2.set(newGameState.playerState(PlayerId.PLAYER_2).ticketCount());
        cardsCount_p1.set(newGameState.playerState(PlayerId.PLAYER_1).cardCount());
        cardsCount_p2.set(newGameState.playerState(PlayerId.PLAYER_2).cardCount());
        carsCount_p1.set(newGameState.playerState(PlayerId.PLAYER_1).carCount());
        carsCount_p2.set(newGameState.playerState(PlayerId.PLAYER_2).carCount());
        pointsCount_p1.set(newGameState.playerState(PlayerId.PLAYER_1).claimPoints());
        pointsCount_p2.set(newGameState.playerState(PlayerId.PLAYER_2).claimPoints());

        ticketObservableList.setAll(newPlayerState.tickets().toList());

        for (int i = 0; i < Card.COUNT; i++) {
            int number = newPlayerState.cards().countOf(Card.values()[i]);
            cardTypeCount.get(i).set(number);
        }

        for (int i = 0; i < ChMap.routes().size(); i++) {
            claimable.get(i).set(
                    newGameState.currentPlayerId() == player &&
                            routeOwner.get(i).get() == null &&
                            newPlayerState.canClaimRoute(ChMap.routes().get(i))
            );
        }

    }

    /**
     *
     * @return the percentage of cards in the deck
     */
    public ReadOnlyIntegerProperty cardsPercentage(){
        return cardsPercentage;
    }

    /**
     *
     * @return the percentage of tickets in the deck
     */
    public ReadOnlyIntegerProperty ticketsPercentage(){
        return ticketsPercentage;
    }

    /**
     *
     * @param slot : the position of the card we are interested for
     * @return a property containing the card
     */
    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }

    /**
     *
     * @param index : index of the route
     * @return the if of the owner of this route (or null)
     */
    public ReadOnlyObjectProperty<PlayerId> routeOwner(int index) {
        return routeOwner.get(index);
    }

    /**
     *
     * @param p : the id of the player
     * @return the count of the tickets of the player
     */
    public ReadOnlyIntegerProperty ticketsCount_p(PlayerId p){
        if(p==PlayerId.PLAYER_1){
            return ticketsCount_p1;
        }
        if (p==PlayerId.PLAYER_2){
            return ticketsCount_p2;
        }
        if (p== PlayerId.PLAYER_3){
            return ticketsCount_p3;
        }
        return null;
    }

    /**
     *
     * @param p : the id of the player
     * @return the count of the card of this player
     */
    public ReadOnlyIntegerProperty cardsCount_p(PlayerId p){
        if(p==PlayerId.PLAYER_1){
            return cardsCount_p1;
        }
        if (p==PlayerId.PLAYER_2){
            return cardsCount_p2;
        }
        if (p== PlayerId.PLAYER_3){
            return cardsCount_p3;
        }
        return null;
    }

    /**
     *
     * @param p : the id of the player
     * @return the count of the remaining cars of this player
     */
    public ReadOnlyIntegerProperty carsCount_p(PlayerId p){
        if(p==PlayerId.PLAYER_1){
            return carsCount_p1;
        }
        if (p==PlayerId.PLAYER_2){
            return carsCount_p2;
        }
        if (p== PlayerId.PLAYER_3){
            return carsCount_p3;
        }
        return null;
    }

    /**
     *
     * @param p : the id of the player
     * @return the points of this player
     */
    public ReadOnlyIntegerProperty pointsCount_p(PlayerId p){
        if(p==PlayerId.PLAYER_1){
            return pointsCount_p1;
        }
        if (p==PlayerId.PLAYER_2){
            return pointsCount_p2;
        }
        if (p== PlayerId.PLAYER_3){
            return pointsCount_p3;
        }
        return null;
    }

    /**
     *
     * @return the list of the tickets
     */
    public ReadOnlyObjectProperty<ObservableList<Ticket>> ticketList(){
        return ticketList;
    }

    /**
     *
     * @param slot : number of the color
     * @return the number of cards of this color that the player have
     */
    public ReadOnlyIntegerProperty cardTypeCount(int slot){
        return cardTypeCount.get(slot);
    }

    /**
     *
     * @param r the route we want property
     * @return if the route is claimable
     */
    public ReadOnlyBooleanProperty claimable(Route r){
        int index = ChMap.routes().indexOf(r);
        return claimable.get(index);
    }

    /**
     *
     * @return if the player can draw cards
     */
    public boolean canDrawCards(){
        return publicGameState.canDrawCards();
    }

    /**
     *
     * @return if the player can draw tickets
     */
    public boolean canDrawTickets(){
        return publicGameState.canDrawTickets();
    }

    /**
     *
     * @param r the route
     * @return the List of possible combination to claim the route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route r){
        return playerState.possibleClaimCards(r);
    }
}

package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * @author Romain Logean (327230)
 * @author Shuli Jia (316620)
 */
public interface ActionHandlers {

    interface DrawTicketsHandler {
        /**
         * called when the player wants to draw a ticket
         */
        void onDrawTickets();
    }

    interface DrawCardHandler {
        /**
         * called when the player wants to draw a card
         * @param slot: slot of the card the player wants to draw
         */
        void onDrawCard(int slot);
    }

    interface ClaimRouteHandler {
        /**
         * called when the player wants to claim the route with their initial cards
         * @param route: the route the player wants to claim
         * @param cards: the initial cards of the player
         */
        void onClaimRoute(Route route, SortedBag<Card> cards);
    }

    interface ChooseTicketsHandler {
        /**
         * called when the player chose to keep the drawn tickets
         * @param tickets: set of tickets the player chose to keep
         */
        void onChooseTickets(SortedBag<Ticket> tickets);
    }

    interface ChooseCardsHandler {
        /**
         * called when the player chose to use the given cards as initial cards
         * or additional cards while claiming a route
         * if they use them as additional cards, the bag can be empty (meaning the player
         * gave up on claiming an underground route)
         * @param cards: the given cards
         */
        void onChooseCards(SortedBag<Card> cards);
    }
}

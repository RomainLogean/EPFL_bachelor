package ch.epfl.tchu.game;

import java.util.List;

import ch.epfl.tchu.Preconditions;


/**
 * @author Romain Logean (327230)
 *
 */
public class PublicCardState {
    
    private List<Card> faceUpCards;
    private int deckSize;
    private int discardSize;

    /**
     * constructs the public card state
     * @param faceUpCards: the five cards that are visible for every player
     * @param deckSize: the number of cards that are in the deck
     * @param discardsSize: the number of cards that are in the discard
     * @throws IllegalArgumentException
     *          - if the number of faced up cards is not equal to 5
     *          - if the deck size or the discard size is negative
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {

        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT &&
                deckSize >= 0 && discardsSize >= 0);

        this.faceUpCards = List.copyOf(faceUpCards);
        this.deckSize = deckSize;
        this.discardSize = discardsSize;

    }

    /**
     *
     * @return the list of visible cards
     */
    public List<Card> faceUpCards() {
        return faceUpCards;
    }

    /**
     *
     * @param slot: the index of the card we want
     * @return the card that is in the wanted slot
     * @throws IndexOutOfBoundsException if the slot is not included between 0 and 4
     */
    public Card faceUpCard(int slot) {
        if (!Constants.FACE_UP_CARD_SLOTS.contains(slot)) {
            throw new IndexOutOfBoundsException();
        }
        return this.faceUpCards.get(slot);
    }

    /**
     *
     * @return the number of cards in the deck
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     *
     * @return true if the deck is empty
     */
    public boolean isDeckEmpty() {
        return deckSize == 0;
    }

    /**
     *
     * @return the number of cards in the discard
     */
    public int discardsSize() {
        return discardSize;
    }
}

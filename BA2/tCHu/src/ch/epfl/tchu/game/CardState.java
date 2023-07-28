package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * @author Shuli JIA (316620)
 */

public final class CardState extends PublicCardState {

    private final Deck<Card> deck;
    private final SortedBag<Card> discard;

    /**
     *
     * @param faceUpCards: the list of the cards that are faced up
     * @param deckSize: the size of the deck
     * @param discardsSize: the size of the set of discards
     * @param deck: the deck
     * @param discard: the set of discards
     */
    private CardState(List<Card> faceUpCards, int deckSize, Deck<Card> deck, int discardsSize, SortedBag<Card> discard) {
        super(faceUpCards, deckSize, discardsSize);
        this.discard = discard;
        this.deck = deck;
    }

    /**
     * method used to create a card state
     * @param deck: the given deck
     * @return the card state of the given deck:
     *      the 5 first cards are faced up
     *      the deck is composed by all of the other cards left
     *      the set of discards is empty
     * @throws IllegalArgumentException if the size of the deck is smaller than
     *         the number of faced up cards
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);

        List<Card> faceUpCards = new ArrayList<>();
        Deck<Card> otherDeck = deck;
        SortedBag<Card> emptyDiscard = SortedBag.of();

        for(int slot : Constants.FACE_UP_CARD_SLOTS) {
            faceUpCards.add(otherDeck.topCard());
            otherDeck = otherDeck.withoutTopCard();
        }

        // compute the size of the deck
        int deckSize = otherDeck.size();
        // size of the discards is to be 0
        int discardsSize = emptyDiscard.size();

        return new CardState(faceUpCards, deckSize, otherDeck, discardsSize, emptyDiscard);
    }

    /**
     *
     * @param slot: index
     * @return a set of cards with the card at the index "slot" replaced by the first card of the deck
     * @throws IllegalArgumentException if the deck is empty
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(!isDeckEmpty());
        if(!(slot >= 0 && slot < Constants.FACE_UP_CARDS_COUNT)) {
            throw new IndexOutOfBoundsException();
        }

        Card topCard = deck.topCard();
        List<Card> otherFaceUpCards = new ArrayList<>();

        for(int i: Constants.FACE_UP_CARD_SLOTS) {
            if (i == slot) {
                otherFaceUpCards.add(topCard);
            } else
                otherFaceUpCards.add(faceUpCards().get(i));

        }

        Deck<Card> otherDeck = deck.withoutTopCard();
        int deckSize = otherDeck.size();
        int discardSize = this.discard.size();

        return new CardState(otherFaceUpCards, deckSize, otherDeck, discardSize, this.discard);
    }

    /**
     *
     * @return the top card of the deck
     * @throws IllegalArgumentException if the deck is empty
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!isDeckEmpty());
        return deck.topCard();
    }

    /**
     *
     * @return the set of card without the top card of the deck
     * @throws IllegalArgumentException if the deck is empty
     *
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!isDeckEmpty());

        Deck<Card> deckWithoutTopCard = deck.withoutTopCard();
        int deckWithoutTopCardSize = deckWithoutTopCard.size();

        int discardSize = this.discard.size();

        return new CardState(this.faceUpCards(), deckWithoutTopCardSize, deckWithoutTopCard,
                discardSize, this.discard);
    }

    /**
     *
     * @param rng: random number generator
     * @return the mixed set of discards that becomes the new deck
     *         because the deck is now empty
     * @throws IllegalArgumentException if the deck is empty
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(isDeckEmpty());

        Deck<Card> mixedDiscards = Deck.of(discard, rng);
        int mixedDiscardsSize = mixedDiscards.size();

        SortedBag<Card> emptyDiscard = SortedBag.of();
        int emptyDiscardSize = emptyDiscard.size();

        return new CardState(this.faceUpCards(),mixedDiscardsSize,mixedDiscards,emptyDiscardSize,emptyDiscard);
    }

    /**
     *
     * @param additionalDiscards: the set of discards
     * @return the set composed of the deck and the discards
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {

        SortedBag<Card> otherDiscards = additionalDiscards.union(this.discard);
        int otherDiscardsSize = otherDiscards.size();

        return new CardState(this.faceUpCards(), this.deckSize(), this.deck, otherDiscardsSize, otherDiscards);
    }

}

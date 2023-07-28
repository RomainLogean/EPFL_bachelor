package ch.epfl.tchu.game;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 *
 * @author Shuli JIA (316620)
 *
 * @param <C> : represents a comparable element
 */

public final class Deck<C extends Comparable<C>> {

    private final List<C> cards;

    /**
     * private constructor of a deck
     * @param cards: a sorted bag of cards that constitutes the deck
     */
    private Deck(List<C> cards) {
        this.cards = cards;
    }

    /**
     * method used to create a deck
     * @param <C> : is a comparable element
     * @param cards: the cards that are going to be mixed
     * @param rng: random number generator
     * @return the same deck but mixed with the random number generator
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {

        List<C> mixedCards = cards.toList();
        Collections.shuffle(mixedCards, rng);

        return new Deck<>(mixedCards);
    }


    /**
     *
     * @return the size of the deck
     */
    public int size() {
        return cards.size();
    }

    /**
     *
     * @return true if the deck is empty
     */
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     *
     * @return the top card of the deck
     * @throws IllegalArgumentException if the deck is empty
     */
    public C topCard() {
        Preconditions.checkArgument(!isEmpty());
        return cards.get(0);
    }

    /**
     *
     * @return another deck without its top card
     * @throws IllegalArgumentException if the deck is empty
     */
    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(!isEmpty());

        return this.withoutTopCards(1);
    }

    /**
     *
     * @param count: index at which the list stops
     * @return the top cards of the deck (until the index count)
     * @throws IllegalArgumentException if the number "count" is not included between
     *         0 and the size of the deck
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument((count >= 0) && (count <= size()));

        List<C> topCardsList = cards.subList(0, count);

        SortedBag.Builder<C> topCardsBuilder = new SortedBag.Builder<>();
        for(C c: topCardsList)
            topCardsBuilder.add(c);

        return topCardsBuilder.build();
    }

    /**
     *
     * @param count: index at which the list begins
     * @return the deck without its top cards
     * @throws IllegalArgumentException if the number "count" is not included between
     *         0 and the size of the deck
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument((count >= 0) || (count <= size()));

        List<C> withoutTopCards = cards.subList(count, cards.size());

        return new Deck<>(withoutTopCards);
    }
}

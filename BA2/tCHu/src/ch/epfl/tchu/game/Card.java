package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Romain Logean (327230)
 * @author Shuli Jia (316620)
 */
public enum Card {

    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null);

    private final Color color;

    /**
     * constructs a card
     * @param color: the color of the card
     */
    Card(Color color) {
        this.color = color;
    }

    public static final List<Card> ALL = List.of(Card.values());
    public static final int COUNT = ALL.size();
    public static final List<Card> CARS = ALL.subList(0, Color.COUNT);

    /**
     * 
     * @param color: the color we want the card to take
     * @return a card of the given color
     */
    public static Card of(Color color) {
        if(color == null) {
            return Card.LOCOMOTIVE;
        }
        return Card.valueOf(color.toString());
    }

    /**
     * 
     * @return the color of the card
     */
    public Color color(){
        return this.color;
    }
    
}

package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Romain Logean (327230)
 * @author Shuli Jia (316620)
 * all the color in tCHu
 */
public enum Color {
    
    BLACK, 
    VIOLET, 
    BLUE, 
    GREEN, 
    YELLOW, 
    ORANGE, 
    RED, 
    WHITE;

    public static final List<Color> ALL = List.of(Color.values());
    public static final int COUNT = ALL.size();
    
}

package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Romain Logean (327230)
 * @author Shuli JIA (316620)
 */
public enum PlayerId {

    PLAYER_1,
    PLAYER_2,
    PLAYER_3;

    public static List<PlayerId> ALL = new ArrayList<>();
    public static final List<PlayerId> ENUM = List.of(PlayerId.values());
    public static int COUNT = 0;

    public static void initialPlayerNumber(int number){
        Preconditions.checkArgument(number >= Constants.MINIMAL_PLAYER_COUNT &&
                number <= Constants.MAXIMAL_PLAYER_COUNT);
        COUNT = number;
        if (number == Constants.MINIMAL_PLAYER_COUNT){
            ALL= List.of(PLAYER_1,PLAYER_2);
        }
        else {
            ALL = List.of(PlayerId.values());
        }
    }

    /**
     *
     * @return the player that will play next
     */
    public PlayerId next() {
        int nextPlayerNumber = ALL.indexOf(this)+1;
        if(nextPlayerNumber < COUNT){
            return ALL.get(nextPlayerNumber);
        }
        return PLAYER_1;
    }
}

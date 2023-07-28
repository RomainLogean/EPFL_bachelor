package ch.epfl.tchu.net;

/**
 * enum of the types of messages that can be returned to the players
 * @author Shuli JIA (316620)
 *
 */
public enum MessageId {
    
    INIT_PLAYERS,
    RECEIVE_INFO,
    UPDATE_STATE,
    SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS,
    NEXT_TURN,
    CHOOSE_TICKETS,
    DRAW_SLOT,
    ROUTE,
    CARDS,
    CHOOSE_ADDITIONAL_CARDS,
    END

}

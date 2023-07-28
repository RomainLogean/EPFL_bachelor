package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ch.epfl.tchu.game.Constants.FACE_UP_CARDS_COUNT;

/**
 * @author Romain Logean (327230)
 * @author Shuli JIA (316620)
 *
 */
public class PublicGameState {

    private final int ticketsCount;
    private final PublicCardState publicCardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    /**
     *
     * @param ticketsCount: number of tickets
     * @param publicCardState: state of the cards
     * @param currentPlayerId: the id of the current player
     * @param playerState: map of the players and the id
     * @param lastPlayer: the id of the last player
     * @throws IllegalArgumentException
     *          - if the size of playerState is not equal 2
     *          - if the deck size or if the number of tickets are negative
     * @throws NullPointerException
     *          - if the public card state is null
     *          - if the id of the current player is null
     *          - if the player state is null
     */
    public PublicGameState(int ticketsCount, PublicCardState publicCardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        Preconditions.checkArgument(playerState.size() == PlayerId.COUNT && publicCardState.deckSize() >= 0 && ticketsCount >= 0);
        Objects.requireNonNull(publicCardState);
        Objects.requireNonNull(currentPlayerId);
        Objects.requireNonNull(playerState);

        this.ticketsCount = ticketsCount;
        this.publicCardState = publicCardState;
        this.currentPlayerId = currentPlayerId;
        this.playerState = playerState;
        this.lastPlayer = lastPlayer;

    }

    /**
     *
     * @return the number of tickets
     */
    public int ticketsCount() {
        return ticketsCount;
    }

    /**
     *
     * @return if the tickets can be drawn
     */
    public boolean canDrawTickets() {
        return ticketsCount >= Constants.IN_GAME_TICKETS_COUNT;
    }

    /**
     *
     * @return the state of the card
     */
    public PublicCardState cardState() {
        return publicCardState;
    }

    /**
     *
     * @return true if the cards can be drawn
     */
    public boolean canDrawCards() {
        return (publicCardState.deckSize() + publicCardState.discardsSize() >= FACE_UP_CARDS_COUNT);
    }

    /**
     *
     * @return the id of the current player
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     *
     * @param playerId: the id of the player
     * @return the public state of the player
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     *
     * @return the state of the current player
     */
    public PublicPlayerState currentPlayerState() {
        return playerState(currentPlayerId);
    }

    /**
     *
     * @return the list of all the claimed routes of the player
     */
    public List<Route> claimedRoutes() {
        List<Route> claimedRoutes = new ArrayList<>();
        for(PlayerId playerId: PlayerId.ALL) {
            claimedRoutes.addAll(playerState.get(playerId).routes());
        }

        return claimedRoutes;
    }

    /**
     *
     * @return the id of the last player
     */
    public PlayerId lastPlayer() {
        return lastPlayer;
    }
}

package ch.epfl.tchu.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import static ch.epfl.tchu.gui.StringsFr.PLAYER_STATS;

/**
 * @author Romain Logean (327230)
 * @author Shuli Jia (316620)
 */
class InfoViewCreator {

    /**
     *
     * @param playerId: the id of the player
     * @param playerName: set that maps the player id to their name
     * @param observableGameState: the observable state of the game
     * @param text: the observable list of the text
     * @return the view of the info
     */
    public static Node createInfoView(PlayerId playerId, Map<PlayerId, String> playerName,
                                      ObservableGameState observableGameState, ObservableList<Text> text) {

        VBox infoBox = new VBox();
        infoBox.getStylesheets().addAll("info.css", "colors.css");

        Separator separator = new Separator(Orientation.HORIZONTAL);
        infoBox.getChildren().add(separator);

        VBox playerStatsBox = new VBox();
        playerStatsBox.setId("player-stats");
        infoBox.getChildren().add(playerStatsBox);

        // stats of the player
        List<PlayerId> listOfPlayers = new ArrayList<>();
        for (int i = 0; i < PlayerId.COUNT; i++) {
            listOfPlayers.add(playerId);
            playerId = playerId.next();
        }
        for(PlayerId p: listOfPlayers) {

            TextFlow playerN = new TextFlow();

            if(p.equals(PlayerId.PLAYER_1)) {
                playerN.getStyleClass().add("PLAYER_1");

            } else if(p.equals(PlayerId.PLAYER_2)){
                playerN.getStyleClass().add("PLAYER_2");

            }else{
                playerN.getStyleClass().add("PLAYER_3");
            }


            playerStatsBox.getChildren().add(playerN);

            Circle filledCircle = new Circle(5.0);
            filledCircle.getStyleClass().add("filled");
            playerN.getChildren().add(filledCircle);

            Text filledText = new Text(playerName.get(p));
            playerN.getChildren().add(filledText);

            StringExpression playerStats = Bindings.format(PLAYER_STATS,
                    playerName.get(p),
                    observableGameState.ticketsCount_p(p),
                    observableGameState.cardsCount_p(p),
                    observableGameState.carsCount_p(p),
                    observableGameState.pointsCount_p(p));

            filledText.textProperty().bind(playerStats);

        }

        TextFlow messageFlow = new TextFlow();
        messageFlow.setId("game-info");

        Bindings.bindContent(messageFlow.getChildren(),text);
        infoBox.getChildren().add(messageFlow);

        return infoBox;
    }
}

package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;

import java.util.List;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * @author Romain Logean (327230)
 * @author Shuli Jia (316620)
 */
class MapViewCreator {

    public static Node createMapView(ObservableGameState observableGameState,
                              ObjectProperty<ClaimRouteHandler> claimRouteProperty,
                              MapViewCreator.CardChooser cardChooser) {
        Pane mapPane = new Pane();
        mapPane.getStylesheets().addAll("map.css","colors.css");
        mapPane.getChildren().add(new ImageView());

        for (int i = 0 ; i< ChMap.routes().size() ; i++) {
            Route r = ChMap.routes().get(i);
            Group routeGroup = new Group();
            routeGroup.setId(r.id());
            mapPane.getChildren().add(routeGroup);

            routeGroup.disableProperty().bind(
                    claimRouteProperty.isNull().or(observableGameState.claimable(r).not()));


            observableGameState.routeOwner(i).addListener( (observable, oldValue, newValue) -> {
                if(oldValue == null){
                    routeGroup.getStyleClass().add(newValue.name());
                }
            });

            String colorName = r.color() == null ? "NEUTRAL" : r.color().name() ;
            if (r.level()== Route.Level.UNDERGROUND) routeGroup.getStyleClass().add(r.level().name());
            routeGroup.getStyleClass().addAll("route", colorName);

            routeGroup.setOnMouseClicked(e -> {
                if (e.getButton() != null) {
                    List<SortedBag<Card>> options = observableGameState.possibleClaimCards(r);
                    ClaimRouteHandler claimRouteHandler = claimRouteProperty.get();
                    if (options.size() == 1) {
                        claimRouteHandler.onClaimRoute(r, options.get(0));
                    } else {
                        ChooseCardsHandler chooseCardsH =
                                chosenCards -> claimRouteHandler.onClaimRoute(r, chosenCards);
                        cardChooser.chooseCards(options, chooseCardsH);
                    }
                }
            });
            for (int j = 1; j <= r.length(); j++) {
                Group caseGroup = new Group();

                StringBuilder sb = new StringBuilder();
                sb.append(r.id());
                sb.append("_");
                sb.append(j);

                caseGroup.setId(sb.toString());

                routeGroup.getChildren().add(caseGroup);

                Rectangle voieRec = new Rectangle(36,12);
                voieRec.getStyleClass().addAll("track", "filled");
                caseGroup.getChildren().add(voieRec);

                Group wagonGroup = new Group();
                wagonGroup.getStyleClass().add("car");
                caseGroup.getChildren().add(wagonGroup);

                Rectangle wagonRec = new Rectangle(36,12);
                wagonRec.getStyleClass().add("filled");
                wagonGroup.getChildren().add(wagonRec);

                Circle wagonCir1 = new Circle(12,6,3);
                wagonGroup.getChildren().add(wagonCir1);

                Circle wagonCir2 = new Circle(24,6,3);
                wagonGroup.getChildren().add(wagonCir2);
            }
        }

        return mapPane;
    }

    @FunctionalInterface
    interface CardChooser {
        /**
         * called when the player must choose the cards he wants to use to
         * claim a route
         * @param options: the possible cards the player can play
         * @param handler: action used when the player made their choice
         */
        void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
    }

}

package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * @author Romain Logean (327230)
 * @author Shuli Jia (316620)
 */
class DecksViewCreator {

    /**
     *
     * @param observableGameState: the state of the game
     * @return the hand view
     */
        public static Node createHandView(ObservableGameState observableGameState) {

            HBox handBox = new HBox();
            handBox.getStylesheets().addAll("decks.css", "colors.css");

            ListView<Ticket> tickets = new ListView<>(observableGameState.ticketList().get());
            tickets.setId("tickets");
            handBox.getChildren().add(tickets);

            HBox handPaneBox = new HBox();
            handPaneBox.setId("hand-pane");
            handBox.getChildren().add(handPaneBox);

            int slot = 0;
            for(Card c: Card.ALL) {
                StackPane cardsStackPane = new StackPane();

                if(c.color() == null) {
                    cardsStackPane.getStyleClass().addAll("NEUTRAL", "card");
                } else {
                    cardsStackPane.getStyleClass().addAll(c.toString(), "card");
                }
                handPaneBox.getChildren().add(cardsStackPane);

                Rectangle outsideCard = new Rectangle(60, 90);
                outsideCard.getStyleClass().add("outside");
                cardsStackPane.getChildren().add(outsideCard);

                Rectangle insideCard = new Rectangle(40, 70);
                insideCard.getStyleClass().addAll("filled", "inside");
                cardsStackPane.getChildren().add(insideCard);


                Rectangle trainImage = new Rectangle(40, 70);
                trainImage.getStyleClass().add("train-image");
                cardsStackPane.getChildren().add(trainImage);

                Text colorCount = new Text();
                colorCount.getStyleClass().add("count");
                cardsStackPane.getChildren().add(colorCount);

                ReadOnlyIntegerProperty count = observableGameState.cardTypeCount(slot);
                cardsStackPane.visibleProperty().bind(Bindings.greaterThan(count, 0));
                slot ++;

                colorCount.textProperty().bind(Bindings.convert(count));
                colorCount.visibleProperty().bind(Bindings.greaterThan(count, 1));
            }

            return handBox;
        }

    /**
     *
     * @param observableGameState: the state of the game
     * @param drawTicketsHandler: action handler for drawing tickets
     * @param drawCardHandler: action handler for drawing cards
     */
    public static Node createCardsView(ObservableGameState observableGameState,
                                                    ObjectProperty<DrawTicketsHandler> drawTicketsHandler,
                                                    ObjectProperty<DrawCardHandler> drawCardHandler) {
        VBox cardsBox = new VBox();
        cardsBox.getStylesheets().addAll("decks.css","colors.css");
        cardsBox.setId("card-pane");

        Button ticketButton = new Button(StringsFr.TICKETS);
        ticketButton.getStyleClass().add("gauged");
        ticketButton.setOnMouseClicked(e -> drawTicketsHandler.get().onDrawTickets());
        ticketButton.disableProperty().bind(Bindings.isNull(drawTicketsHandler));

        Group ticketGauge =  new Group();

        Rectangle ticketBackRec = new Rectangle(50,5);
        ticketBackRec.getStyleClass().add("background");

        Rectangle ticketFrontRec = new Rectangle(50,5);
        ticketFrontRec.widthProperty().bind(
                observableGameState.ticketsPercentage().multiply(50).divide(100));
        ticketFrontRec.getStyleClass().add("foreground");

        ticketGauge.getChildren().add(ticketBackRec);
        ticketGauge.getChildren().add(ticketFrontRec);

        ticketButton.setGraphic(ticketGauge);
        cardsBox.getChildren().add(ticketButton);

        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++) {
            int slot = i;
            StackPane cardPane = new StackPane();
            cardPane.getStyleClass().add("card");
            cardPane.disableProperty().bind(Bindings.isNull(drawCardHandler));
            cardsBox.getChildren().add(cardPane);

            observableGameState.faceUpCard(slot).addListener((observable, oldValue, newValue) -> {
                if(oldValue != null){
                    String oldColorName = oldValue.color() == null ? "NEUTRAL" : oldValue.color().name();
                    cardPane.getStyleClass().remove(oldColorName);
                }
                String colorName = newValue.color()==null ? "NEUTRAL" : newValue.color().name();
                cardPane.getStyleClass().add(colorName);
            });
            cardPane.setOnMouseClicked(e -> drawCardHandler.get().onDrawCard(slot));

            Rectangle outsideRec = new Rectangle(60,90);
            outsideRec.getStyleClass().add("outside");
            outsideRec.getStyleClass().add("track");
            cardPane.getChildren().add(outsideRec);

            Rectangle insideRec = new Rectangle(40,70);
            insideRec.getStyleClass().add("inside");
            insideRec.getStyleClass().add("track");
            insideRec.getStyleClass().add("filled");
            cardPane.getChildren().add(insideRec);

            Rectangle trainRec = new Rectangle(40,70);
            trainRec.getStyleClass().add("train-image");
            trainRec.getStyleClass().add("track");
            cardPane.getChildren().add(trainRec);
        }

        Button cardButton = new Button(StringsFr.CARDS);
        cardButton.getStyleClass().add("gauged");
        cardButton.disableProperty().bind(Bindings.isNull(drawCardHandler));
        cardButton.setOnMouseClicked(e -> drawCardHandler.get().onDrawCard(Constants.DECK_SLOT));

        Group CardGauge =  new Group();

        Rectangle cardBackRec = new Rectangle(50,5);
        cardBackRec.getStyleClass().add("background");

        Rectangle cardFrontRec = new Rectangle(50,5);
        cardFrontRec.widthProperty().bind(
                observableGameState.cardsPercentage().multiply(50).divide(100));
        cardFrontRec.getStyleClass().add("foreground");

        CardGauge.getChildren().add(cardBackRec);
        CardGauge.getChildren().add(cardFrontRec);

        cardButton.setGraphic(CardGauge);
        cardsBox.getChildren().add(cardButton);

        cardsBox.visibleProperty();

        return cardsBox;
    }
}

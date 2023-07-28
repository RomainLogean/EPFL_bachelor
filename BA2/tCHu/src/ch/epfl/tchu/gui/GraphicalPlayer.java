package ch.epfl.tchu.gui;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.ActionHandlers.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import static javafx.application.Platform.isFxApplicationThread;

/**
 * @author Romain Logean (327230)
 * @author Shuli Jia (316620)
 */
public class GraphicalPlayer {

    private final ObservableGameState observableGameState;

    private final ObservableList<Text> infos;

    ObjectProperty<DrawTicketsHandler> drawTicketsHandlerProperty;
    ObjectProperty<DrawCardHandler> drawCardHandlerObjectProperty;
    ObjectProperty<ClaimRouteHandler> claimRouteHandlerObjectProperty;

    private Stage mainStage;

    /**
     * 
     * @param player: the id of the player
     * @param playerName: set that maps the player id with the player's name
     */
    public GraphicalPlayer(PlayerId player, Map<PlayerId, String> playerName) {
        assert isFxApplicationThread();

        observableGameState = new ObservableGameState(player);

        drawTicketsHandlerProperty = new SimpleObjectProperty<>();
        drawCardHandlerObjectProperty = new SimpleObjectProperty<>();
        claimRouteHandlerObjectProperty = new SimpleObjectProperty<>();

        infos = FXCollections.observableArrayList();
                                                                                                                                //(options, handler) -> chooseClaimCards(options, handler)
        BorderPane borderPane= new BorderPane(MapViewCreator.createMapView(observableGameState, claimRouteHandlerObjectProperty, this::chooseClaimCards),
                null,
                DecksViewCreator.createCardsView(observableGameState,drawTicketsHandlerProperty,drawCardHandlerObjectProperty),
                DecksViewCreator.createHandView(observableGameState),
                InfoViewCreator.createInfoView(player, playerName, observableGameState, infos));

        Scene scene = new Scene(borderPane);
        Stage mainStage = new Stage();
        this.mainStage = mainStage;
        mainStage.setOnCloseRequest(e -> {
            e.consume();
            quitConfirmation();
        });


        mainStage.setScene(scene);
        String title = String.format("tCHu \u2014 " + playerName.get(player));
        mainStage.setTitle(String.format(title));

        mainStage.show();

    }

    /**
     * 
     * @param newGameState: the state of the game
     * @param newPlayerState: the state of the player
     */
    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        assert isFxApplicationThread();

        observableGameState.setState(newGameState,newPlayerState);
    }

    /**
     * 
     * @param message: the message the method has to show on the bottom of the screen
     */
    public void receiveInfo(String message) {
        assert isFxApplicationThread();

        Text messageText = new Text(message);

        if(infos.size()==5){
            infos.remove(0);
        }
        infos.add(messageText);
    }

    /**
     * 
     * @param ticketsHandler: the action of drawing tickets
     * @param cardHandler: action of drawing cards
     * @param claimRouteHandler: action to claim a route
     */
    public void startTurn(DrawTicketsHandler ticketsHandler, DrawCardHandler cardHandler, ClaimRouteHandler claimRouteHandler) {
        assert isFxApplicationThread();

        if(observableGameState.canDrawTickets()) {
            drawTicketsHandlerProperty.set(() ->{
                ticketsHandler.onDrawTickets();
                drawTicketsHandlerProperty.set(null);
                drawCardHandlerObjectProperty.set(null);
                claimRouteHandlerObjectProperty.set(null);
            });
        }
        else {
            drawTicketsHandlerProperty.set(null);
        }

        if(observableGameState.canDrawCards()) {
            drawCardHandlerObjectProperty.set((slot) -> {
                cardHandler.onDrawCard(slot);
                drawTicketsHandlerProperty.set(null);
                claimRouteHandlerObjectProperty.set(null);
                drawCardHandlerObjectProperty.set(null);
                drawCard(cardHandler);
            });
        }
        else{
            drawCardHandlerObjectProperty.set(null);
        }

        claimRouteHandlerObjectProperty.set((route,cards) -> {
            claimRouteHandler.onClaimRoute(route,cards);
            drawTicketsHandlerProperty.set(null);
            drawCardHandlerObjectProperty.set(null);
            claimRouteHandlerObjectProperty.set(null);
        });
    }

    /**
     * 
     * @param tickets: multi set of tickets
     * @param chooseTicketsHandler: action to choose the tickets
     */
    public void chooseTickets(SortedBag<Ticket> tickets, ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();

        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(mainStage);
        stage.setTitle(StringsFr.TICKETS_CHOICE);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOnCloseRequest(e -> e.consume());

        VBox vBox= new VBox();
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");
        stage.setScene(scene);

        TextFlow textFlow = new TextFlow();

        int minTicket = tickets.size()-Constants.DISCARDABLE_TICKETS_COUNT;
        Text text = new Text(String.format(StringsFr.CHOOSE_TICKETS,minTicket,StringsFr.plural(minTicket)));
        textFlow.getChildren().add(text);
        vBox.getChildren().add(textFlow);

        ObservableList<Ticket> ticketsObservable = FXCollections.observableArrayList(tickets.toList());
        ListView<Ticket> listView = new ListView<>(ticketsObservable);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        vBox.getChildren().add(listView);

        Button button = new Button(StringsFr.CHOOSE);
        button.disableProperty().
                bind(Bindings.size(
                        listView.getSelectionModel().getSelectedItems()).
                        lessThan(minTicket));
        button.setOnAction(e ->{
            stage.hide();
            chooseTicketsHandler.onChooseTickets(SortedBag.of(listView.getSelectionModel().getSelectedItems()));
        });

        vBox.getChildren().add(button);

        stage.show();
    }

    /**
     * 
     * @param drawCardHandler: action to draw a card
     */
    public void drawCard(DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();
        drawCardHandlerObjectProperty.set(drawCardHandler);

        drawCardHandlerObjectProperty.set((slot) -> {
            drawCardHandler.onDrawCard(slot);
            drawTicketsHandlerProperty.set(null);
            claimRouteHandlerObjectProperty.set(null);
            drawCardHandlerObjectProperty.set(null);
        });
    }

    /**
     * 
     * @param initialCards: list of the set of initial cards the player can play to claim a route
     * @param chooseCardsHandler: the action of choosing cards
     */
    public void chooseClaimCards(List<SortedBag<Card>> initialCards, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();

        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(mainStage);
        stage.setTitle(StringsFr.CARDS_CHOICE);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOnCloseRequest(e -> e.consume());

        VBox vBox= new VBox();
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");
        stage.setScene(scene);

        TextFlow textFlow = new TextFlow();
        Text text = new Text(String.format(StringsFr.CHOOSE_CARDS));
        textFlow.getChildren().add(text);
        vBox.getChildren().add(textFlow);

        ObservableList<SortedBag<Card>> cardChoiceObservable = FXCollections.observableArrayList(initialCards);
        ListView<SortedBag<Card>> listView = new ListView<>(cardChoiceObservable);
        listView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));
        vBox.getChildren().add(listView);

        Button button = new Button(StringsFr.CHOOSE);
        button.disableProperty().
                bind(Bindings.size(
                        listView.getSelectionModel().getSelectedItems()).
                        isNotEqualTo(1));
        button.setOnAction(e ->{
            stage.hide();
            chooseCardsHandler.onChooseCards(listView.getSelectionModel().getSelectedItem());
        });

        vBox.getChildren().add(button);

        stage.show();
    }

    /**
     * 
     * @param additionalCards: additional cards the player can use to claim a tunnel
     * @param chooseCardsHandler: the action of choosing cards
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> additionalCards, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();

        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(mainStage);
        stage.setTitle(StringsFr.CARDS_CHOICE);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOnCloseRequest(e -> e.consume());

        VBox vBox= new VBox();
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");
        stage.setScene(scene);

        TextFlow textFlow = new TextFlow();
        Text text = new Text(String.format(StringsFr.CHOOSE_ADDITIONAL_CARDS));
        textFlow.getChildren().add(text);
        vBox.getChildren().add(textFlow);

        ObservableList<SortedBag<Card>> cardChoiceObservable = FXCollections.observableArrayList(additionalCards);
        ListView<SortedBag<Card>> listView = new ListView<>(cardChoiceObservable);
        listView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));
        vBox.getChildren().add(listView);

        Button button = new Button(StringsFr.CHOOSE);
        button.setOnAction(e ->{
            stage.hide();
            chooseCardsHandler.onChooseCards(listView.getSelectionModel().getSelectedItem());
        });

        vBox.getChildren().add(button);

        stage.show();
    }


    public void end(String endingWindow){
        assert isFxApplicationThread();

        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(mainStage);
        stage.setTitle(StringsFr.END);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOnCloseRequest(e -> e.consume());

        VBox vBox= new VBox();
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("end.css");
        stage.setScene(scene);

        vBox.getChildren().add(new ImageView());

        Text text = new Text(endingWindow);
        vBox.getChildren().add(text);

        Button button = new Button(StringsFr.QUIT);
        button.setOnAction(e -> mainStage.close());

        vBox.getChildren().add(button);

        stage.show();


    }

    private void quitConfirmation(){
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(mainStage);

        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOnCloseRequest(e -> e.consume());

        VBox vBox= new VBox();
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");
        stage.setScene(scene);

        Text message = new Text(StringsFr.CHOOSE_QUIT);

        vBox.getChildren().add(message);


        Button quitButton = new Button(StringsFr.CHOICE_QUIT);
        quitButton.setOnAction(e -> mainStage.close());

        vBox.getChildren().add(quitButton);

        Button continueButton = new Button(StringsFr.CHOICE_CONTINUE);
        continueButton.setOnAction(e -> stage.hide());

        vBox.getChildren().add(continueButton);

        stage.show();
    }

    class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

        @Override
        public String toString(SortedBag<Card> object) {

            StringJoiner sj = new StringJoiner(" ");

            Card card1 = object.get(0);
            int num1 = object.countOf(card1);
            sj.add(String.valueOf(num1));
            sj.add(Info.cardName(card1, num1));
            if (object.size() <= num1) return sj.toString();

            Card card2 = object.get(num1);
            int num2 = object.countOf(card2);
            sj.add(StringsFr.AND_SEPARATOR);
            sj.add(String.valueOf(num2));
            sj.add(Info.cardName(card2, num2));

            return sj.toString();
        }

        @Override
        public SortedBag<Card> fromString(String string) {
            throw new UnsupportedOperationException();
        }
    }
}
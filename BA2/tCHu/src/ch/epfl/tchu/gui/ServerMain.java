package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.*;

/**
 * @author Romain Logean (327230)
 * @author Shuli Jia (316620)
 */
public class ServerMain extends Application {

    private final static int PORT = 5108;
    private  static int PLAYER_NUMBER;

    /**
     * to launch the application
     * @param args the parameters : the name of the two players
     */
    public static void main(String[] args) {

        PLAYER_NUMBER = args.length;
        PlayerId.initialPlayerNumber(PLAYER_NUMBER);
        launch(args);
    }

    /**
     * start the server
     * @param primaryStage not used here
     * @throws IOException if something goes wrong
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        ServerSocket serverSocket = new ServerSocket(PORT);

        GraphicalPlayerAdapter player1 = new GraphicalPlayerAdapter();

        RemotePlayerProxy player2 = new RemotePlayerProxy(serverSocket.accept());

        Map<PlayerId, String> playerNames;
        Map<PlayerId, Player> players;

        if (PLAYER_NUMBER == Constants.MAXIMAL_PLAYER_COUNT){
            RemotePlayerProxy player3 = new RemotePlayerProxy(serverSocket.accept());

            playerNames = Map.of(PLAYER_1, getParameters().getRaw().get(0),
                    PLAYER_2, getParameters().getRaw().get(1), PLAYER_3, getParameters().getRaw().get(2));

            players = Map.of(PLAYER_1, player1, PLAYER_2, player2, PLAYER_3, player3);
        }
        else {
            playerNames = Map.of(PLAYER_1, getParameters().getRaw().get(0),
                    PLAYER_2, getParameters().getRaw().get(1));

            players = Map.of(PLAYER_1, player1, PLAYER_2, player2);
        }



        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        Random rng = new Random();
        new Thread(() -> Game.play(players, playerNames, tickets, rng)).start();
        
    }
}

package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author Romain Logean (327230)
 * @author Shuli Jia (316620)
 */
public class ClientMain extends Application {

    /**
     * to launch the application
     * @param args the parameters : the host and the port of the server
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * start the client to join the server
     * @param primaryStage not used here
     */
    @Override
    public void start(Stage primaryStage) {
        String hostName = getParameters().getRaw().get(0);
        int port = Integer.parseInt(getParameters().getRaw().get(1));

        GraphicalPlayerAdapter player2 = new GraphicalPlayerAdapter();
        RemotePlayerClient client = new RemotePlayerClient(player2, hostName, port);

        new Thread(client::run).start();
    }
}

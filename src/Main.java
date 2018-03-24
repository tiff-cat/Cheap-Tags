import gui.PrimaryStageManager;
import javafx.application.Application;
import javafx.stage.Stage;
import model.StateManager;

import static gui.PrimaryStageManager.getPrimaryStageManager;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        StateManager.startSession();
        PrimaryStageManager.setPrimaryStage(primaryStage);
        PrimaryStageManager primaryStageManager = getPrimaryStageManager();
        primaryStageManager.setDefaultScreenWidth(1080);
        primaryStageManager.setDefaultScreenHeight(720);
        primaryStageManager.setScreen("Cheap Tags", "/activities/home_screen_view.fxml");
        primaryStageManager.showStage();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

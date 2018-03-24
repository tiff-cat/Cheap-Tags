package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A class to act as the manager for windows - "stages" - in this program.
 */
public class StageManager {
    /**
     * The stage this StageManager manages
     */
    private Stage stage;

    /**
     * The default Screen Width for the window this StageManager will create
     */
    private double defaultScreenWidth = 1080;

    /**
     * The default Screen Height for the window this StageManager will create
     */
    private double defaultScreenHeight = 720;

    /**
     * A record of all StageManagers within this session
     */
    private static ArrayList<StageManager> stageManagers;

    public StageManager(Stage stage) {
        this.stage = stage;
        stageManagers = new ArrayList<>();
        stageManagers.add(this);
    }

    /**
     * Get the primary stage.
     *
     * @return the primary stage being managed
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Show the primary stage window
     */
    public void showStage() {
        getStage().show();
    }

    /**
     * Close the primary stage window
     */
    public void closeStage() {
        getStage().close();
    }

    /**
     * Set the scene for the entire primary stage using a given FXML file.
     *
     * @param windowTitle the title of the new window
     * @param fxmlPath    the path of this FXML file relative to this class.
     */
    public void setScreen(String windowTitle, String fxmlPath) {
        getStage().setTitle(windowTitle);
        getStage().setScene(new Scene(getParentWithFXMLPath(fxmlPath),
                getDefaultScreenWidth(), getDefaultScreenHeight()));
    }

    /**
     * Return a Parent object loaded with an object hierarchy from an FXML file.
     *
     * @param fxmlPath the path of the FXML file to load the Parent with. (relative to this class)
     * @return a Parent instance, loaded with the given FXML file
     */
    private Parent getParentWithFXMLPath(String fxmlPath) {
        try {
            return FXMLLoader.load(StageManager.class.getResource(fxmlPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Pane();
    }

    /**
     * Get the default width of the primary stage.
     *
     * @return the default width of this primary stage
     */
    private double getDefaultScreenWidth() {
        return defaultScreenWidth;
    }

    /**
     * Set the default width of the primary stage. This will be used when a main scene is set to stage with unspecified
     * width.
     *
     * @param width the width to set as default.
     */
    public void setDefaultScreenWidth(double width) {
        defaultScreenWidth = width;
    }

    /**
     * Get the default height of the primary stage.
     *
     * @return the default height of this primary stage
     */
    private double getDefaultScreenHeight() {
        return defaultScreenHeight;
    }

    /**
     * Set the default height of the primary stage. This will be used when a main scene is set to stage with unspecified
     * height.
     *
     * @param height the height to set as default
     */
    public void setDefaultScreenHeight(double height) {
        defaultScreenHeight = height;
    }

    static ArrayList<StageManager> getStageManagers() {
        return stageManagers;
    }

    /**
     * Set the window title of this stage.
     *
     * @param title the title to set
     */
    public void setWindowTitle(String title){
        getStage().setTitle(title);
    }
}

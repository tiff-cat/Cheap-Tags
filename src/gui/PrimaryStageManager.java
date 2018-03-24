package gui;

import javafx.stage.Stage;
import model.StateManager;

/**
 * This class consists exclusively of static methods, and delegates all communication with the
 * primary stage of the application.
 *
 * This class is final as it does not make sense to extend the PrimaryStageManager.
 */
public final class PrimaryStageManager extends StageManager {

    private static PrimaryStageManager thisPrimaryStageManager;

    /**
     * Private constructor because there is only one PrimaryStage, so there cannot be multiple instances
     *
     * @param stage the primary stage
     */
    private PrimaryStageManager(Stage stage) {
        super(stage);
    }

    /**
     * Get the one and only instance of PrimaryStageManager
     *
     * @return the PrimaryStageManager for this progream
     */
    public static PrimaryStageManager getPrimaryStageManager() {
        return thisPrimaryStageManager;
    }

    /**
     * Set the primaryStage this PrimaryStageManager will manage. A stage can only be
     * set once.
     *
     * @param stage the stage to manage.
     */
    public static void setPrimaryStage(Stage stage) {
        if (thisPrimaryStageManager == null) {
            thisPrimaryStageManager = new PrimaryStageManager(stage);

            // When the primaryStage is requested to close, do so via closeStage().
            thisPrimaryStageManager.getStage().setOnCloseRequest(event -> thisPrimaryStageManager.closeStage());
        }
    }

    /**
     * The PrimaryStageManager closes all other windows when it is closed.
     */
    @Override
    public void closeStage() {
        StateManager.endSession();
        for (StageManager stageManager : StageManager.getStageManagers()) {
            if (stageManager != null && !(stageManager instanceof PrimaryStageManager)) {
                stageManager.closeStage();
            }
        }
        this.getStage().close();
    }

}
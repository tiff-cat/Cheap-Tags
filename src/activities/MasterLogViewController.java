package activities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.StateManager;
import gui.ConfigureJFXControl;
import model.Log;

import java.net.URL;
import java.util.ResourceBundle;


public class MasterLogViewController implements Initializable {

    /**
     * Shows a table for all revision history
     */
    @FXML
    TableView<Log> masterLog;

    /**
     * the column for current name in the table view
     */
    @FXML
    TableColumn<Log, String> currentName;

    /**
     * the column for old name in the table view
     */
    @FXML
    TableColumn<Log, String> oldName;

    /**
     * the column for timeStamp in the table view
     */
    @FXML
    TableColumn<Log, String> timeStamp;


    /**
     * Stores all revision history in an observable list
     */
    ObservableList<Log> allRevisionHistory = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources){
        allRevisionHistory.clear();
        for (String name: StateManager.userData.getImageFileNames()) {
            allRevisionHistory = ConfigureJFXControl.populatedTableViewWithArrayList(masterLog,
                    StateManager.userData.getImageFileWithName(name).getImageLogs(),
                    currentName, oldName, timeStamp);
        }
    }
}
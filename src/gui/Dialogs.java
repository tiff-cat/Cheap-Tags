package gui;

import com.sun.istack.internal.Nullable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import model.StateManager;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.brunocvcunha.instagram4j.Instagram4j;
import utils.FileOperations;


import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Optional;

import static gui.PrimaryStageManager.getPrimaryStageManager;

/**
 * A class containing methods behind all the popup dialogs in the program. Contains alerts and directory-choosing popups.
 */
public class Dialogs {

    /**
     * Show an alert dialog with yes/no options
     *
     * @param windowTitle the dialog window title
     * @param header      the dialog header text
     * @param body        the dialog body text
     * @return the user response to the dialog
     */
    public static ButtonType showYesNoAlert(String windowTitle, String header, String body) {
        Alert yesNoDialog = new Alert(Alert.AlertType.CONFIRMATION, body, ButtonType.NO, ButtonType.YES);
        yesNoDialog.setTitle(windowTitle);
        yesNoDialog.setHeaderText(header);
        yesNoDialog.showAndWait();
        return yesNoDialog.getResult();
    }

    /**
     * Tell the user that they are trying to move/rename a file to/in a directory where a file with that name exists.
     *
     * @param directory the directory in which there is an existing file
     * @param fileName  the name of the file they are trying to place in the directory
     * @param filter    which names to avoid when selecting a suffixed name
     * @return the file name with a numerical suffix, or null if they don't want to rename it
     */
    public static String showFileExistsAlert(File directory, String fileName, @Nullable Collection filter) {
        // Get new file with a suffix
        String suffixedFileName = FileOperations.getSuffixedFileName(directory, fileName);

        while (filter != null && filter.contains(suffixedFileName)) {
            suffixedFileName = FileOperations.getSuffixedFileName(directory, suffixedFileName);
        }
        // Ask user if they would like a suffixed name on the image
        ButtonType renameReqResponse = Dialogs.showYesNoAlert("Could not rename file",
                "Filename Taken",
                fileName + " already exists. Would you like to name it " + suffixedFileName + "?");
        if (renameReqResponse == ButtonType.OK) {
            return suffixedFileName;
        }
        return null;
    }

    /**
     * Show an alert telling the user than an error has occured
     *
     * @param windowTitle the title of the dialog window
     * @param header      the header text for the dialog
     * @param body        the body text
     */
    public static void showErrorAlert(String windowTitle, String header, String body) {
        Alert errorAlert = new Alert(Alert.AlertType.NONE, body, ButtonType.OK);
        errorAlert.setTitle(windowTitle);
        errorAlert.setHeaderText(header);
        errorAlert.showAndWait();
    }

    /**
     * Show an alert telling the user than an error has occured
     *
     * @param windowTitle the title of the dialog window
     * @param header      the header text for the dialog
     * @param body        the body text
     * @return the string the user entered
     */
    public static String showTextInputDialog(String windowTitle, @Nullable String header, String body) {
        TextInputDialog textDialog = new TextInputDialog();
        textDialog.setTitle(windowTitle);
        if (header != null) {
            textDialog.setHeaderText(header);
        }
        textDialog.setContentText(body);
        textDialog.showAndWait();
        return textDialog.getResult();
    }

    /**
     * Show a dialog asking the user for login creds
     *
     * @param windowTitle the title of the dialog window
     * @param header      the header text for the dialog
     * @param body        the body text
     * @return a String array containing the username and password and indexes 0 and 1, respectively
     */
    public static String[] loginDialog(String windowTitle, String header, @Nullable String body) {
        /*
         Citation
         *************************************************************************************
         Title: JavaFX Custom Login Dialog
         Author: Marco Jakob
         Date: 2014
         Availability: http://code.makery.ch/blog/javafx-dialogs-official/
         */
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle(windowTitle);
        dialog.setHeaderText(header);
        if (body != null) {
            dialog.setContentText(body);
        }

        ButtonType loginBtnType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtnType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(loginBtnType, cancelBtnType);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        PasswordField password = new PasswordField();
        username.setPromptText("Username");
        password.setPromptText("Password");
        gridPane.add(username, 0, 0);
        gridPane.add(password, 0, 1);

        dialog.getDialogPane().setContent(gridPane);

        dialog.setResultConverter(dialogBtn -> {
            if (dialogBtn == loginBtnType) {
                return new String[]{username.getText(), password.getText()};
            }
            return null;
        });
        String[] loginCreds = new String[2];

        Optional<String[]> dialogReturn = dialog.showAndWait();
        dialogReturn.ifPresent(dialogResult -> {
            loginCreds[0] = dialogResult[0];
            loginCreds[1] = dialogResult[1];
        });
        return loginCreds;
    }

    /**
     * Prompt the user to select a directory using a DirectoryChooser Dialog, and return the selected directory.
     *
     * @return the directory chosen by the user. returns null if no file selected.
     */
    public static File getDirectoryWithChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a directory");
        return directoryChooser.showDialog(getPrimaryStageManager().getStage());
    }

    public static boolean showInstagramLoginDialog() {
        turnOffLog4J();
        String[] instagramCreds = Dialogs.loginDialog("Login to Instagram",
                "Enter your Instagram credentials ...", null);
        if (instagramCreds[0] != null && instagramCreds[1] != null
                && instagramCreds[0].length() > 0 && instagramCreds[1].length() > 0) {
            Instagram4j instagram = Instagram4j.builder().username(instagramCreds[0])
                    .password(instagramCreds[1]).build();
            instagram.setup();
            try {
                instagram.login();
                StateManager.sessionData.instagramReference = instagram;
                return true;
            } catch (IOException e) {
//                e.printStackTrace();
                return false;
            }
        } else {
            Dialogs.showErrorAlert("Invalid Input", "No Input",
                    "You must enter valid credentials");
        }
        return false;
    }

    /**
     * A method to turn off all apache log4j loggers.
     */
    public static void turnOffLog4J() {
        Enumeration loggers = LogManager.getCurrentLoggers();
        while (loggers.hasMoreElements()) {
            Logger logger = (Logger) loggers.nextElement();
            logger.setLevel(Level.OFF);
        }
        LogManager.getRootLogger().setLevel(Level.OFF);
    }
}

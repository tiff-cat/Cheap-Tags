package activities;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import utils.ImageFileOperations;
import model.StateManager;
import model.UserTagData;
import model.ImageFile;
import model.Tag;
import gui.Dialogs;
import utils.SearchBars;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static gui.PrimaryStageManager.getPrimaryStageManager;

public class TagScreenViewController implements Initializable {

    /**
     * Adds a tag to the system with name corresponding to user input in the TextField. Can also be accessed by pressing
     * ENTER. Displays the added tag on the screen in tagView. Does not accept empty or duplicate tag names.
     */
    @FXML
    Button add;

    /**
     * Deletes the selected tag from the system. Removes the tag name from the screen.
     */
    @FXML
    Button delete;

    /**
     * Takes user back to home screen.
     */
    @FXML
    Button home;

    /**
     * Takes the user to their last viewed directory.
     */
    @FXML
    Button lastDirectoryButton;

    /**
     * A TextField where users can enter their desired tag names.
     */
    @FXML
    TextField tagInput;

    /**
     * A TextField for users to search tags
     */
    @FXML
    TextField tagSearch;

    /**
     * Displays currently existing tags on the screen.
     */
    @FXML
    ListView<Tag> tagView;

    /**
     * The base pane for the scene.
     */
    @FXML
    Pane pane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> tagInput.requestFocus());
        tagView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // clears tagView to prevent duplication after reinitializing the scene and re-adds all the tags from UserTagData
        tagView.getItems().clear();
        for (Tag tag : UserTagData.getTagList()) {
            tagView.getItems().add(tag);
        }


    }

    /**
     * Handles when add button is clicked. Creates a tag with name = text input and adds it to the tag list on screen.
     * Empty input cannot be added, also shows alert if user trying to create duplicate tag i.e. create another tag
     * with same name.
     */
    @FXML
    public void addButtonClicked() {
        // tagInput checks if text box is empty since we cant have a tag with empty string as name.
        if (tagInput.getText() != null && !tagInput.getText().equals("")) {

            // check if the input matches an already existing tag. If it exists, show an alert. Else, proceed
            // and add the tag.
            int duplicateExists = 0;
            for (Tag eachTag : UserTagData.getTagList()) {
                if (eachTag.name.equals(tagInput.getText())) {
                    duplicateExists += 1;
                }
            }

            // duplicate is not 0, so there was an already existing tag which matched the name.
            if (duplicateExists != 0) {
                Dialogs.showErrorAlert("Error", "Duplicate Tags", "A tag with this name already exists.Please select a different name.");
                tagInput.clear();
            }

            // else there are no duplicates, proceed with adding tag to the tag list.
            else {
                Tag newTag = new Tag(tagInput.getText());
                UserTagData.addTag(newTag);
                tagView.getItems().add(newTag);
                tagInput.clear();
                tagInput.requestFocus();
            }
        }
    }

    /**
     * Handles when delete button is clicked. Removes selected Tag from the list on screen and removes selected Tag
     * from list in UserTagData.
     * When the selected tag associated with existing images, the program will show a warning box to ask user for permission.
     */
    @FXML
    public void deleteButtonClicked() {
        ArrayList<Integer> intArray = new ArrayList<>();
        intArray.addAll(tagView.getSelectionModel().getSelectedIndices());
        int deleteNum = 0;
        for (int i : intArray) {

            if (i - deleteNum > -1) {
                Tag thisTag = tagView.getItems().get(i - deleteNum);
                if (thisTag.images.size() != 0) {
                    ButtonType renameReqResponse = Dialogs.showYesNoAlert("Could Not Delete The Tag", "This Tag Associates With " + thisTag.images.size() + " Image",
                            "Are You Sure You Want To Delete?");
                    if (renameReqResponse == ButtonType.YES) {
                        for (ImageFile j : thisTag.images) {
                            j.getTagList().remove(thisTag);

                            ArrayList<Tag> tempList = new ArrayList<>();
                            tempList.addAll(j.getTagList());

                            StringBuilder sb = new StringBuilder();

                            j.getTagList().clear(); //clear all tags, since .addAll adds everything again.

                            j.getTagList().addAll(tempList);

                            for (Tag tag : tempList) {
                                sb.append("@").append(tag).append(" ");
                            }
                            sb.append(j.getOriginalName()); //.getOriginalName returns a name with .jpg at the end
                            ImageFileOperations.renameImageFile(j, sb.toString());
                        }
                        tagView.getItems().remove(i - deleteNum);
                        UserTagData.getTagList().remove(thisTag);
                        deleteNum++;
                    }
                } else {
                    tagView.getItems().remove(i - deleteNum);
                    UserTagData.getTagList().remove(thisTag);
                    deleteNum++;
                }
            }
        }
        repopulateTagView();
    }

    /**
     * Changes scene to home screen.
     */
    @FXML
    public void homeButtonClicked() {
        getPrimaryStageManager().setScreen("Cheap Tags", "/activities/home_screen_view.fxml");
    }

    /**
     * Calls add button if user presses ENTER key.
     *
     * @param ke key that the user has pressed.
     */
    @FXML
    public void handleEnterPressed(KeyEvent ke) {
        if (ke.getCode() == KeyCode.ENTER) {
            addButtonClicked();
        }
    }

    /**
     * A function that handles when a key is released when a user is typing in the search bar. Narrows down the Tag
     * results according to the user input.
     */
    @FXML
    public void searchInputChanged() {
        String input = tagSearch.getText().toLowerCase();
        SearchBars.TagSearchByText(tagView, tagView.getItems(),input);
    }

    /**
     * Refreshes the displayed Tags.
     */
    private void repopulateTagView() {
        tagView.getItems().clear();
        for (Tag tag : UserTagData.getTagList()) {
            tagView.getItems().add(tag);
        }
    }

    /**
     * Handles when the last directory button is clicked. Takes user to most recently viewed directory.
     */
    @FXML
    public void lastDirectoryButtonClicked(){
        String[] pathsVisited = StateManager.userData.getPreviousPathsVisited();
        if (pathsVisited != null && pathsVisited.length >0 ) {
            String lastDirectory = pathsVisited[pathsVisited.length - 1];
            File directoryPath = new File(lastDirectory);
            BrowseImageFilesViewController.setNewTargetDirectory(directoryPath);
            getPrimaryStageManager().setScreen("Browse Images - [~" + directoryPath.getAbsolutePath() + "]",
                    "/activities/browse_imagefiles_view.fxml");
        }
        else {
            Dialogs.showErrorAlert("Error", "No Available Directory", "There are no previously" +
                    " viewed directories! Please open a directory in the home screen.");
        }
    }
}

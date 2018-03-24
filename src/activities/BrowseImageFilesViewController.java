package activities;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import utils.ImageFileOperations;
import gui.StageManager;
import model.StateManager;
import model.ImageFile;
import model.Tag;
import model.UserTagData;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramUploadPhotoRequest;
import gui.ConfigureJFXControl;
import gui.Dialogs;
import utils.SearchBars;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gui.Dialogs.turnOffLog4J;
import static gui.PrimaryStageManager.getPrimaryStageManager;

/**
 * This class manages all activities when the user is on the Browse Images screen such as button clicks, populating
 * the screen with images.
 */
public class BrowseImageFilesViewController implements Initializable {

    /**
     * A ListView of String representing all tags in the system (displayed on right pane).
     */
    @FXML
    ListView<Tag> allTagsListView;

    /**
     * A ListView of String displaying all tags associated with the chosen file.
     */
    @FXML
    ListView<Tag> existingTags;


    /**
     * Displays the currently selected file.
     */
    @FXML
    ImageView selectedImageView;

    /**
     * Allows user to pick a new directory and put the chosen file in that directory.
     */
    @FXML
    Button changedDirectory;

    /**
     * Adds the selected tags to the files name.
     */
    @FXML
    Button rename;

    /**
     * Removes selected tag from the file's name. Once removed, it appears in allTagsListView again.
     */
    @FXML
    Button Delete;

    /**
     * Takes user back to home screen.
     */
    @FXML
    Button home;


    /**
     * Labels allTagsListView as "Tags".
     */
    @FXML
    Label Tags;

    /**
     * Quick search for image files
     */
    @FXML
    TextField imageSearchBar;

    /**
     * Displays all image files in side bar
     */
    @FXML
    TilePane imageTilePane;


    /**
     * Displays name of the currently selected file above itself.
     */
    @FXML
    Label nameOfSelectedFile;

    /**
     * Load a list view of names of all images
     */
    @FXML
    ToggleButton toggleButton;


    /**
     * Display names of images as a list view
     */
    @FXML
    ListView<String> imageNamesListView;

    /**
     * Show the image of instagram
     */
    @FXML
    Button shareWithInstagramBtn;

    /**
     * Quick search for tags
     */
    @FXML
    TextField TagSearchBar;

    /**
     * take user to the revision history window
     */
    @FXML
    Button revisionLogButton;

    /**
     * Takes the user to the tag screen.
     */
    @FXML
    Button myTagsButton;

    /**
     *
     */
    @FXML
    Button viewParentButton;


    private Label selectedImageLabel;

    /**
     * Store the image files names as an observable list
     */
    private ObservableList<String> imageNamesObservable;

    /**
     * Stores the selected directory File object.
     */
    private static File targetDirectory;

    /**
     * Store available tag options in an observable list
     */
    private ObservableList<Tag> availableTagOptions;

    /**
     * Store existing tags on image file in an observable list
     */
    private ObservableList<Tag> existingTagsOnImageFile;

    /**
     * Store the image names
     */
    private Collection<String> imageNames;

    /**
     * check if the change that user made on the image is saved or not
     */
    private boolean unsavedChanges = false;

    /**
     * Use to prepare for image search by regex
     */
    private StringBuilder imageSearchPatternEnd;

    /**
     * The File object that is the currently displayed image.
     */
    ImageFile selectedImageFile = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Enable listviews to be able to display objects with the same type as their type parameter
        ConfigureJFXControl.setListViewToDisplayCustomObjects(existingTags);
        ConfigureJFXControl.setListViewToDisplayCustomObjects(allTagsListView);

        // Populate the listview of tag options
        availableTagOptions = ConfigureJFXControl.populateListViewWithArrayList(allTagsListView, UserTagData.getTagList());

        toggleButton.setSelected(false);
        imagesViewToggle();

        prepImageSearchRegex();

        imageNames = StateManager.sessionData.getImageFileNames();

        imageTilePane.setOrientation(Orientation.HORIZONTAL);
        imageTilePane.setVgap(0);
        populateImageTilePane();

        rename.setDisable(true);
        System.out.println(imageSearchPatternEnd);
    }

    /**
     * Handle the click on the image view in the tile pane
     * Display the selected image view to the screen, and load data for the image
     */
    @FXML
    public void chooseImageClick() {
        checkForUnsavedChanges();
        String selectedImage = imageNamesListView.getSelectionModel().getSelectedItem();
        if (imageNamesListView.getItems().indexOf(selectedImage) > -1) {
            selectedImageFile = StateManager.sessionData.getImageFileWithName(selectedImage);
            if (selectedImageFile != null) {
                Image image = new Image(selectedImageFile.getThisFile().toURI().toString());
                selectedImageView.setImage(image);
                nameOfSelectedFile.setText(selectedImageFile.getCurrentName());
                populateImageFileTagListViews();
            }
        }
    }

    // Button click handlers

    /**
     * Handles when a tag is clicked and add the selected tag under the selected image and removes the tag from
     * allTagsListView.
     */
    @FXML
    public void addButtonClick() {
        Tag selectedTag = allTagsListView.getSelectionModel().getSelectedItem();
        if (selectedImageFile == null) {
            Dialogs.showErrorAlert("Error", "Nothing selected", "No image file has been selected yet. Please select a image file first.");

        } else if (allTagsListView.getItems().indexOf(selectedTag) > -1) {
            if (selectedImageFile.getTagList().contains(selectedTag)) {
                Dialogs.showErrorAlert("Error", "", "The selected file already contains this tag.");
            } else {
                availableTagOptions.remove(selectedTag);
                existingTagsOnImageFile.add(selectedTag);
                unsavedChanges = true;
                rename.setDisable(false);
                Delete.setDisable(false);
            }
        }
    }

    /**
     * Deletes the tag from selected tags when user clicks the button or clicks the tag. Deleted tag will reappear in
     * allTagsListView.
     */
    @FXML
    public void deleteButtonClick() {
        Tag selectedTag = existingTags.getSelectionModel().getSelectedItem();
        if (selectedImageFile == null) {
            Dialogs.showErrorAlert("Error", "Nothing selected", "No image file has been selected yet. Please select a image file first.");
        } else if (existingTags.getItems().size() > 0 && selectedTag != null) {

            existingTagsOnImageFile.remove(selectedTag);
            availableTagOptions.add(selectedTag);

            if (existingTagsOnImageFile.size() == 0) {
                Delete.setDisable(true);
            }
            unsavedChanges = true;
            rename.setDisable(false);
        }
    }

    /**
     * Renames the file name in the user's operating system.
     * Modifies the tagsList of selected image, and stores the data
     */
    @FXML
    public void setTagsBtnClick() {
        if (selectedImageFile == null) {
            Dialogs.showErrorAlert("Error", "Nothing selected ", "No image file has been selected yet. Please select a image file first.");
        } else {
            selectedImageFile.updateTagHistory(selectedImageFile.getTagList()); //Add the tag list to the tag history before updating.

            StringBuilder sb = new StringBuilder();

            selectedImageFile.getTagList().clear(); //clear all tags, since .addAll adds everything again.

            selectedImageFile.getTagList().addAll(existingTagsOnImageFile);
            for (Tag tag : existingTagsOnImageFile) {
                sb.append("@").append(tag).append(" ");
            }
            sb.append(selectedImageFile.getOriginalName()); //.getOriginalName returns a name with .jpg at the end
            imageNames.remove(selectedImageFile.getCurrentName());
            if (imageNamesObservable != null) {
                imageNamesObservable.remove(selectedImageFile.getCurrentName());
            }
            selectedImageFile = ImageFileOperations.renameImageFile(selectedImageFile, sb.toString());

            unsavedChanges = false;
            rename.setDisable(true);
            if (existingTagsOnImageFile.size() != 0) {
                Delete.setDisable(false);
            }
            nameOfSelectedFile.setText(selectedImageFile.getCurrentName());
            imageNames.add(selectedImageFile.getCurrentName());
            if (imageNamesObservable != null) {
                imageNamesObservable.add(selectedImageFile.getCurrentName());
            }
            for (Tag i : selectedImageFile.getTagList()) {
                i.images.add(selectedImageFile);
            }
            selectedImageLabel.setText(selectedImageFile.getCurrentName());
        }
    }

    /**
     * Handles the click of the toggleButton
     * show the list view of the names of image files
     */
    @FXML
    public void imagesViewToggle() {
        if (toggleButton.isSelected()) {
            imageTilePane.setVisible(false);
            imageSearchBar.setVisible(false);
            imageNamesListView.setVisible(true);
            ArrayList<String> imageNamesArrayList = new ArrayList<>();
            imageNamesArrayList.addAll(imageNames);
            // set imageFileNames for the toggle that allows images to be viewed as text (file names)
            if (imageNamesObservable != null) {
                imageNamesObservable.clear();
            }
            imageNamesObservable = ConfigureJFXControl.populateListViewWithArrayList(imageNamesListView, imageNamesArrayList);
        } else {
            imageTilePane.setVisible(true);
            imageSearchBar.setVisible(true);
            imageNamesListView.setVisible(false);
        }
    }

    /**
     * Moves the image to a new directory which the user selects. After moving an image, the user can go to that new
     * directory or stay in the current directory.
     */
    @FXML
    public void moveImageButtonClick() {

        checkForUnsavedChanges();
        if (selectedImageFile == null) {
            Dialogs.showErrorAlert("Error", "Nothing selected",
                    "No image file has been selected yet. Please select a image file first.");
        } else {
            File newDirectory = Dialogs.getDirectoryWithChooser();
            File movedFile = ImageFileOperations.moveImageFile(selectedImageFile, newDirectory);
            if (movedFile != null) {
                File newDirectoryLocation = movedFile.getParentFile();
                ButtonType response = Dialogs.showYesNoAlert("Go To Directory", null, "Would you like to go " +
                        "to the new directory?");
                if (response == ButtonType.YES) {
                    // set screen to new directory
                    setNewTargetDirectory(newDirectoryLocation);
                } else {
                    setNewTargetDirectory(targetDirectory);
                }
                getPrimaryStageManager().setScreen("Browse Images - [~" + targetDirectory.getPath() + "]",
                        "/activities/browse_imagefiles_view.fxml");
            }
        }
    }

    /**
     * Takes user to the home screen when back button is clicked.
     */
    @FXML
    public void homeButtonClick() {
        checkForUnsavedChanges();
        getPrimaryStageManager().setScreen("Cheap Tags", "/activities/home_screen_view.fxml");
    }

    /**
     * Handles the click on move directory button
     *
     * @param directory the directory to browse
     *
     */
    static void setNewTargetDirectory(File directory) {
        StateManager.userData.addPathToVisitedList(directory.getAbsolutePath());
        StateManager.sessionData.startNewSession(directory);
        targetDirectory = directory;
    }

    /**
     * Prepares for the search by regular expression in the image search bar
     */
    private void prepImageSearchRegex() {
        imageSearchPatternEnd = new StringBuilder(".*\\b(");
        for (String extension : ImageFileOperations.ACCEPTED_EXTENSIONS) {
            imageSearchPatternEnd.append(extension);
            imageSearchPatternEnd.append("|");
        }
        imageSearchPatternEnd.deleteCharAt(imageSearchPatternEnd.lastIndexOf("|"));
        imageSearchPatternEnd.append(")\\b");
    }

    /**
     * Populate the ImageTilePane with all the images in this session
     */
    private void populateImageTilePane() {
        for (ImageFile imageFile : StateManager.sessionData.getNameToImageFileMap().values()) {
            addImageToTilePane(imageFile);
        }
    }

    /**
     * Add a new image to the tilepane using its corresponding ImageFile object
     *
     * @param imageFile the ImageFile of the image to add
     */
    private void addImageToTilePane(ImageFile imageFile) {
        Image image = null;
        try {
            // Set the Image object to show the image associated with this ImageFile
            image = new Image(imageFile.getThisFile().toURI().toURL().toString(), 300,
                    300, true, true);
        } catch (MalformedURLException e) {

            Dialogs.showErrorAlert("Gallery Error", "Error", "There was an error adding " +
                    imageFile.getThisFile().getAbsolutePath() + " to the gallery. You sure it exists?");
        }
        // Construct an ImageView for the image
        ImageView imageView = new ImageView();
        imageView.setImage(image);

        // Setup VBox to display both image and a label with the imagename
        VBox tilePaneVBox = new VBox();

        // Construct a BEAUTIFUL label
        Label imageNameLabel = new Label(imageFile.getCurrentName());
        imageNameLabel.setPadding(new Insets(20, 0, 0, 0));
        imageNameLabel.setTextFill(Color.web("#000000"));
        ConfigureJFXControl.setFontOfLabeled("/resources/fonts/Roboto-Regular.ttf", 17, imageNameLabel);
        tilePaneVBox.setAlignment(Pos.CENTER);
        imageView.setOnMouseClicked(event -> imageClicked(imageFile, imageNameLabel));
        // Add imageview and label to vbox + add vbox to tilepane
        tilePaneVBox.getChildren().addAll(imageView, imageNameLabel);
        imageTilePane.getChildren().add(tilePaneVBox);
    }

    /**
     * Process a click on an image on the tile pane
     *
     * @param imageFile the ImageFile that was clicked
     */
    private void imageClicked(ImageFile imageFile, Label imageNameLabel){
        try {
            // Before navigating to the clicked image, alert the user if they have unset tags
            checkForUnsavedChanges();
            selectedImageLabel = imageNameLabel;
            // Keep a reference to the selected image and set up right pane attributes for selected image
            selectedImageFile = imageFile;
            selectedImageView.setImage(new Image(selectedImageFile.getThisFile().toURI().toURL().toString(), true));
            nameOfSelectedFile.setText(selectedImageFile.getCurrentName());
            populateImageFileTagListViews();

            String imagePath = selectedImageFile.getThisFile().getPath();
            getPrimaryStageManager().setWindowTitle("Browse Images - [~" + imagePath + "]");

        } catch (MalformedURLException e) {
            Dialogs.showErrorAlert("Image Error", "Error", "There was an error fetching " +
                    imageFile.getThisFile().getAbsolutePath() + " from the gallery. You sure it exists?");
        }
    }

    /**
     * Check if the user has unsaved changes and alert them if they do.
     */
    private void checkForUnsavedChanges() {
        if (unsavedChanges) {
            ButtonType saveChangesResponse = Dialogs.showYesNoAlert("Save Your Changes", "Save Changes?",
                    "You forgot to hit Set Tags! Would you like us to set your new tags?");
            if (saveChangesResponse == ButtonType.YES) {
                setTagsBtnClick();
            }
            unsavedChanges = false;
        }
    }

    /**
     * Populate all listviews to correspond to the newly selected ImageFile
     */
    void populateImageFileTagListViews() {
        // Clear "Existing Tags" listview from previous image then set for new image
        if (existingTagsOnImageFile != null) {
            existingTagsOnImageFile.clear();
        }
        existingTagsOnImageFile = ConfigureJFXControl.populateListViewWithArrayList(existingTags, selectedImageFile.getTagList());

        // Reset the available tag options, and remove all the tags that already belong to the selected ImageFile
        availableTagOptions.clear();
        availableTagOptions.addAll(UserTagData.getTagList());
        availableTagOptions.removeAll(existingTagsOnImageFile);

    }

    /**
     * Handle text changed on the image search bar
     */
    public void imageSearchTextChanged() {
        String input = imageSearchBar.getText().toLowerCase().replace("@", "");
        ArrayList<ImageFile> searchResultImageFileList = new ArrayList<>();
        String fullPattern;
        if (input.startsWith("^") && input.endsWith("$")) {
            fullPattern = input.substring(1, input.length() - 1);
        } else if (input.startsWith("^")) {
            // User is currently typing a regex. Must wait until they complete.
            return;
        } else {
            fullPattern = ".*\\b(" + Pattern.quote(input) + ")" + imageSearchPatternEnd.toString();
        }

        Pattern imageSearchPattern = Pattern.compile(fullPattern);
        Matcher imageSearchMatcher;
        imageTilePane.getChildren().clear();
        if (input.isEmpty()) {
            searchResultImageFileList.clear();
            populateImageTilePane();
        } else {
            for (String name : imageNames) {
                imageSearchMatcher = imageSearchPattern.matcher(name.toLowerCase());
                if (imageSearchMatcher.find()) {
                    searchResultImageFileList.add(StateManager.sessionData.getImageFileWithName(name));
                }
            }
            for (ImageFile imf : searchResultImageFileList) {
                addImageToTilePane(imf);
            }
        }
    }

    /**
     * Handles the text field as a search bar
     * Loads the input from user and search it through the list of tags
     */
    public void TagSearchTextChanged() {
        String input = TagSearchBar.getText().toLowerCase();
        SearchBars.TagSearchByText(allTagsListView, availableTagOptions,input);
    }

    /**
     * A function to handle the click action of the ImageView (the instagram icon) in the BrowseImageFilesView
     * This function prompts the user for their Instagram credentials and a caption, and then posts the current selected
     * ImageFile to their Instagram.
     * <p>
     * Note that this function uses Instagram's private API because their public API does not allow photo upload from
     * third parties.
     */
    public void shareWithInstagram() {
        if (selectedImageFile == null) {
            Dialogs.showErrorAlert("Must select an image", "Select an Image",
                    "You must select an image first!");
            return;
        }

        Instagram4j instagram = StateManager.sessionData.instagramReference;
            if (instagram == null){
                turnOffLog4J();
                String[] instagramCreds = Dialogs.loginDialog("Instagram Login",
                        "Enter your Instagram credentials ...", null);
                if (instagramCreds[0] != null && instagramCreds[1] != null
                        && instagramCreds[0].length() > 0 && instagramCreds[1].length() > 0) {
                    instagram = Instagram4j.builder().username(instagramCreds[0])
                            .password(instagramCreds[1]).build();
                    instagram.setup();
            }else {
                    Dialogs.showErrorAlert("Invalid Input", "No Input",
                            "You must enter valid credentials");
                }
        }
        sendInstagramPostRequest(instagram);
    }

    /**
     * A helper function for shareWithInstagram. This function logs in and sends the photo request to instagram.
     *
     * @param instagram the instance of Instagam4j used to setup Instagam4j
     */
    private void sendInstagramPostRequest(Instagram4j instagram) {
        try {
            instagram.login();
            try {
                String caption = Dialogs.showTextInputDialog("Instagram Caption", "Caption?",
                        "Enter a caption for your photo");
                if (caption == null) {
                    caption = "";
                }
                StringBuilder sb = new StringBuilder();
                for(Tag i : selectedImageFile.getTagList()){
                    String tag = "@" + i.toString() + " ";
                    sb.append(tag);
                }
                caption = caption + " " + sb.toString();
                InstagramUploadPhotoRequest photoRequest = new
                        InstagramUploadPhotoRequest(selectedImageFile.getThisFile(), caption);
                instagram.sendRequest(photoRequest);
            } catch (IOException | RuntimeException e) {
                Dialogs.showErrorAlert("Upload Error", "Error", "Please ensure the photo is JPG and" +
                        " the credentials are correct");
            }
        } catch (IOException e) {
            Dialogs.showErrorAlert("Invalid Credentials", "Invalid Creds",
                    "Please enter a valid username and password.");
        }
    }

    /**
     * A function handle the click of revision log button
     * pops up new window with revision history
     * stores current page to revision log view controller.
     */
    @FXML
    public void revisionLogButtonClick() {
        if (selectedImageFile == null) {
            Dialogs.showErrorAlert("Error", "Nothing selected", "No image file has been selected yet. Please select a image file first.");
        } else {
            // store the data on the current screen for revisionLogViewController
            RevisionLogViewController.setBrowseController(this);
            StageManager revisionLog = new StageManager(new Stage());
            revisionLog.setDefaultScreenHeight(400);
            revisionLog.setDefaultScreenWidth(600);
            revisionLog.setScreen("Revision History", "/activities/revision_log_view.fxml");
            revisionLog.showStage();
        }

    }

    /**
     * Takes the user to the tag screen.
     */
    @FXML
    public void myTagsButtonClicked(){
        checkForUnsavedChanges();
        getPrimaryStageManager().setScreen("My Tags", "/activities/tag_screen_view.fxml");
    }

    /**
     * Takes the user to the parent directory screen
     */
    @FXML
    public void viewParentButtonClicked(){
        if (selectedImageFile == null){
            Dialogs.showErrorAlert("Error", "Nothing selected",
                    "No image file has been selected yet. Please select a image file first.");

        }
        else{
            try{
            File file = new File (selectedImageFile.getThisFile().getParentFile().toString());
            Desktop desktop = Desktop.getDesktop();
            desktop.open(file);}
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}
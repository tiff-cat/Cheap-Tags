package activities;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import gui.StageManager;
import model.StateManager;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramUserFeedRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedItem;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import gui.ConfigureJFXControl;
import gui.Dialogs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;

import java.util.ResourceBundle;

import static gui.Dialogs.turnOffLog4J;
import static gui.PrimaryStageManager.getPrimaryStageManager;

/**
 * This class manages activities on the home screen.
 */
public class HomeScreenViewController implements Initializable {

    /**
     * A list of hyperlinks showing directories that have previously been opened.
     */
    @FXML
    ListView<Hyperlink> previouslyViewedListView;

    /**
     * The title above the list of previously viewed directories.
     */
    @FXML
    Label previouslyViewedLabel;

    /**
     * A button which asks the user to choose a directory, then opens images under that directory under a new screen.
     */
    @FXML
    Button openDirectoryButton;

    /**
     * A button which takes the user to the Tag screen.
     */
    @FXML
    Button myTagsButton;

    /**
     * Button that initiates process of importing images from Instagram.
     */
    @FXML
    Button importFromInstagramBtn;


    /**
     * A button which asks the user to choose a directory which the images will be imported to, then asks the user to
     * enter the tumblr URL where they want to download images from. After images are downloaded into the directory,
     * the screen will open on that directory containing tumblr photos.
     */
    @FXML
    Button tumblrBtn;

    /**
     * The background image of the home screen.
     */
    @FXML
    ImageView backgroundImage;

    /**
     * The logo "Cheap Tags"
     */
    @FXML
    ImageView logo;

    @FXML
    Button masterLogButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ConfigureJFXControl.populateListViewWithArrayList(previouslyViewedListView,
                getHyperlinkArrayList(StateManager.userData.getPreviousPathsVisited()));
        ConfigureJFXControl.setFontOfLabeled("/resources/fonts/Roboto-Regular.ttf",
                15, previouslyViewedLabel);
        ConfigureJFXControl.setFontOfLabeled("/resources/fonts/Roboto-Light.ttf",
                15, openDirectoryButton, myTagsButton, importFromInstagramBtn, tumblrBtn);
        ConfigureJFXControl.toggleHoverTextColorOfLabeled(Color.web("#2196fe"),
                Color.BLACK, openDirectoryButton, myTagsButton, importFromInstagramBtn, tumblrBtn, masterLogButton);
    }

    /**
     * Function to handle "Open Directory" button click on Home screen. Opens the chosen directory and moves to
     * the image-browsing screen.
     */
    public void openDirectoryClick() {
        File selectedFile = Dialogs.getDirectoryWithChooser();
        if (selectedFile != null) {
            switchToToBrowseImageFilesView(selectedFile);
        }
    }

    /**
     * Given an array of paths, get an arrayList of hyperlinked paths
     *
     * @param pathArray an array of paths
     * @return an ArrayList of hyperlinked paths
     */
    private ArrayList<Hyperlink> getHyperlinkArrayList(String[] pathArray) {
        ArrayList<Hyperlink> hyperlinkArrayList = new ArrayList<>();
        for (String path : pathArray) {
            hyperlinkArrayList.add(0, getHyperlinkWithPathName(path));
        }
        return hyperlinkArrayList;
    }

    /**
     * Create a hyperlink with a given path, and set its action to  switch to the BrowseImageFilesView
     *
     * @param path the path to hyperlink
     * @return the hyperlinked path
     */
     private Hyperlink getHyperlinkWithPathName(String path) {
        Hyperlink hyperlink = new Hyperlink(path);
        hyperlink.setOnAction(event -> switchToToBrowseImageFilesView(new File(path)));
        ConfigureJFXControl.toggleHoverTextColorOfLabeled(Color.BLUE, Color.BLACK, hyperlink);
        hyperlink.setTextFill(Color.BLACK);
        hyperlink.setUnderline(false);
        hyperlink.setVisited(false);
        ConfigureJFXControl.setFontOfLabeled("/resources/fonts/Roboto-Thin.ttf", 20, hyperlink);
        return hyperlink;
    }

    /**
     * Takes an File argument and opens that File in the image-browsing screen.
     *
     * @param directoryPath The directory that is to be opened.
     */
     private void switchToToBrowseImageFilesView(File directoryPath) {
        StateManager.userData.addPathToVisitedList(directoryPath.getPath());
        BrowseImageFilesViewController.setNewTargetDirectory(directoryPath);
        if (StateManager.sessionData.getNameToImageFileMap().values().size() > 0) {
            getPrimaryStageManager().setScreen("Browse Images - [~" + directoryPath.getAbsolutePath() + "]",
                    "/activities/browse_imagefiles_view.fxml");
        } else {
            Dialogs.showErrorAlert("No Files to Load", "Uh oh!", "We didn't find any image files" +
                    " in the directory you loaded. Please select another");
            openDirectoryClick();
        }
    }

    /**
     * Imports the first 20 images to the chosen directory from the instagram that the user entered.
     */
    public void importFromInstagram() {
        File chosenDirectory = Dialogs.getDirectoryWithChooser();
        if (chosenDirectory != null) {
            if (StateManager.sessionData.instagramReference == null) {
                Dialogs.showInstagramLoginDialog();
            }
            ArrayList<String> codeList = getInstagramPhotoCodes();
            ArrayList<String> directUrls = getInstagramDirectUrls(codeList);
            writeUrlToFile(directUrls, chosenDirectory);
            switchToToBrowseImageFilesView(chosenDirectory);

        }
    }

    /**
     * Returns a list of url strings where each individual image is sourced.
     *
     * @param photoCodes the instagram photo's id
     * @return ArrayList of String containing urls to each individual image
     */
    private ArrayList<String> getInstagramDirectUrls(ArrayList<String> photoCodes) {
        ArrayList<String> directUrls = new ArrayList<>();
        for (String photoCode : photoCodes) {
            try {
                String INSTAGRAM_API_URL = "https://api.instagram.com/oembed/?url=http://instagram.com/p/";
                CloseableHttpResponse response = getHttpResponse(INSTAGRAM_API_URL + photoCode);
                JSONObject json = getJSONObject(response);
                String directURL = null;
                if (json != null) {
                    directURL = json.getString("thumbnail_url");
                }
                CloseableHttpResponse directImageResponse = getHttpResponse(directURL);
                // check the direct link to the image works
                if (directImageResponse != null && directImageResponse.getStatusLine().getStatusCode() == 200) {
                    directUrls.add(directURL);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return directUrls;
    }

    /**
     * Gets an array of instagram photo codes using the Instagram4j API.
     *
     * @return an ArrayList of string representing the each photo's id.
     */
    private ArrayList<String> getInstagramPhotoCodes() {
        ArrayList<String> instagramPhotoIds = new ArrayList<>();
        Instagram4j instagramRef = StateManager.sessionData.instagramReference;
        try {
            turnOffLog4J();
            InstagramFeedResult feed = instagramRef.sendRequest(new InstagramUserFeedRequest(instagramRef.getUserId()));
            if (feed != null && feed.getItems() != null) {
                for (InstagramFeedItem item : feed.getItems()) {
                    instagramPhotoIds.add(item.getCode());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instagramPhotoIds;
    }

    /**
     * A function that handles when the import from tumblr button is clicked. Prompts user to choose a directory and
     * enter a tumblr URL. Using the tumblr API, imports first 20 images from the blog to the chosen directory and
     * opens the image browsing screen on that directory.
     */
    @FXML
    public void tumblrButtonClicked() {
        File chosenDirectory = Dialogs.getDirectoryWithChooser();
        if (chosenDirectory != null) {
            String blogName = Dialogs.showTextInputDialog("Import From Tumblr blog", null,
                    "Please enter a Tumblr URL");
            if (blogName != null) {
                String TUMBLR_API_KEY = "3ty3TDhh79GPAJBoVy25768p81ApgqiyYTp59ugyD19ncgQdh0";
                CloseableHttpResponse response = getHttpResponse("https://api.tumblr.com/v2/blog/" +
                        blogName + "/posts/photo?&api_key=" + TUMBLR_API_KEY);
                // response is not null and equal to 200 i.e. success code
                if (response != null && response.getStatusLine().getStatusCode() == 200) {
                    JSONObject json = getJSONObject(response);
                    if (json != null) {
                        ArrayList<String> urlArray = getTumblrPhotoUrls(json);
                        if (urlArray != null && !urlArray.isEmpty()) {
                            writeUrlToFile(urlArray, chosenDirectory);
                            switchToToBrowseImageFilesView(chosenDirectory);
                        }
                    }
                } else {
                    Dialogs.showErrorAlert("Error", "Not a valid tumblr URL",
                            "The URL entered was not a valid tumblr blog. Please try again.");
                }
            }
        }
    }

    /**
     * Returns the url where the specific tumblr photo is located.
     *
     * @param json The JSON object to search for the url in
     * @return String of the photo's url location, otherwise null if it is not found
     */
    private ArrayList<String> getTumblrPhotoUrls(JSONObject json) {
        ArrayList<String> result = new ArrayList<>();
        try {
            JSONObject responseJson = json.getJSONObject("response");
            JSONArray posts = responseJson.getJSONArray("posts");
            for (int i = 0; i < posts.length(); i++) {
                JSONObject currPost = posts.getJSONObject(i);
                JSONArray photoArray = currPost.getJSONArray("photos");
                for (int j = 0; j < photoArray.length(); j++) {
                    JSONObject photoObj = photoArray.getJSONObject(j);
                    JSONArray photoSpecs = photoObj.getJSONArray("alt_sizes");
                    String photoUrlString = photoSpecs.getJSONObject(0).getString("url");
                    result.add(photoUrlString);
                }
            }
            return result;
        } catch (JSONException j) {
            j.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the HttpResponse object created from requesting GET from a uri.
     *
     * @param uriString string of the URI to be accessed
     * @return returns the response object from that page
     */
    private CloseableHttpResponse getHttpResponse(String uriString) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(uriString);
        try {
            return httpClient.execute(httpGet);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Given a valid HTTP response, creates a JSON object from given entity.
     *
     * @param response the Http response to retrieve entity from
     * @return return the JSONObject created from http entity, or null if JSONObject not created
     */
    private JSONObject getJSONObject(CloseableHttpResponse response) {
        HttpEntity entity = response.getEntity();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return new JSONObject(sb.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Given a list of URLs containing only an image, retrieves image and writes a new File object on the
     * user's computer to the chosen directory with that image.
     *
     * @param urlArray        The list of URLs as strings.
     * @param chosenDirectory The directory where the user wants the images written to.
     */
    private void writeUrlToFile(ArrayList<String> urlArray, File chosenDirectory) {
        for (String urlString : urlArray) {
            try {
                URL url = new URL(urlString);
                try {
                    BufferedImage image = ImageIO.read(url);
                    File outputfile = new File(chosenDirectory.getAbsolutePath() +
                            File.separator + getUniqueNameFromUrl(urlString));
                    ImageIO.write(image, "png", outputfile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Return a String of the unique name of an image located at the end of URL (after the last '/' character).
     *
     * @param urlString the url containing link to image and unique name.
     * @return a String of the unique name.
     */
    private String getUniqueNameFromUrl(String urlString) {
        int startIndex = urlString.lastIndexOf("/");
        return urlString.substring(startIndex, urlString.length());
    }

    /**
     * Changes the current screen to the Tag screen.
     */
    public void openTagScreen() {
        getPrimaryStageManager().setScreen("My Tags", "/activities/tag_screen_view.fxml");
    }

    /**
     * Change the current screen to the tag screen
     */
    public void openMasterLog() {
        StageManager masterLog = new StageManager(new Stage());
        masterLog.setDefaultScreenHeight(400);
        masterLog.setDefaultScreenWidth(600);
        masterLog.setScreen("All Revision History", "/activities/master_log_view.fxml");
        masterLog.showStage();
    }
}
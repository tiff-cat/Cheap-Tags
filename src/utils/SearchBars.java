package utils;

import gui.ConfigureJFXControl;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import model.UserTagData;
import model.Tag;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * the class helps all text fields to be used as search bars
 */
public class SearchBars  {

    /**
     * this method takes an input from user and searches throw the tag manager,
     * adds all result to an observable list to show on the screen
     *
     * @param listView the list to populate
     * @param data the observable list of searching results
     * @param input users input in text field
     *
     */
    public static void TagSearchByText(ListView listView, ObservableList data, String input ){
        ArrayList searchResult = new ArrayList<>();

        Pattern tagSearchPattern = Pattern.compile(input);
        Matcher tagSearchMatcher;

        data.clear();
        if (input.isEmpty()) {
            searchResult.clear();
            data = ConfigureJFXControl.populateListViewWithArrayList(listView, UserTagData.getTagList());
        } else {
            for (Tag tag : UserTagData.getTagList()) {
                tagSearchMatcher = tagSearchPattern.matcher(tag.toString().toLowerCase());
                if (tagSearchMatcher.find()) {
                    searchResult.add(tag);
                }
            }
            data.addAll(searchResult);
        }
    }
}

package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;

/**
 * A series of convenience functions for javafx.scene.control.Control objects
 */
public abstract class ConfigureJFXControl {

    /**
     * Set the font text font of any item that is an instance of Labeled, using a font file.
     *
     * @param fontPath     the path of the font file you wish to use. Eg. resources/fonts/myFont.ttf
     * @param fontSize     the size of the text on the Labeled instance.
     * @param labeledItems one or more instances of Labeled on which the font should be applied
     */
    public static void setFontOfLabeled(String fontPath, double fontSize, Labeled... labeledItems) {
        Font font = Font.loadFont(ConfigureJFXControl.class.getResourceAsStream(fontPath), fontSize);
        for (Labeled labeledItem : labeledItems) {
            labeledItem.setFont(font);
        }
    }

    /**
     * Set a simple animation to change text color on mouse enter and mouse exit of instances of Labeled
     *
     * @param mouseEnterColor the color to set the text to when the mouse enters/hovers over the instance of Labeled
     * @param mouseExitColor  the color to set the text to when the mouse is exits the instance of Labeled
     * @param labeledItems    one or more instances of Labeled on which to apply this animation
     */
    public static void toggleHoverTextColorOfLabeled(Color mouseEnterColor, Color mouseExitColor, Labeled... labeledItems) {
        for (Labeled labeledItem : labeledItems) {
            labeledItem.setOnMouseEntered(event -> labeledItem.setTextFill(mouseEnterColor));
            labeledItem.setOnMouseExited(event -> labeledItem.setTextFill(mouseExitColor));
        }
    }

    /**
     * Set the listview to display string representation of custom objects.
     * Precondition: The type parameter of the listview should have a toString() method defined.
     * <p>
     * Source:https://stackoverflow.com/questions/36657299/how-can-i-populate-a-listview-in-javafx-using-custom-objects
     *
     * @param listView the listview on which to set the cell factory
     * @param <T>      the type parameter of the listview
     */
    public static <T> void setListViewToDisplayCustomObjects(ListView<T> listView) {
        listView.setCellFactory(param -> new ListCell<T>() {
            @Override
            protected void updateItem(T obj, boolean empty) {
                super.updateItem(obj, empty);

                if (empty || obj == null || obj.toString() == null) {
                    setText(null);
                } else {
                    setText(obj.toString());
                }
            }

        });

    }

    /**
     * Populate a given ListView with a given ArrayList of data by adding the data to an ObservableList (which acts
     * as the data source for the ListView). Returns a reference to the ObservableList so the user can modify
     * the ListView's data.
     *
     * @param listView      the ListView to populate
     * @param dataArrayList the data with which listView should be initially populated.
     * @param <T>           the type of the items that listView will hold, and that dataArrayList already contains.
     * @return a reference to the data source that is set to listView
     */
    public static <T> ObservableList<T> populateListViewWithArrayList(ListView<T> listView, ArrayList<T> dataArrayList) {
        ObservableList<T> observableList = listView.getItems();
        if (observableList == null) {
            observableList = FXCollections.observableArrayList();
        }
        for (T item : dataArrayList) {
            observableList.add(item);
        }
        listView.setItems(observableList);
        return observableList;
    }

    /**
     * Populate a given table view with a given array list
     * by converting array list in the array list to log object, and storing in an observable list
     * the table contains three column
     * using setCellValueFactory to set those three column
     * return an observable list
     *
     * @param tableView the table to populate
     * @param data      the array list to work with
     * @param column1   shows the first index of the array list under data
     * @param column2   shows the second index of the array list under data
     * @param column3   shows the third index of the array list under data
     * @return a reference to the table view as an ObservableList
     */
    public static ObservableList populatedTableViewWithArrayList(TableView tableView, ArrayList data,
                                                                 TableColumn column1,
                                                                 TableColumn column2,
                                                                 TableColumn column3) {
        ObservableList observableList = tableView.getItems();
        if (observableList == null) {
            observableList = FXCollections.observableArrayList();
        }
        observableList.addAll(data);


        column1.setCellValueFactory(new PropertyValueFactory<>("currentName"));
        column2.setCellValueFactory(new PropertyValueFactory<>("oldName"));
        column3.setCellValueFactory(new PropertyValueFactory<>("timeStamp"));

        tableView.setItems(observableList);
        return observableList;
    }
}

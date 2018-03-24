package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class keeps track of all the existing tags in the system.
 */
public class UserTagData implements Serializable {

    /**
     * A list of all the tags in the system.
     */
    private static ArrayList<Tag> tagList = new ArrayList<>();

    /**
     * Add a new tag.
     *
     * @param newTag the tag to be added to the list of tags.
     */
    public static void addTag(Tag newTag) {
        tagList.add(newTag);
    }

    /**
     * Returns the list of tags in the system.
     */
    public static ArrayList<Tag> getTagList() {
        return tagList;
    }

    /**
     * searches the Tag manager for a Tag with name stringOfTag, since no two tags should have the same name.
     *
     * @param stringOfTag The name of the tag we are searching for
     * @return The Tag with name matching stringOfTag. Returns null if there is no tag with that name.
     */
    public static Tag getTagByString(String stringOfTag) {
        for (Tag i : UserTagData.tagList) {
            if (i.toString().equals(stringOfTag)) {
                return i;
            }
        }
        return null;
    }

    /**
     * Clears all old tags in the system and replaces it with Tags in newList.
     *
     * @param newList The ArrayList of tags which we want to replace existing tags in the Tag manager with.
     */
    public static void setTagList(ArrayList<Tag> newList) {
        tagList = newList;
    }
}

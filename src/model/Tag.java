package model;

import java.io.Serializable;
import java.util.TreeSet;

/**
 * the {@code Tag} class is used to take users input in the program
 * and convert the input to a Tag object for further use
 */
public class Tag implements Serializable {

    /**
     * the name of the tag
     */
    public String name;

    /**
     * the ArrayList stores all images that have this tag
     */
    public TreeSet<ImageFile> images = new TreeSet<>();

    /**
     * Constructs a new model Tag object.
     *
     * @param name the name of the tag
     */
    public Tag(String name) {
        this.name = name;
    }

    /**
     * Returns the string representation of the Tag i.e. the name.
     *
     * @return String returns the tag objects name value
     */
    public String toString() {
        return name;
    }

    /**
     * Return true if the object compared is also a Tag with the same name.
     *
     * @param o the object being compared
     * @return boolean True if the object is a Tag with the same name, return false otherwise.
     */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        } else {
            Tag newo = (Tag) o;
            return newo.name.equals(this.name);
        }
    }
}

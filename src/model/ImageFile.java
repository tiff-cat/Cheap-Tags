package model;

import utils.ImageFileOperations;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * ImageFile represents actual image file in operating system.
 * After the filemanager passes the file to this class, model.ImageFile will construct an ImageFile object on it.
 * Any operations inside this class will not manipulate the actual file, but the data inside the userdata.
 *
 * @author Renjie Li
 * @see ImageFileOperations
 * @see Tag
 * @since 1.0
 */
public class ImageFile implements Serializable, Comparable<ImageFile> {

    /**
     * the most current name of this image
     */
    private StringBuilder currentName;

    /**
     * the list of tag this image has.
     */
    private ArrayList<Tag> tagList;

    /**
     * imageLogs keeps track of all of the revision histories in the format of arraylist [newname,previous name,timestamp]
     */
    private ArrayList<Log> imageLogs;

    /**
     * the original name of this image without any tag.
     */
    private String originalName;

    /**
     * the path for the parent folder of this image.
     */
    private String underWhichDirectory;

    /**
     * the actual file object.
     */
    private File thisFile;

    /**
     * the type of the string(eg. ".jpeg").
     */
    private String imageType;

    /**
     * stores the tag history for ImageFile.
     */
    private ArrayList<ArrayList<Tag>> tagHistory;

    /**
     * Constructs a new model.ImageFile object.
     *
     * @param oneImageFile is the actual imagefile(eg.image.jpeg)
     */

    public String origName;

    public ImageFile(File oneImageFile) {
        currentName = new StringBuilder();
        originalName = oneImageFile.getName();
        currentName.append(originalName);
        imageLogs = new ArrayList<>();
        underWhichDirectory = oneImageFile.getParent();
        thisFile = oneImageFile;
        String c = oneImageFile.getName();
        String[] split = c.split("\\.");
        imageType = ("." + split[split.length - 1]);
        tagList = new ArrayList<>();
        tagHistory = new ArrayList<>();
        origName = oneImageFile.getName();
    }

    /**
     * override the generalReNameFunction but only take one parameter.
     * Change inner information of an imagefile class based on given String
     *
     * @param newName the newname for the imagefile
     */
    public void generalReName(String newName) {
        String tempName = currentName.toString();
        currentName = new StringBuilder();
        currentName.append(newName);
        Long timeStamp = System.currentTimeMillis();
        Log temLog = new Log(currentName.toString(), tempName, timeStamp.toString());
        this.imageLogs.add(temLog); //Add new entry to revision history
        String targetName = this.underWhichDirectory + tempName + this.imageType;
        this.thisFile = new File(targetName);
    }

    /**
     * It updates tag history
     *
     * @param newEntry an arraylist of tag
     */
    public void updateTagHistory(ArrayList<Tag> newEntry) {
        ArrayList<Tag> temp = new ArrayList<>();
        temp.addAll(newEntry);
        this.tagHistory.add(temp);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ImageFile)) {
            return false;
        } else {
            ImageFile temp = (ImageFile) obj;
            if (this.getThisFile().getAbsolutePath().equals(temp.getThisFile().getAbsolutePath())) {
                return true;
            }
            return false;
        }
    }

    //overload compareto method, let the image can be ordered in alphabetical order.
    public int compareTo(ImageFile i) {
        if (this.originalName.charAt(0) < i.originalName.charAt(0)) {
            return -1;
        } else if (this.originalName.charAt(0) > i.originalName.charAt(0)) {
            return 1;
        } else {
            return 0;
        }
    }

    //some getters
    public String getCurrentName() {
        return this.currentName.toString();
    }
    public void setCurrentName(String newName){
        this.currentName = new StringBuilder(newName);
    }

    public String getOriginalName() {
        return this.originalName;
    }

    public ArrayList<Log> getImageLogs() {
        return this.imageLogs;
    }

    public File getThisFile() {
        return this.thisFile;
    }

    public ArrayList<Tag> getTagList() {
        return this.tagList;
    }

    //some setters
    public void setFile(File newFile) {
        this.thisFile = newFile;
    }
}
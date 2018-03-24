package model;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class stores all ImageFile data from every session.
 */
public class UserImageFileData implements Serializable {

    /**
     * An ArrayList of all the previous directories the user has visited.
     */
    private ArrayList<String> previousPathsVisited = new ArrayList<>();

    /**
     * An ArrayList of all the tags
     */
    public ArrayList<Tag> allTags;

    /**
     * The name to image file map
     */
    public HashMap<String, ImageFile> nameToImageFileMap = new HashMap<>();


    public HashMap<String, HashSet<ImageFile>> directoryToImageFileMap = new HashMap<>();

    /**
     * Get the ImageFile associated with the given name
     *
     * @param imageName the image name
     * @return the imagefile with the imageName
     */
    public ImageFile getImageFileWithName(String imageName) {
        if (nameToImageFileMap.containsKey(imageName)){
            return nameToImageFileMap.get(imageName);
        }
        return null;
    }

    /**
     * Get the ImageFile associated with the given file
     *
     * @param file the file object corresponding to the image.
     * @return the imagefile with the imageName
     */
    public ImageFile getImageFileWithFile(File file) {
        // First check to see if the file exists under the file objects generic name
        ImageFile rawNameImageFile = getImageFileWithName(file.getName());
        if (rawNameImageFile != null && rawNameImageFile.getThisFile().getAbsolutePath()
                .equals(file.getAbsolutePath())){
            return rawNameImageFile;
        }else {
            // Image may be under a name with a subdirectory prefix. Loop through all imagefiles.
            for (ImageFile imageFile : getNameToImageFileMap().values()){
                if (imageFile.getThisFile().getAbsolutePath().equals(file.getAbsolutePath())){
                    return imageFile;
                }
            }
        }
        return null;
    }

    /**
     * Add an ImageFile to the main map containing all ImageFiles on record. If the name exists
     *
     * @param imageFile the ImageFile to add
     */
    public void addImageFileToMap(ImageFile imageFile) {
        File file = imageFile.getThisFile();
        // get imagefile that's already in records (if it exists)
        ImageFile existingImageFile = getImageFileWithName(imageFile.getCurrentName());

        if (existingImageFile != null){
            if (!existingImageFile.getThisFile().getAbsolutePath().equals(file.getAbsolutePath())){
                String newName = file.getAbsolutePath();
                int slashIndex = StringUtils.lastOrdinalIndexOf(newName, File.separator, 2);
                newName = newName.substring(slashIndex, newName.length());
                imageFile.setCurrentName(newName);
                getNameToImageFileMap().put(newName, imageFile);
            }
        }else{
            getNameToImageFileMap().put(imageFile.getCurrentName(), imageFile);
        }
//        addToImageFileByDirectoryMap(file.getParentFile(), imageFile);
    }

    /**
     * Reset the key of the ImageFile in the main HashMap of all ImageFiles
     *
     * @param oldName the old name of this image
     */
    public void resetImageFileKey(String oldName) {
        if (getNameToImageFileMap().containsKey(oldName)) {
            ImageFile renamedImageFile = getNameToImageFileMap().get(oldName);
            getNameToImageFileMap().remove(oldName);
            addImageFileToMap(renamedImageFile);
        }
    }

    /**
     * Get a collection of names of all ImageFiles on record.
     *
     * @return a collection of all names.
     */
    public Collection<String> getImageFileNames() {
        return new ArrayList<>(nameToImageFileMap.keySet());
    }

    /**
     * Get a reference to the main HashMap containing all ImageFiles on record
     *
     * @return a HashMap of image names to their corresponding ImageFile
     */
    public HashMap<String, ImageFile> getNameToImageFileMap() {
        return nameToImageFileMap;
    }

    /**
     * Check if an image with the given name exists in the main HashMap of ImageFiles
     *
     * @param imageName the name of the image
     * @return if it exists in the map
     */
    public boolean existsInMap(String imageName) {
        return nameToImageFileMap.containsKey(imageName);
    }

    /**
     * Check if a given file object corresponds to an ImageFile in the main HashMap of ImageFiles
     *
     * @param file the file object corresponding to this image
     * @return returns true iff the image is present in our database.
     */
    public boolean existsInMap(File file) {
        boolean rawNameExists = existsInMap(file.getName());
        if (!rawNameExists){
            String imagePath = file.getAbsolutePath();
            for (ImageFile imageFile : nameToImageFileMap.values()){
                if (imageFile.getThisFile().getAbsolutePath().equals(imagePath)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Add a path to the visited paths list
     *
     * @param path the path to add
     */
    public void addPathToVisitedList(String path) {
        if (previousPathsVisited.contains(path)) {
            previousPathsVisited.remove(path);
        }
        previousPathsVisited.add(path);
    }

    /**
     * Get an array of the previous paths visited
     *
     * @return an array of previous paths
     */
    public String[] getPreviousPathsVisited() {
        return previousPathsVisited.toArray(new String[previousPathsVisited.size()]);
    }

//    /**
//     *
//     */
//    public void addToImageFileByDirectoryMap(File directory, ImageFile imageFile){
//        String path = directory.getAbsolutePath();
//
//        if (directoryToImageFileMap.containsKey(path)){
//            HashSet<ImageFile> imageFileSet = directoryToImageFileMap.get(path);
//            boolean addedToSet = imageFileSet.add(imageFile);
//            if (!addedToSet){
//                imageFileSet.remove(imageFile);
//                imageFileSet.add(imageFile);
//            }
//        }
//    }
}

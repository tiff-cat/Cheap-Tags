package utils;

import com.sun.istack.internal.Nullable;
import model.ImageFile;

import model.StateManager;
import model.Tag;
import model.UserTagData;
import gui.Dialogs;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Arrays;

import static utils.FileOperations.*;
import static utils.FileOperations.FileOperationsResponse.FAILURE;
import static utils.FileOperations.FileOperationsResponse.FILENAME_TAKEN;
import static utils.FileOperations.FileOperationsResponse.SUCCESS;

/**
 * This class handles how file operations are <b>handled</b> within the program.
 * <p>
 * Note: This class differs from utils/FileOperations in that the latter deals with the OS-related operations,
 * while this class handles the consequences of those operations (ie.
 */
public class ImageFileOperations {

    public static String[] ACCEPTED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".bmp", ".tif"};

    /**
     * Rename a given ImageFile but only take two parameters.
     *
     * @param imageFile the image to rename
     * @param newName   the new name
     * @return a ImageFile object with the new name if successfully renamed, the old File object otherwise
     */
    public static ImageFile renameImageFile(ImageFile imageFile, String newName) {
        File currentImageFile = imageFile.getThisFile();
        Path imageFilePath = Paths.get(currentImageFile.getParentFile().getAbsolutePath());

        FileOperationsResponse response = renameFile(currentImageFile, newName);
        if (response == SUCCESS) {
            String oldName = imageFile.getCurrentName();
            imageFile.generalReName(newName);
            StateManager.userData.resetImageFileKey(oldName);
            StateManager.sessionData.resetImageFileKey(oldName);
            imageFilePath = Paths.get(imageFilePath.toAbsolutePath().toString(), newName);
        } else if (response == FILENAME_TAKEN) {
            String suffixedFileName = Dialogs.showFileExistsAlert(currentImageFile.getParentFile(), newName,
                    StateManager.userData.getImageFileNames());
            // User accepted to a suffixed filename
            if (suffixedFileName != null) {
                return renameImageFile(imageFile, suffixedFileName);
            }
        } else {
            // Show error alert dialog
            Dialogs.showErrorAlert("Renaming Error", "Error",
                    "There was an error renaming your file");
        }
        imageFile.setFile(imageFilePath.toFile());
        return imageFile;
    }

    /**
     * Move an image to another directory
     *
     * @param imageFile the imageFile representing the image to move
     * @return File file in its new directory or null if file not moved
     */
    public static File moveImageFile(ImageFile imageFile, File newDirectory) {
        String oldName = imageFile.getCurrentName();

        File oldFile = imageFile.getThisFile();

        // If user clicks cancel on directory dialog, end function.
        if (newDirectory == null) {
            return null;
        }
        // A file object of the imageFile in the new directory
        File newFile = new File(newDirectory, oldFile.getName());
        FileOperationsResponse response = moveFile(oldFile, newDirectory.toPath());

        if (response == FILENAME_TAKEN) {
            String suffixedFileName = Dialogs.showFileExistsAlert(newDirectory, newFile.getName(), null);

            if (suffixedFileName != null) {
                imageFile.generalReName(suffixedFileName);
                moveFile(imageFile.getThisFile(), newDirectory.toPath());
            } else {
                // Don't move
                newFile = null;
            }
        } else if(response == FAILURE) {
            Dialogs.showErrorAlert("Move Error", "Error", "There was an error moving your file");
        }
        imageFile.setFile(newFile);
        StateManager.sessionData.resetImageFileKey(oldName);
        StateManager.userData.resetImageFileKey(oldName);
        return newFile;
    }

    /**
     * Fetch images in a given directory and handle various possible events.
     *
     * @param directory the directory to fetch from
     */
    public static void fetchImageFiles(File directory) {
        ArrayDeque<String> acceptedExtensions = new ArrayDeque<>();
        acceptedExtensions.addAll(Arrays.asList(ACCEPTED_EXTENSIONS));
        try {
            // Get a list of files from the directory that have an accepted extension
            ArrayDeque<File> filesFromDir = fetchFromDirectory(directory, acceptedExtensions);
            for (File file : filesFromDir) {
                if (StateManager.userData.existsInMap(file)) {
                    // The file name already exists in our records
                    ImageFile imageFile = StateManager.userData.getImageFileWithFile(file);
                    processFetchedImageFile(null, imageFile);

                } else {
                    // Image is new. Process it as new
                    processFetchedImageFile(file, null);
                }
            }
        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
            Dialogs.showErrorAlert("Error", "Fetch Error",
                    "There was an error fetching your files. You sure that folder exists?");

        }
    }

    /**
     * Process a fetched image file by adding it to the session and load list.
     * Precondition: one of file or existingImageFile must be null. Pass in file if the file being imported
     * is not in our records. Pass in existingImageFile (from records) if the file being imported exists and we have
     * an ImageFile for the file.
     *
     * @param file              the file being imported (if this files ImageFile isn't stored already), null otherwise
     * @param existingImageFIle the ImageFile for the file being imported (iff it exists in records), null otherwise
     */
    private static void processFetchedImageFile(@Nullable File file,
                                                @Nullable ImageFile existingImageFIle) {
        ImageFile fileToProcess;
        if (file == null) {
            fileToProcess = existingImageFIle;
        } else {
            fileToProcess = new ImageFile(file);

            // Check to see if there are tags in the new file, that don't already exist in our database
            String[] beginningName = fileToProcess.getCurrentName().split("\\s");
            for (String i : beginningName) {
                if (i.startsWith("@")) {
                    String withoutSymbol = i.substring(1, i.length());
                    if (UserTagData.getTagByString(withoutSymbol) == null) {
                        Tag tempTag = new Tag(withoutSymbol);
                        fileToProcess.getTagList().add(tempTag);
                        tempTag.images.add(fileToProcess);
                        UserTagData.addTag(tempTag);
                    } else {
                        Tag tempTag = new Tag(withoutSymbol);
                        fileToProcess.getTagList().add(tempTag);
                        tempTag.images.add(fileToProcess);
                    }

                }
            }
            StateManager.userData.addImageFileToMap(fileToProcess);
        }
        StateManager.sessionData.addImageFileToMap(fileToProcess);
    }
}



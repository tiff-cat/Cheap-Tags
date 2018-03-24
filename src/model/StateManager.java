package model;

import exceptions.DirectoryCreationException;
import exceptions.FileNotCreatedException;
import model.UserImageFileData;
import model.UserSessionData;
import model.UserTagData;

import java.io.*;

import static gui.PrimaryStageManager.getPrimaryStageManager;

/**
 * A class to manage states for the program, including session state and user data state.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class StateManager {
    /**
     * Container for all user data in records
     */
    public static UserImageFileData userData;

    /**
     * Container for all session data
     */
    public static UserSessionData sessionData;

    /**
     * Start a new session
     */
    public static void startSession() {
        reloadState();
        sessionData = new UserSessionData();
    }

    /**
     * End a session
     */
    public static void endSession() {
        userData.allTags = UserTagData.getTagList();
        saveState(userData);
    }

    /**
     * Attempt to load a state from a previous session
     */
    private static void reloadState() {
        File dataFile = new File("data/data.ctags");
        if (!dataFile.exists()) {
            userData = new UserImageFileData();
        } else {
            FileInputStream fileInputStream;
            ObjectInputStream objectInputStream;
            try {
                fileInputStream = new FileInputStream(dataFile);
                objectInputStream = new ObjectInputStream(fileInputStream);
                userData = (UserImageFileData) objectInputStream.readObject();
                UserTagData.setTagList(userData.allTags);
                objectInputStream.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                dataFile.delete();
                getPrimaryStageManager().closeStage();
            }
        }
    }

    /**
     * Attempt to store all new data from this session, including ImageFiles and Tags.
     *
     * @param userImageFileData the main data manager for this session.
     */
    private static void saveState(UserImageFileData userImageFileData) {
        FileOutputStream fileOutputStream;
        ObjectOutputStream objectOutputStream;
        File dataFile = new File("data/data.ctags");
        try {
            if (!dataFile.exists()) {
                createDataFile(dataFile);
            }
            fileOutputStream = new FileOutputStream(dataFile);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(userImageFileData);
            objectOutputStream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    private static void createDataFile(File dataFile) {
        if (!dataFile.exists()) {
            File dir = new File(dataFile.getParentFile().getAbsolutePath());
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    try {
                        throw new DirectoryCreationException("There was an error making a directory!");
                    } catch (DirectoryCreationException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                dir.delete();
                dir.mkdirs();
            }
            try {
                if (!dataFile.createNewFile()) {
                    throw new FileNotCreatedException("There was an error creating a new data file!");
                }
            } catch (IOException | FileNotCreatedException e) {
                e.printStackTrace();
            }
        }
    }
}



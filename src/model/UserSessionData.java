package model;

import utils.ImageFileOperations;
import org.brunocvcunha.instagram4j.Instagram4j;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

/**
 * This class holds all user data for the duration of the ongoing session.
 */
public class UserSessionData extends UserImageFileData implements Serializable {
    public Instagram4j instagramReference;

    /**
     * Set this session using the given path as the identifier for the session. Note that the instagram session,
     * if it exists, is not cleared after a new session is started.
     *
     * @param directory the directory this session will browse.
     */
    public void startNewSession(File directory) {
        this.nameToImageFileMap.clear();
        ImageFileOperations.fetchImageFiles(directory);
    }

    /**
     * Functions in exactly the same way as {@link UserImageFileData#getNameToImageFileMap() the parent's version}
     * but has a <pre>public</pre> access modifier.
     */
    @Override
    public HashMap<String, ImageFile> getNameToImageFileMap() {
        return nameToImageFileMap;
    }

}

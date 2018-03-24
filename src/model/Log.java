package model;

import java.io.Serializable;

/**
 * the {@code Log} class is used to store the current name, old name and time stamp of an image file
 * used in activities/RevisionLogViewController to generate the table view of revision history
 *
 * @author Caroline Ming
 */
public class Log implements Serializable{
    /**
     * the current name of the image file
     */
    private String currentName;

    /**
     * the old name of the image file
     */
    private String oldName;

    /**
     * the time stamp of the image file
     */
    private String timeStamp;

    /**
     * construct a new log with three variables
     *
     * @param current current name
     * @param old     old name
     * @param time    time stamp
     */
    public Log(String current, String old, String time) {
        this.currentName = current;
        this.oldName = old;
        this.timeStamp = time;
    }

    /**
     * Get the current name as a string
     *
     * @return String current name
     */
    public String getCurrentName() {
        return currentName;
    }

    /**
     * Get the old name as a string
     *
     * @return String current name
     */
    public String getOldName() {
        return oldName;
    }

    /**
     * Get the time stamp as a string
     *
     * @return String current name
     */
    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * Set the current name as a SimpleStringProperty
     */
    public void setCurrentName(String name) {
//        SimpleStringProperty current = new SimpleStringProperty(name);
        currentName = name;
    }

    /**
     * Set the old name as a
     */
    public void setOldName(String name) {
        oldName = name;

    }

    /**
     * Set the time stamp
     */
    public void setTimeStamp(String time) {
        timeStamp = time;

    }

    @Override
    public String toString() {
        StringBuilder logString =  new StringBuilder();
        logString.append(currentName +", ");
        logString.append(oldName + ", ");
        logString.append(timeStamp);

        return logString.toString();

    }

}


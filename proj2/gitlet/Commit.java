package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Charles
 */
public class Commit implements Serializable, Dumpable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;

    /** The date of this Commit. */
    private Date date;

    /** The author of this Commit. */
    private String author;

    /** The parent of this Commit. */
    private String parentID;

    @Override
    public String toString() {
        return "Commit{" +
                "message='" + message + '\'' +
                ", date=" + date +
                ", author='" + author + '\'' +
                ", parentID='" + parentID + '\'' +
                '}';
    }

    public Commit(String message, Date date, String author, String parentID) {
        this.message = message;
        this.date = date;
        this.author = author;
        this.parentID = parentID;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

    public String getParentID() {
        return parentID;
    }

    @Override
    public void dump() {
        System.out.println("Commit{" +
                "message='" + message + '\'' +
                ", date=" + date +
                ", author='" + author + '\'' +
                ", parentID='" + parentID + '\'' +
                '}');
    }
}

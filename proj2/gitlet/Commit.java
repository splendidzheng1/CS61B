package gitlet;

// TODO: any imports you need here

import javax.swing.*;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import static gitlet.Utils.sha1;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Charles
 */
public class Commit implements Serializable, Dumpable {
    /** The message of this Commit. */
    private String message;

    /** The date of this Commit. */
    private Date date;

    /** The author of this Commit. */
    private String author;

    /** The parent of this Commit. */
    private String parentID1;

    /** The parent of this Commit. */
    private String parentID2;

    /** The sha1 of this Commit. */
    private String sha1;

    /** The files of this Commit. */
    private TreeMap<String, String> blobs;

    public Commit(String message, Date date, String parentID1, String parentID2, TreeMap<String, String> blobs) {
        this.message = message;
        this.date = date;
        this.parentID1 = parentID1 == null ? "" : parentID1;
        this.parentID2 = parentID2 == null ? "" : parentID2;
        if (parentID1 != null) {
            this.blobs = blobs;
        }
        if (this.blobs == null) {
            this.blobs = new TreeMap<>();
        }
        this.sha1 = "";
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

    public String getParentID1() {
        return parentID1;
    }

    public void setParentID1(String parentID1) {
        this.parentID1 = parentID1;
    }

    public String getParentID2() {
        return parentID2;
    }

    public void setParentID2(String parentID2) {
        this.parentID2 = parentID2;
    }

    public String getSha1() {
        if (sha1.isEmpty()) {
            List<Object> obj = new ArrayList<>();
            obj.add(date.toString());
            obj.add(parentID1);
            obj.add(parentID2);
            obj.add(message);
            for (Object value : blobs.values()) {
                try {
                    obj.add(Files.readAllBytes(Utils.join(Repository.OBJ_DIR, value.toString()).toPath()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            sha1 = sha1(obj);
        }
        return sha1;
    }

    public TreeMap<String, String> getBlobs() {
        return blobs;
    }

    @Override
    public void dump() {
        System.out.println("Commit{" +
                "message='" + message + '\'' +
                ", date=" + date +
                ", author='" + author + '\'' +
                ", parentID1='" + parentID1 + '\'' +
                ", parentID2='" + parentID2 + '\'' +
                '}');
    }
}

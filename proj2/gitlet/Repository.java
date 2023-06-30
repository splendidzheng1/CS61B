package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Charles
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The commit directory inside .gitlet. */
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    /** The commit directory inside .gitlet. */
    public static final File OBJ_DIR = join(GITLET_DIR, "objects");
    /** The commit directory inside .gitlet. */
    public static final File STAGE_FILE = join(GITLET_DIR, "index");

    /**
     * Initial gitlet, create .gitlet directory,
     * make a new commit and a new branch(master).
     */
    public static void initCommand() {
        if (GITLET_DIR.exists()) {
            message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        // create initial directory
        GITLET_DIR.mkdir();
        REFS_DIR.mkdir();
        OBJ_DIR.mkdir();
        // make a new commit
        Commit commit = new Commit("initial commit", new Date(0L), "Charles", "");
        // make a new branch
        List<Object> obj = new ArrayList<>();
        obj.add(commit.getAuthor());
        obj.add(commit.getDate().toString());
        obj.add(commit.getParentID());
        obj.add(commit.getMessage());
        String sha1 = sha1(obj);
        writeObject(join(REFS_DIR, sha1), commit);
    }

    /**
     * Add file to staged area.
     * Exit if the file doesn't exist.
     */
    public static void addCommand(String filename) {
        File addFile = join(CWD, filename);
        if (!addFile.exists()) {
            message("File does not exist.");
        }
        List<Object> obj = new ArrayList<>();
        obj.add(String.valueOf(addFile.length()));
        obj.add(new byte[1]);
        obj.add(readContents(addFile));
        String sha1File = sha1(obj);
        // Store file.
        writeContents(join(OBJ_DIR, sha1File));
        // Add relationship to stage area.
        Stage stage;
        if (STAGE_FILE.exists()) {
            stage = readObject(STAGE_FILE, Stage.class);
        } else {
            stage = new Stage();
        }
        stage.add(filename, sha1File);
        writeObject(STAGE_FILE, stage);
    }
}

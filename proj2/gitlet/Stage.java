package gitlet;

import java.io.Serializable;
import java.util.TreeMap;

/** Represents stage area in gitlet.
 *
 *  Staged for addition and removal
 *  Record where these files are in .gitlet/objects
 *
 *  @author Charles
 */
public class Stage implements Serializable, Dumpable {

    /** The info of files added. */
    private TreeMap<String, String> addition;

    /** The info of files removed. */
    private TreeMap<String, String> removal;

    @Override
    public String toString() {
        return "Stage{" +
                "addition=" + addition +
                ", removal=" + removal +
                '}';
    }

    public Stage() {
        addition = new TreeMap<>();
        removal = new TreeMap<>();
    }

    public void add(String filename, String sha1File) {
        addition.put(filename, sha1File);
    }

    @Override
    public void dump() {
        System.out.println("Stage{" +
                "addition=" + addition +
                ", removal=" + removal +
                '}');
    }
}

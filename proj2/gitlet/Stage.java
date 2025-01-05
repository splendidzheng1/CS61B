package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
    private List<String> removal;

    public Stage() {
        addition = new TreeMap<>();
        removal = new ArrayList<>();
    }

    public TreeMap<String, String> getAddition() {
        return addition;
    }

    public List<String> getRemoval() {
        return removal;
    }

    public void addAddition(String additionFile, String sha1) {
        if (!additionFile.isEmpty() && !sha1.isEmpty()) {
            if (removal.contains(additionFile)) {
                // If remove first, just not staging it
                removal.remove(additionFile);
            } else {
                if (addition.containsKey(additionFile)) {
                    deleteAddition(additionFile);
                }
                addition.put(additionFile, sha1);
            }
        }
    }

    public void deleteAddition(String deleteFile) {
        if (addition.containsKey(deleteFile)) {
            Utils.restrictedDelete(Utils.join(Repository.STAGE_DIR, deleteFile));
            addition.remove(deleteFile);
        }
    }

    public void addRemoval(String removalFile) {
        addRemoval(removalFile, true); // 默认行为
    }

    public void addRemoval(String removalFile, boolean isTracked) {
        if (!removal.contains(removalFile)) {
            deleteAddition(removalFile);
            if (isTracked) {
                removal.add(removalFile);
            }
        }
    }

    public void deleteRmoval(String deleteFile) {
        removal.remove(deleteFile);
    }

    public void clear() {
        for (String fileName : addition.values()) {
            Utils.restrictedDelete(Utils.join(Repository.STAGE_DIR, fileName));
        }
        addition.clear();
        removal.clear();
    }

    public boolean isEmpty() {
        return addition.isEmpty() && removal.isEmpty();
    }

    @Override
    public void dump() {
        System.out.println("Stage{" +
                "addition=" + addition +
                ", removal=" + removal +
                '}');
    }
}

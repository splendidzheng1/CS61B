package gitlet;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Charles
 */
public class Repository{
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The commit directory inside .gitlet. */
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    /** The branch directory inside .gitlet. */
    public static final File BRANCH_DIR = join(REFS_DIR, "branch");
    /** The head directory inside .gitlet. */
    public static final File HEAD_DIR = join(REFS_DIR, "head");
    /** The head object inside .gitlet. */
    public static final File HEAD_FILE = join(HEAD_DIR, "head");
    /** The blob directory inside .gitlet. */
    public static final File OBJ_DIR = join(GITLET_DIR, "objects");
    /** The stage directory inside .gitlet. */
    public static final File STAGE_DIR = join(GITLET_DIR, "stage");
    /** The stage object inside .gitlet. */
    public static final File STAGE_FILE = join(STAGE_DIR, "stage");

    /**
     * Initial gitlet, create .gitlet directory,
     * make a new commit and a new branch(master).
     */
    public static void initCommand() {
        if (GITLET_DIR.exists()) {
            throw error("A Gitlet version-control system already exists in the current directory.");
        }
        // Create initial directory
        GITLET_DIR.mkdir();
        REFS_DIR.mkdir();
        OBJ_DIR.mkdir();
        BRANCH_DIR.mkdir();
        HEAD_DIR.mkdir();
        STAGE_DIR.mkdir();
        // Make a new commit
        Commit commit = new Commit("initial commit", new Date(0L), null, null, null);
        storeCommit(commit);
        // Make a new master branch
        Branch branch = new Branch("master", commit.getSha1());
        storeBranch(branch);
        // Let head point to it
        Head head = new Head(branch.getName());
        setHead(head);
    }

    /**
     * Add file to staged area.
     * Exit if the file doesn't exist.
     */
    public static void addCommand(String filename) {
        File addFile = getCWDBlob(filename);
        if (!addFile.exists()) {
            throw error("File does not exist.");
        }
        Commit currentCommit = getCommit(getBranch(getHead().getBranchName()).getCommitId());
        Stage stage = getStage();
        if (currentCommit.getBlobs().containsKey(filename)) {
            if (currentCommit.getBlobs().get(filename).equals(blobSha1(addFile))) {
                //  If blob equals to that in commit, stop staging
                deleteStageBlob(currentCommit.getBlobs().get(filename));
                stage.deleteAddition(filename);
                stage.deleteRmoval(filename);
                setStage(stage);
                return;
            }
        }
        // Store file.
        String sha1File = storeStageBlob(addFile);
        // Add relationship to stage area.
        stage.addAddition(filename, sha1File);
        setStage(stage);
    }

    public static void commitCommand(String message) {
        commitCommand(message, "");
    }

    /**
     * Make a commit.
     */
    public static void commitCommand(String message, String parentId2) {
        if (message.isEmpty()) {
            throw Utils.error("Please enter a commit message.");
        }
        Stage stage = getStage();
        if (stage.getAddition().isEmpty()
                && stage.getRemoval().isEmpty()) {
            throw error("No changes added to the commit.");
        }
        Head head = getHead();
        Branch currentBranch = getBranch(head.getBranchName());
        Commit currentCommit = getCommit(currentBranch.getCommitId());
        Commit newCommit = new Commit(message, new Date(), currentCommit.getSha1(), parentId2, currentCommit.getBlobs());

        // Iterate stage and make change to new commit
        for (Map.Entry<String, String> entry : stage.getAddition().entrySet()) {
            storeBlob(getStageBlob(entry.getValue()), entry.getValue());
            newCommit.getBlobs().put(entry.getKey(), entry.getValue());
        }
        for (String entry : stage.getRemoval()) {
            newCommit.getBlobs().remove(entry);
        }

        // Clear stage and store commit
        stage.clear();
        setStage(stage);
        storeCommit(newCommit);
        // Store branch with new commit id
        currentBranch.setCommitId(newCommit.getSha1());
        storeBranch(currentBranch);
    }

    /**
     * Remove file from staged area.
     */
    public static void rmCommand(String filename) {
        Stage stage = getStage();
        Commit currentCommit = getCommit(getBranch(getHead().getBranchName()).getCommitId());
        if (!currentCommit.getBlobs().containsKey(filename) &&
                !stage.getAddition().containsKey(filename)) {
            throw error("No reason to remove the file.");
        }
        boolean isTracked = currentCommit.getBlobs().containsKey(filename);
        if (isTracked) {
            deleteCWDBlob(filename);
        }
        stage.addRemoval(filename, isTracked);
        setStage(stage);
    }

    /**
     * Log commit from the head to initial commit.
     */
    public static void logCommand() {
        Commit commit = getCommit(getBranch(getHead().getBranchName()).getCommitId());
        while (commit != null) {
            logHelper(commit);
            if (commit.getParentID1().isEmpty()) {
                commit = null;
            } else {
                commit = getCommit(commit.getParentID1());
            }
        }
    }

    /**
     * Log all commits' message without order.
     */
    public static void glogCommand() {
        List<String> lCommit = plainFilenamesIn(REFS_DIR);
        if (lCommit != null) {
            lCommit.forEach(c -> logHelper(getCommit(c)));
        }
    }

    /**
     * Find commit with asked message.
     */
    public static void findCommand(String message) {
        List<String> lCommit = plainFilenamesIn(REFS_DIR);
        if (lCommit != null) {
            boolean isExistOne = false;
            for (String c : lCommit) {
                Commit commit = getCommit(c);
                if (commit.getMessage().contains(message)) {
                    message(commit.getSha1());
                    isExistOne = true;
                }
            }
            if (!isExistOne) {
                throw error("Found no commit with that message.");
            }
        }
    }

    /**
     * Check out file from head or commitId or turn into another branch.
     */
    public static void checkoutCommand(String commitId, String fileName, String branchName) {
        Commit currentCommit = null;
        if (fileName != null) {
            if (commitId == null) {
                currentCommit = getCommit(getBranch(getHead().getBranchName()).getCommitId());
            } else {
                // Get commit from commitId
                currentCommit = getCommit(getCommitId(commitId));
            }
            if (currentCommit.getBlobs().containsKey(fileName)) {
                storeCWDBlob(getBlob(currentCommit.getBlobs().get(fileName)), fileName);
            } else {
                throw error("File does not exist in that commitId.");
            }
        } else {
            // Switch to branch
            if (getBranch(getHead().getBranchName()).getName().equals(branchName)) {
                throw error("No need to checkout the current branch.");
            }
            List<Branch> lBranch = getAllBranch();
            if (lBranch != null) {
                for (Branch branch : lBranch) {
                    if (branch.getName().equals(branchName)) {
                        checkoutByCommit(getCommit(branch.getCommitId()));
                        Head head = getHead();
                        head.setBranchName(branch.getName());
                        setHead(head);
                        return;
                    }
                }
            }
            throw error("No such branch exists.");
        }
    }

    /**
     * Print out status.
     */
    public static void statusCommand() {
        // Get branch name
        String headBranchName = getHead().getBranchName();
        List<String> lBranchName = getAllBranch().stream().map(Branch::getName).sorted().collect(Collectors.toList());

        // Print out branch Name
        message("=== Branches ===");
        for (String branchName : lBranchName) {
            if (branchName.equals(headBranchName)) {
                System.out.print("*");
            }
            message(branchName);
        }
        message("");

        // Print out stage
        Stage stage = getStage();
        List<String> lAdd = new ArrayList<>(stage.getAddition().keySet());
        List<String> lRemove = new ArrayList<>(stage.getRemoval());
        Collections.sort(lAdd);
        Collections.sort(lRemove);
        message("=== Staged Files ===");
        lAdd.forEach(Utils::message);
        message("");
        message("=== Removed Files ===");
        lRemove.forEach(Utils::message);
        message("");
        message("=== Modifications Not Staged For Commit ===");
        message("");
        message("=== Untracked Files ===");
        message("");
    }

    /**
     * Create a new branch.
     */
    public static void branchCommand(String branchName) {
        List<String> lBranchName = getAllBranch().stream().map(Branch::getName).collect(Collectors.toList());
        if (lBranchName.contains(branchName)) {
            throw error("A branch with that name already exists.");
        }
        Branch newBranch = new Branch(branchName, getBranch(getHead().getBranchName()).getCommitId());
        storeBranch(newBranch);
    }

    /**
     * Remove a branch by branch name.
     */
    public static void rmBranchCommand(String branchName) {
        Branch currentBranch = getBranch(getHead().getBranchName());
        if (currentBranch.getName().equals(branchName)) {
            throw error("Can't remove the current branch.");
        }
        List<Branch> lBranch = getAllBranch();
        if (lBranch != null) {
            for (Branch branch : lBranch) {
                if (branch.getName().equals(branchName)) {
                    deleteBranch(branchName);
                    return;
                }
            }
        }
        throw error("A branch with that name does not exist.");
    }

    /**
     * Reset head branch to new commit by commit id
     */
    public static void resetCommand(String commitId) {
        // Get commit id and check out to it
        Branch currentBranch = getBranch(getHead().getBranchName());
        commitId = getCommitId(commitId);
        checkoutByCommit(getCommit(commitId));

        // Store branch info
        currentBranch.setCommitId(commitId);
        storeBranch(currentBranch);

        // Clear stage
        Stage stage = getStage();
        stage.clear();
        setStage(stage);
    }

    /**
     * Merge commit from given branch
     */
    public static void mergeCommand(String givenBranchName) {
        Head head = getHead();
        Branch currentBranch = getBranch(head.getBranchName());
        Stage stage = getStage();
        if (!stage.isEmpty()) {
            throw error("You have uncommitted changes.");
        }
        if (currentBranch.getName().equals(givenBranchName)) {
            throw error("Cannot merge a branch with itself.");
        }
        if (getAllBranch().stream().noneMatch(b -> b.getName().equals(givenBranchName))) {
            throw error("A branch with that name does not exist.");
        }

        // Find the latest common ancestor, aka split point
        // 1. Find all parent commit of current branch
        Commit currentCommit = getCommit(currentBranch.getCommitId());
        Commit splitCommit = currentCommit;
        List<String> lParentCommitId = new ArrayList<>();
        Deque<String> queue = new ArrayDeque<>();
        queue.add(currentCommit.getSha1());
        while (!queue.isEmpty()) {
            String parentId = queue.poll();
            if (!parentId.isEmpty()) {
                Commit parentCommit = getCommit(parentId);
                if (!lParentCommitId.contains(parentCommit.getSha1())) {
                    lParentCommitId.add(parentCommit.getSha1());
                }
                queue.add(parentCommit.getParentID1());
                queue.add(parentCommit.getParentID2());
            }
        }
        // 2. Iterate given branch's all parent and find the first matched
        Commit givenCommit = getCommit(getBranch(givenBranchName).getCommitId());
        queue.add(givenCommit.getSha1());
        while (!queue.isEmpty()) {
            String givenParentId = queue.poll();
            if (!givenParentId.isEmpty()) {
                Commit givenParentCommit = getCommit(givenParentId);
                if (lParentCommitId.contains(givenParentId)) {
                    splitCommit = getCommit(givenParentId);
                    break;
                }
                queue.add(givenParentCommit.getParentID1());
                queue.add(givenParentCommit.getParentID2());
            }
        }

        if (splitCommit.getSha1().equals(currentCommit.getSha1())) {
            currentBranch.setCommitId(givenCommit.getSha1());
            checkoutByCommit(getCommit(currentBranch.getCommitId()));
            storeBranch(currentBranch);
            message("Current branch fast-forwarded.");
            return;
        }
        if (splitCommit.getSha1().equals(givenCommit.getSha1())) {
            message("Given branch is an ancestor of the current branch.");
            return;
        }

        // Try to merge commit
        boolean isConfict = false;
        TreeMap<String, String> currentBlobs = currentCommit.getBlobs();
        TreeMap<String, String> givenBlobs = givenCommit.getBlobs();
        TreeMap<String, String> splitBlobs = splitCommit.getBlobs();
        for (String splitTrack : splitBlobs.keySet()) {
            if (currentBlobs.containsKey(splitTrack) && givenBlobs.containsKey(splitTrack)) {
                // 1. All tracked and only modified in given branch
                if (splitBlobs.get(splitTrack).equals(currentBlobs.get(splitTrack)) &&
                        !splitBlobs.get(splitTrack).equals(givenBlobs.get(splitTrack))) {
                    File blob = getBlob(givenBlobs.get(splitTrack));
                    storeCWDBlob(blob, splitTrack);
                    storeStageBlob(blob, givenBlobs.get(splitTrack));
                    stage.addAddition(splitTrack, givenBlobs.get(splitTrack));
                }
                // 2. All tracked and only modified in current branch, not change
                // 3. All tracked and modified in both but the same, not change
                // 8. Modified by different way
                if (!splitBlobs.get(splitTrack).equals(currentBlobs.get(splitTrack)) &&
                        !splitBlobs.get(splitTrack).equals(givenBlobs.get(splitTrack)) &&
                                !currentBlobs.get(splitTrack).equals(givenBlobs.get(splitTrack))) {
                    isConfict = true;
                    String conflictStr = String.format("<<<<<<< HEAD\n%s=======\n%s>>>>>>>\n", readContentsAsString(getBlob(currentBlobs.get(splitTrack))), readContentsAsString(getBlob(givenBlobs.get(splitTrack))));
                    byte[] blob = conflictStr.getBytes(Charset.defaultCharset());
                    storeCWDBlob(blob, splitTrack);
                    String sha1 = storeStageBlob(blob);
                    stage.addAddition(splitTrack, sha1);
                }
            }
            if (currentBlobs.containsKey(splitTrack) && !givenBlobs.containsKey(splitTrack)) {
                if (currentBlobs.get(splitTrack).equals(splitBlobs.get(splitTrack))) {
                    // 6. Tracked by spit commit, current commit and remove by given commit
                    deleteCWDBlob(splitTrack);
                    stage.addRemoval(splitTrack);
                } else {
                    // 8. Modified by different way
                    isConfict = true;
                    String conflictStr = String.format("<<<<<<< HEAD\n%s=======\n>>>>>>>\n", readContentsAsString(getBlob(currentBlobs.get(splitTrack))));
                    byte[] blob = conflictStr.getBytes(Charset.defaultCharset());
                    storeCWDBlob(blob, splitTrack);
                    String sha1 = storeStageBlob(blob);
                    stage.addAddition(splitTrack, sha1);
                }
            }
            // 7. Tracked by spit commit, given commit and remove by current commit, not change
            // 8. Modified by different way
            if (!currentBlobs.containsKey(splitTrack) && givenBlobs.containsKey(splitTrack)) {
                if (!givenBlobs.get(splitTrack).equals(splitBlobs.get(splitTrack))) {
                    isConfict = true;
                    String conflictStr = String.format("<<<<<<< HEAD\n=======\n%s>>>>>>>\n", readContentsAsString(getBlob(givenBlobs.get(splitTrack))));
                    byte[] blob = conflictStr.getBytes(Charset.defaultCharset());
                    storeCWDBlob(blob, splitTrack);
                    String sha1 = storeStageBlob(blob);
                    stage.addAddition(splitTrack, sha1);
                }
            }
        }
        // 4. Any file only tracked in current branch, not change
        for (String givenTrack : givenBlobs.keySet()) {
            // 5. Any file only tracked in given branch, stage it
            if (!splitBlobs.containsKey(givenTrack)) {
                if (!currentBlobs.containsKey(givenTrack)) {
                    File cwdBlob = getCWDBlob(givenTrack);
                    if (cwdBlob.exists() && !givenBlobs.get(givenTrack).equals(blobSha1(cwdBlob))) {
                        throw error("There is an untracked file in the way; delete it, or add and commit it first.");
                    }
                    File blob = getBlob(givenBlobs.get(givenTrack));
                    storeCWDBlob(blob, givenTrack);
                    storeStageBlob(blob, givenBlobs.get(givenTrack));
                    stage.addAddition(givenTrack, givenBlobs.get(givenTrack));
                }
            }
        }

        // Make a commit
        setStage(stage);
        commitCommand(String.format("Merged %s into %s.", givenBranchName, currentBranch.getName()), givenCommit.getSha1());
        if (isConfict) {
            message("Encountered a merge conflict.");
        }
    }

    private static Stage getStage() {
        Stage stage;
        if (STAGE_FILE.exists()) {
            stage = readObject(STAGE_FILE, Stage.class);
        } else {
            stage = new Stage();
        }
        return stage;
    }

    private static void setStage(Stage stage) {
        try {
            writeObject(STAGE_FILE, stage);
        } catch (Exception e) {
            throw error("Set stage wrong");
        }
    }

    private static Head getHead() {
        Head head;
        if (HEAD_FILE.exists()) {
            head = readObject(HEAD_FILE, Head.class);
        } else {
            throw error("Without head object");
        }
        return head;
    }

    private static void setHead(Head head) {
        try {
            writeObject(HEAD_FILE, head);
        } catch (Exception e) {
            throw error("Set head wrong");
        }
    }

    private static Branch getBranch(String branchId) {
        Branch branch;
        if (join(BRANCH_DIR, branchId).exists()) {
            branch = readObject(join(BRANCH_DIR, branchId), Branch.class);
        } else {
            throw error("Without branch object");
        }
        return branch;
    }

    private static List<Branch> getAllBranch() {
        return Objects.requireNonNull(plainFilenamesIn(BRANCH_DIR)).stream().map(Repository::getBranch).collect(Collectors.toList());
    }

    private static void storeBranch(Branch branch) {
        try {
            writeObject(join(BRANCH_DIR, branch.getName()), branch);
        } catch (Exception e) {
            throw error("Store branch wrong");
        }
    }

    private static void deleteBranch(String branchId) {
        restrictedDelete(join(BRANCH_DIR, branchId));
    }

    private static Commit getCommit(String commitId) {
        Commit commit;
        if (join(REFS_DIR, commitId).exists()) {
            commit = readObject(join(REFS_DIR, commitId), Commit.class);
        } else {
            throw error("Without commit object");
        }
        return commit;
    }

    private static String getCommitId(String commitId) {
        String finalCommit = commitId;
        // Find commitId
        String finalCommit1 = finalCommit;
        List<String> lPath = plainFilenamesIn(REFS_DIR).stream().filter(s -> s.startsWith(finalCommit1)).collect(Collectors.toList());
        if (lPath.size() == 1) {
            finalCommit = lPath.get(0);
        } else {
            throw error("No commitId with that id exists.");
        }
        return finalCommit;
    }

    private static void storeCommit(Commit commit) {
        try {
            writeObject(join(REFS_DIR, commit.getSha1()), commit);
        } catch (Exception e) {
            throw error("Store commit wrong");
        }
    }

    private static File getBlob(String blobId) {
        File blob;
        if (join(OBJ_DIR, blobId).exists()) {
            blob = join(OBJ_DIR, blobId);
        } else {
            throw error("Without blob object");
        }
        return blob;
    }

    private static String storeBlob(File blob, String sha1) {
        try {
            byte[] content = readContents(blob);
            writeContents(join(OBJ_DIR, sha1), (Object) content);
        } catch (Exception e) {
            throw error("Store blob wrong");
        }
        return sha1;
    }

    private static File getStageBlob(String blobId) {
        File blob;
        if (join(STAGE_DIR, blobId).exists()) {
            blob = join(STAGE_DIR, blobId);
        } else {
            throw error("Without stage's blob object");
        }
        return blob;
    }

    private static String storeStageBlob(File blob) {
        String sha1 = "";
        try {
            byte[] content = readContents(blob);
            sha1 = blobSha1(blob);
            writeContents(join(STAGE_DIR, sha1), (Object) content);
        } catch (Exception e) {
            throw error("Store stage's blob wrong");
        }
        return sha1;
    }

    private static void storeStageBlob(File blob, String fileName) {
        try {
            byte[] content = readContents(blob);
            writeContents(join(STAGE_DIR, fileName), (Object) content);
        } catch (Exception e) {
            throw error("Store stage's blob wrong");
        }
    }

    private static String storeStageBlob(byte[] blob) {
        String sha1 = "";
        try {
            sha1 = sha1((Object) blob);
            writeContents(join(STAGE_DIR, sha1), (Object) blob);
        } catch (Exception e) {
            throw error("Store cwd's blob wrong");
        }
        return sha1;
    }

    private static void deleteStageBlob(String blobId) {
        restrictedDelete(join(STAGE_DIR, blobId));
    }

    private static File getCWDBlob(String fileName) {
        return join(CWD, fileName);
    }

    private static String storeCWDBlob(File srcPath, String fileName) {
        try {
            byte[] content = readContents(srcPath);
            writeContents(join(CWD, fileName), (Object) content);
        } catch (Exception e) {
            throw error("Store cwd's blob wrong");
        }
        return fileName;
    }

    private static String storeCWDBlob(byte[] src, String fileName) {
        try {
            writeContents(join(CWD, fileName), (Object) src);
        } catch (Exception e) {
            throw error("Store cwd's blob wrong");
        }
        return fileName;
    }

    private static void deleteCWDBlob(String fileName) {
        restrictedDelete(join(CWD, fileName));
    }

    private static void logHelper(Commit commit) {
        message("===");
        message("commit %s", commit.getSha1());
        if (!commit.getParentID2().isEmpty()) {
            message("Merge: %s %s", commit.getParentID1().substring(0, 7), commit.getParentID2().substring(0, 7));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.US);
        message("Date: %s", sdf.format(commit.getDate()));
        message(commit.getMessage());
        message("");
    }

    private static void checkoutByCommit(Commit commit) {
        List<String> lCwdFileName = plainFilenamesIn(CWD);
        Commit currentCommit = getCommit(getBranch(getHead().getBranchName()).getCommitId());
        for (String fileName : commit.getBlobs().keySet()) {
            if (!currentCommit.getBlobs().containsKey(fileName) && lCwdFileName.contains(fileName)) {
                throw error("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }
        for (String fileName : currentCommit.getBlobs().keySet()) {
            deleteCWDBlob(fileName);
        }
        commit.getBlobs().forEach((fileName, blobId) -> {
            storeCWDBlob(getBlob(blobId), fileName);
        });
    }
}

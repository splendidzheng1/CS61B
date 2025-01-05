package gitlet;

import java.io.Serializable;

public class Branch implements Serializable, Dumpable {
    private String name;
    private String commitId;

    public Branch(String name, String commitId) {
        this.name = name;
        this.commitId = commitId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    @Override
    public void dump() {
        System.out.println("Branch{" +
                "name='" + name + '\'' +
                ", commitId=" + commitId +
                '}');
    }
}

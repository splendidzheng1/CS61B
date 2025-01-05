package gitlet;

import java.io.Serializable;

public class Head implements Serializable, Dumpable {
    private String branchName;

    public Head(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    @Override
    public void dump() {
        System.out.println("branchName{" +
                branchName +
                '}');
    }
}

package gitlet;

import static gitlet.Repository.GITLET_DIR;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Charles
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                throw Utils.error("Please enter a command");
            }
            String commandArg = args[0];
            if (!GITLET_DIR.exists() && !commandArg.equals("init")) {
                throw Utils.error("Not in an initialized Gitlet directory.");
            }
            switch(commandArg) {
                case "init":
                    if (validateNumArgs(args, 1)) {
                        Repository.initCommand();
                    }
                    break;
                case "add":
                    if (validateNumArgs(args, 2)) {
                        Repository.addCommand(args[1]);
                    }
                    break;
                case "commit":
                    if (validateNumArgs(args, 2)) {
                        Repository.commitCommand(args[1]);
                    }
                    break;
                case "rm":
                    if (validateNumArgs(args, 2)) {
                        Repository.rmCommand(args[1]);
                    }
                    break;
                case "log":
                    if (validateNumArgs(args, 1)) {
                        Repository.logCommand();
                    }
                    break;
                case "global-log":
                    if (validateNumArgs(args, 1)) {
                        Repository.glogCommand();
                    }
                    break;
                case "find":
                    if (validateNumArgs(args, 2)) {
                        Repository.findCommand(args[1]);
                    }
                    break;
                case "status":
                    if (validateNumArgs(args, 1)) {
                        Repository.statusCommand();
                    }
                    break;
                case "checkout":
                    String fileName = null;
                    String commit = null;
                    String branch = null;
                    if (args.length == 2) {
                        branch = args[1];
                    } else if (args.length == 3){
                        if (args[1].equals("--")) {
                            fileName = args[2];
                        } else {
                            Utils.message("Incorrect operands.");
                            break;
                        }
                    } else if (args.length == 4) {
                        commit = args[1];
                        if (args[2].equals("--")) {
                            fileName = args[3];
                        } else {
                            Utils.message("Incorrect operands.");
                            break;
                        }
                    }
                    if (fileName == null && commit == null && branch == null) {
                        Utils.message("Incorrect operands.");
                    } else {
                        Repository.checkoutCommand(commit, fileName, branch);
                    }
                    break;
                case "branch":
                    if (validateNumArgs(args, 2)) {
                        Repository.branchCommand(args[1]);
                    }
                    break;
                case "rm-branch":
                    if (validateNumArgs(args, 2)) {
                        Repository.rmBranchCommand(args[1]);
                    }
                    break;
                case "reset":
                    if (validateNumArgs(args, 2)) {
                        Repository.resetCommand(args[1]);
                    }
                    break;
                case "merge":
                    if (validateNumArgs(args, 2)) {
                        Repository.mergeCommand(args[1]);
                    }
                    break;
                default:
                    Utils.message("No command with that name exists.");
            }
        } catch (Exception ex)
        {
            Utils.message(ex.getMessage());
            System.exit(0);
        }
    }

    public static boolean validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            Utils.message("Incorrect operands.");
        }
        return args.length == n;
    }
}

package testing;

import gitlet.Main;
import gitlet.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Test {

    @org.junit.Test
    public void testInit() {
        Repository.initCommand();
        Repository.addCommand("wug.txt");
        Repository.commitCommand("Another commit message.");
        Repository.rmCommand("wug.txt");
        Repository.addCommand("wug.txt");
        Repository.checkoutCommand("6b310e2aa5c03677aae0af371c8a46755ff1a34a", "wug.txt", null);
        Repository.logCommand();
    }

    @org.junit.Test
    public void testMain() {
        String[] strs = new String[1];
        strs[0] = "init";
        Main.main(strs);
        strs = new String[3];
        strs[0] = "checkout";
        strs[1] = "--";
        strs[2] = "wug.txt";
        Main.main(strs);
        strs = new String[4];
        strs[0] = "checkout";
        strs[1] = "6b310e2aa5c03677aae0af371c8a46755ff1a34a";
        strs[2] = "--";
        strs[3] = "wug.txt";
        Main.main(strs);
    }

    @org.junit.Test
    public void testUtil() {
        try {
            byte[] bytes = Files.readAllBytes(Path.of("aa"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

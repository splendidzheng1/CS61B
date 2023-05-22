package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
      AListNoResizing<Integer> correct = new AListNoResizing<>();
      BuggyAList<Integer> broken = new BuggyAList<>();

      correct.addLast(5);
      correct.addLast(10);
      correct.addLast(15);

      broken.addLast(5);
      broken.addLast(10);
      broken.addLast(15);

      assertEquals(correct.size(), broken.size());

      assertEquals(correct.removeLast(), broken.removeLast());
      assertEquals(correct.removeLast(), broken.removeLast());
      assertEquals(correct.removeLast(), broken.removeLast());
    }

    @Test
    public void randomizedTest() {
      AListNoResizing<Integer> correct = new AListNoResizing<>();
      BuggyAList<Integer> broken = new BuggyAList<>();

      int N = 5000;
      for (int i = 0; i < N; i += 1) {
        int operationNumber = StdRandom.uniform(0, 4);
        if (operationNumber == 0) {
          // addLast
          int randVal = StdRandom.uniform(0, 100);
          correct.addLast(randVal);
          broken.addLast(randVal);
          System.out.println("addLast(" + randVal + ")");
        } else if (operationNumber == 1) {
          // size
          int c_size = correct.size();
          int b_size = broken.size();
          assertEquals(c_size, b_size);
          System.out.println("size: " + c_size);
        } else if (operationNumber == 2) {
          // getLast
          assertEquals(correct.size(), broken.size());
          if(correct.size() > 0 && broken.size() > 0){
            Integer c_last = correct.getLast();
            Integer b_last = broken.getLast();
            assertEquals(c_last, b_last);
            System.out.println("getLast(: " + c_last + ")");
          }
        } else if (operationNumber == 3) {
          // removeLast
          assertEquals(correct.size(), broken.size());
          if(correct.size() > 0 && broken.size() > 0){
            Integer c_last = correct.removeLast();
            Integer b_last = broken.removeLast();
            assertEquals(c_last, b_last);
            System.out.println("getLast(: " + c_last + ")");
          }
        }
      }
    }
}

package tester;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

import static org.junit.Assert.assertEquals;
public class TestArrayDequeEC {
    @Test
    public void randomTest() {
        ArrayDequeSolution<Integer> correct = new ArrayDequeSolution<>();
        StudentArrayDeque<Integer> broken = new StudentArrayDeque<>();
        StringBuilder sb = new StringBuilder();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                correct.addFirst(randVal);
                broken.addFirst(randVal);
                sb.append("addFirst(" + randVal + ")\n");
                // System.out.println("addFirst(" + randVal + ")");
            } else if (operationNumber == 1) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                correct.addLast(randVal);
                broken.addLast(randVal);
                sb.append("addFirst(" + randVal + ")\n");
                // System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 2) {
                // removeLast
                if (correct.size() > 0 && broken.size() > 0) {
                    Integer c_last = correct.removeLast();
                    Integer b_last = broken.removeLast();
                    assertEquals(sb.toString(), c_last, b_last);
                    sb.append("removeLast()\n");
                    // System.out.println("getLast(: " + c_last + ")");
                }
            } else if (operationNumber == 3) {
                // removeFirst
                if (correct.size() > 0 && broken.size() > 0) {
                    Integer c_first = correct.removeFirst();
                    Integer b_first = broken.removeFirst();
                    assertEquals(sb.toString(), c_first, b_first);
                    sb.append("removeFirst()\n");
                    // System.out.println("getLast(: " + c_first + ")");
                }
            }
        }
    }
}

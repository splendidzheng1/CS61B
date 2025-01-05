package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;
        import static org.junit.Assert.assertEquals;

public class MaxArraryDequeTest {


    @Test
    public void testConstructor(){
        MaxArrayDeque<Integer> mA = new MaxArrayDeque<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });

        assertTrue("A newly initialized LLDeque should be empty", mA.isEmpty());
        mA.addFirst(1);
        mA.addFirst(2);
        mA.addFirst(3);
        mA.addFirst(4);

        assertEquals(4, (int)mA.max());
        assertEquals(4, (int)mA.max(new MaxComparator()));
    }
}

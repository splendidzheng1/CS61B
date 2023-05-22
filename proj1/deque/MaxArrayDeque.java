package deque;


import java.util.Comparator;

public class MaxArrayDeque<Item> extends ArrayDeque<Item>{
    public MaxArrayDeque(Comparator<Item> c){
        cP = c;
    }

    public static class MaxComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2){
            return o1.compareTo(o2);
        }
    }
    public Item max(){
        return max(null);
    }

    public Item max(Comparator<Item> c){
        int maxPos = 0;
        if(c == null)   c = cP;
        for(int i = 0; i < size(); i++){
            int compare = c.compare(get(maxPos), get(i));
            if(compare < 0){
                maxPos = i;
            }
        }
        return get(maxPos);
    }

    private Comparator<Item> cP;
}

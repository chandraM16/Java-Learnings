
import java.util.*;

public class PartB {
    public static void main(String[] args) {
        List<Integer> ints = new ArrayList<>(List.of(1, 2, 3));
        List<Number> nums = new ArrayList<>();

        List<Number> b1 = ints; // B1 
        List<? extends Number> b2 = ints; // B2
        b2.add(4); // B3
        List<? super Integer> b4 = nums;
        b4.add(4); // B4
        Object b5 = (ints instanceof List<Integer> ? 1 : 0);// B5
        class Holder<T> {
            T[] make() {
                return new T[4];
            }
        } // B6
    }
}

// B1 > reject, List<Integer> List<Number> pe convert nahi ho sakta 
// B2 > ok
// B3 > reject, only read kar sakte hai 
// B4 > ok
// B5 > ok
// B6 > pta nahi  
 
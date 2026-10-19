import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Kyun list.sort() ki signature Comparator<? super E> hai, Comparator<E> nahi. */
public class SortSuperDemo {

    public static void main(String[] args) {

        // EK comparator jo kisi bhi Number ko compare kar sakta hai
        Comparator<Number> byValue = Comparator.comparingDouble(Number::doubleValue);

        // ...aur wahi ek comparator TEEN alag list types pe chal jaata hai
        List<Integer> ints = new ArrayList<>(List.of(5, 1, 3));
        List<Double> doubles = new ArrayList<>(List.of(2.5, 0.5, 9.9));
        List<Long> longs = new ArrayList<>(List.of(30L, 10L, 20L));

        ints.sort(byValue);
        doubles.sort(byValue);
        longs.sort(byValue);

        System.out.println("List<Integer> + Comparator<Number> = " + ints);
        System.out.println("List<Double>  + Comparator<Number> = " + doubles);
        System.out.println("List<Long>    + Comparator<Number> = " + longs);

        // Comparator<Object> bhi chalta hai — Integer bhi ek Object hai
        Comparator<Object> byText = Comparator.comparing(Object::toString);
        List<Integer> again = new ArrayList<>(List.of(5, 1, 30));
        again.sort(byText);
        System.out.println("List<Integer> + Comparator<Object> = " + again + "  (text se sort)");
    }
}

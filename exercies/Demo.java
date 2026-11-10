
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Demo {
    public static void main(String[] args) {

        HashMap<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        map.put("d", 4);

        for (Map.Entry<String, Integer> e : map.entrySet()) {
            System.out.println(e.getKey());
            System.out.println(e.getValue());
        }
        

        Map<String, Integer> tMap = new TreeMap<>();

        for (String s : List.of("X", "X", "Z", "Y")) {
            tMap.merge(s, 1, Integer::sum);
        }
        System.out.println(tMap);

        Map<String, List<String>> byCat = new TreeMap<>();

        byCat.computeIfAbsent("food", k -> new ArrayList<>()).add("ko");
        byCat.computeIfAbsent(("car"), k -> new ArrayList<>()).add("BMW");

        System.out.println(byCat);


        Map<String, Integer> withNull = new HashMap<>();
        withNull.put(null, 1);
        System.out.println(withNull);

    }
}

class Customer {
    private final String name;
    private final int age;

    public Customer(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Customer(String name) {
        this(name, 0);
    }

}

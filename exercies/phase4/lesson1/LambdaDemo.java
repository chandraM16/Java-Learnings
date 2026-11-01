import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LambdaDemo {

    public static void main(String[] args) {
        // 1. Same thing, two ways: anonymous class vs lambda
        Comparator<String> oldWay = new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return Integer.compare(a.length(), b.length());
            }
        };
        Comparator<String> newWay = (a, b) -> Integer.compare(a.length(), b.length());

        System.out.println(oldWay.compare("hi", "hello")); // negative
        System.out.println(newWay.compare("hi", "hello")); // same

        // 2. The four workhorses
        Function<String, Integer> len = s -> s.length(); // T -> R .apply
        Predicate<String> isEmpty = s -> s.isEmpty(); // T -> boolean .test
        Supplier<String> greeting = () -> "hello"; // () -> T .get
        Consumer<String> printer = s -> System.out.println(s); // T -> void .accept

        System.out.println(len.apply("chandan")); // 7
        System.out.println(isEmpty.test("")); // true
        System.out.println(greeting.get()); // hello
        printer.accept("printed via Consumer");

        // 3. Lambdas passed to library methods
        List<String> names = new ArrayList<>(List.of("zoya", "al", "meera"));
        names.removeIf(n -> n.length() < 3); // Predicate
        names.forEach(n -> System.out.println(n)); // Consumer
        names.sort((a, b) -> a.compareTo(b)); // Comparator

        System.out.println(names);

        // 4. Capturing a local variable
        int minLength = 4;
        Predicate<String> longEnough = s -> s.length() >= minLength; // reads minLength: OK
        System.out.println(longEnough.test("meera"));
        // minLength = 5; // <- uncomment this and see what javac says

        Function<Integer, Integer> twice = x -> x * 2;
        Predicate<String> startsWithA = s -> s.startsWith("A");
        Supplier<Integer> five = () -> 5;
        Consumer<String> shout = s -> System.out.println(s.toUpperCase());

        System.out.println(twice.apply(21)); // (1)
        System.out.println(startsWithA.test("Asha")); // (2)
        System.out.println(five.get() + twice.apply(5)); // (3)
        shout.accept("ok"); // (4)
        System.out.println(startsWithA.test("Asha")); // (5)
        Function<Integer, Integer> bad = (x) -> x + 2; // (6)

        int base = 10;
        Function<Integer, Integer> addBase = n -> n + base;
        System.out.println(addBase.apply(1)); // (8) (agar (7) compile hota to)
    }
}

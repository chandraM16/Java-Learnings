import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/** Concept 2 — Java ko order kaise batate hain. Chalao aur har step ka output padho. */
public class CompareDemo {

    /** Plain class — Java ko iska koi order nahi pata. */
    static final class Plain {
        final String name;
        Plain(String name) { this.name = name; }
        @Override public String toString() { return name; }
    }

    /** WAY 1 — class KHUD batati hai apna order. */
    static final class Student implements Comparable<Student> {
        final String name;
        final int marks;

        Student(String name, int marks) { this.name = name; this.marks = marks; }

        @Override
        public int compareTo(Student other) {
            return Integer.compare(this.marks, other.marks);   // marks ascending
        }

        @Override public String toString() { return name + "(" + marks + ")"; }
    }

    public static void main(String[] args) {
        Student alice = new Student("alice", 50);
        Student bob = new Student("bob", 90);
        Student carol = new Student("carol", 70);

        System.out.println("=== STEP 1: Java ko Plain ka order nahi pata ===");
        try {
            Set<Plain> set = new TreeSet<>();
            set.add(new Plain("x"));
            set.add(new Plain("y"));          // doosre element pe compare karna pada
        } catch (ClassCastException e) {
            System.out.println("CRASH -> " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== STEP 2: compareTo ka sign (number matter nahi karta) ===");
        System.out.println("alice.compareTo(bob)   = " + alice.compareTo(bob) + "   negative -> alice PEHLE");
        System.out.println("bob.compareTo(alice)   = " + bob.compareTo(alice) + "   positive -> bob BAAD mein");
        System.out.println("alice.compareTo(alice) = " + alice.compareTo(alice) + "   zero -> barabar");

        System.out.println();
        System.out.println("=== STEP 3: Comparable ek baar likha, har sorted jagah chal gaya ===");
        List<Student> list = new ArrayList<>(List.of(bob, alice, carol));
        Collections.sort(list);
        System.out.println("Collections.sort(list) = " + list);
        System.out.println("new TreeSet<>(list)    = " + new TreeSet<>(list));

        System.out.println();
        System.out.println("=== STEP 4: WAY 2 - Comparator, order bahar se ===");
        Comparator<Student> byName = Comparator.comparing(s -> s.name);
        Comparator<Student> byMarksDesc = Comparator.comparingInt((Student s) -> s.marks).reversed();

        list.sort(byName);
        System.out.println("list.sort(byName)      = " + list);
        list.sort(byMarksDesc);
        System.out.println("list.sort(byMarksDesc) = " + list);

        Set<Student> nameSet = new TreeSet<>(byName);   // TreeSet ko comparator diya
        nameSet.addAll(list);
        System.out.println("new TreeSet<>(byName)  = " + nameSet + "   <- compareTo IGNORE ho gaya");

        System.out.println();
        System.out.println("=== STEP 5: tie-breaker kyun chahiye ===");
        Student dave = new Student("dave", 90);          // bob ke barabar marks
        List<Student> tie = new ArrayList<>(List.of(bob, dave, alice));
        tie.sort(byMarksDesc);
        System.out.println("bina breaker = " + tie + "   <- bob/dave ka order guarantee nahi");
        tie.sort(byMarksDesc.thenComparing(s -> s.name));
        System.out.println("with breaker = " + tie + "   <- hamesha yahi");

        System.out.println();
        System.out.println("=== STEP 6: subtraction se compare = overflow bug ===");
        int big = Integer.MAX_VALUE;
        int small = -10;
        System.out.println("big - small          = " + (big - small) + "  <- NEGATIVE?! galat");
        System.out.println("Integer.compare(...) = " + Integer.compare(big, small) + "  <- sahi");
    }
}

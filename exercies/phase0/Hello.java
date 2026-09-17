public class Hello { // all code lives inside a class
    public static void main(String[] args) { // the JVM's entry point
        String name = args.length > 0 ? args[0] : "JVM";
        System.out.println("Hello, " + name);
    }

}

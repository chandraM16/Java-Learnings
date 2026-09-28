package demo;

class Counter {
    protected int total;

    public void add(int n) {
        total += n;
    }

    public void addAll(int[] numbers) {
        for (int n : numbers) {
            add(n);
        }
    }
}

class LoggingCounter extends Counter {
    private int addCalls = 0;
    private final Counter counter = new Counter();

    @Override
    public void add(int n) {
        addCalls++;
        counter.add(n);
    }

    @Override
    public void addAll(int[] numbers) {
        System.out.println("n ->" + numbers.length);
        addCalls += numbers.length;
        counter.addAll(numbers);
    }

    public int getAddCalls() {
        return addCalls;
    }
}

public class CounterDemo {
    public static void main(String[] args) {
        LoggingCounter c = new LoggingCounter();
        c.add(5);
        c.addAll(new int[] { 1, 2, 3 });
        System.out.println("total: " + c.total);
        System.out.println("addCalls: " + c.getAddCalls());
    }
}
class Shape {
    public double area() {
        return 0.0;
    }

    public String describe() {
        return "area=" + area();
    }
}

class Circle extends Shape {
    private final double radius;

    Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}

public class ShapeDemo {
    public static void main(String[] args) {
        Shape s = new Circle(2.0);
        System.out.println("1: " + s.area());
        System.out.println("2: " + s.describe());
    }
}

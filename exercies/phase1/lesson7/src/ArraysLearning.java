import java.util.Arrays;

public class ArraysLearning {
    public static void main(String[] args) {
        int[] intArr = new int[5];
        String[] strArr = {"Test", "Array"};
        int[] intArr2 = new int[]{1,2,3,4};

        int[] original = {3,1,2};

        int[] sorted = original;

        Arrays.sort(original);

        int[] sorted2 = Arrays.copyOf(original, original.length);

        int[] part = Arrays.copyOfRange(original, 1, 2);

    }
}

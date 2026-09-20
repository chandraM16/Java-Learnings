public class StringLearning {
    public static void main(String[] args) {

        try {
            int a = 90;
            int b = a;
            System.out.println(a);

            b = 30;
            System.out.println(b);
            System.out.println(a);

            StringBuilder x = new StringBuilder("hi");
            StringBuilder y = x;
            y.append("!");
            System.out.println(x);

            String role = "admin";
            role.toUpperCase();
            System.out.println(role.toUpperCase());
            System.out.println(role);

            String email = "  Chandan@gamil.com  ";
            

            email = email.replace("gamil", "yahoo").strip();
            System.out.println(email);

            String[] arr = "a,b,c".split(",");

            for (String i : arr) {
                System.out.println(i);
            }

            // int to String
            int num = Integer.parseInt(("39090"));
            System.out.println(num);

            // String to Int
            String str = String.valueOf(123);
            System.out.println(str);

            // String Builder

            StringBuilder csv = new StringBuilder();
            csv.append("id, email\n");

            System.out.println(csv.toString());

            // text block
            String sql = """
                    SELECT id, email
                    FROM users
                    WHERE status = 'ACTIVE'
                    """;
            System.out.println(sql);


            

        } catch (Exception e) {
            // TODO: handle exception

            System.out.println(e);
        }
    }
}

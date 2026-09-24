
import java.util.Objects;

 
public class Money {
    private final long amountInPaise;
    private final String currency;

    public Money(long amountInPaise, String currency) {
        this.amountInPaise = amountInPaise;
        this.currency = currency;
    }

    @Override 
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amountInPaise == money.amountInPaise && Objects.equals(currency, money.currency);
    }

    @Override 
    public int hashCode(){
        return Objects.hash(amountInPaise, currency);
    }

    @Override 
    public String toString(){
        return "Amount is " + amountInPaise + " and currency is " + currency;
    }

    public static void main(String[] args) {
        Money m1 = new Money(100, "paise");
        Money m2 = new Money(100, "paise");

        System.out.println(m1.hashCode());
        System.out.println(m2.hashCode());
        System.out.println(m2.equals(m1));
        
    }
}

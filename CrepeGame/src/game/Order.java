package game;
import java.util.Arrays;
import strategy.*;

public class Order {
    private String[] toppings;
    private ScoreStrategy strategy;

    public Order(String[] toppings,ScoreStrategy strategy) {   // creates order for createOrder method in OrderFactory
        this.toppings = toppings;
        this.strategy = strategy;
    }

    public String[] getToppings() {
        return toppings;
    }

    public ScoreStrategy getStrategy() {
        return strategy;
    }

    @Override
    public String toString() {
        return Arrays.toString(toppings);
    }
}
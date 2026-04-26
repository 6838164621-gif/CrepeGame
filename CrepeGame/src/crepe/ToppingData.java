package crepe;

import java.util.*;

public class ToppingData {

    private String name;
    private double cost;

    public ToppingData(String name, double cost) { // constructor
        this.name = name;
        this.cost = cost;
    }

    public String getName() { return name; }
    public double getCost() { return cost; }

    // instead of making every individual topping a class we do this to not add many classes
    private static final Map<String, Double> toppingPrices = new HashMap<>();

    static {        // hashmap (dictionary) of all the toppings
        toppingPrices.put("Chocolate", 5.0);
        toppingPrices.put("Banana", 5.0);
        toppingPrices.put("Whipped Cream", 3.0);
        toppingPrices.put("Strawberries", 5.0);
        toppingPrices.put("Nutella", 7.0);
        toppingPrices.put("MarshMallows",5.0);
        toppingPrices.put("Oreos",5.0);
        toppingPrices.put("Sprinkles",5.0);
        toppingPrices.put("Condensed Milk",5.0);
        toppingPrices.put("Blueberry Jam",5.0);
        toppingPrices.put("Peanut Butter",5.0);
        toppingPrices.put("Brownies",5.0);
        toppingPrices.put("Foi Thong",5.0);
        toppingPrices.put("Raspberries", 5.0);
        toppingPrices.put("Jelly",3.0);
        toppingPrices.put("Caramel", 5.0);
    }

    public static double getPrice(String name) {
        return toppingPrices.getOrDefault(name, 1.0);
    }

    public static List<String> getToppings() {
        return new ArrayList<>(toppingPrices.keySet());
    }
}
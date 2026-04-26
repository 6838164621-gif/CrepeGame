package game;

import crepe.ToppingData;
import strategy.BasicProfitStrategy;
import strategy.TimeBonusStrategy;

import java.util.*;

public class OrderFactory {

    private static Random rand = new Random(); // randomizer object

    public static Order createOrder(int round) {        // triggers every round (factory output)
        if (round <= 3) {       // if round is <= 3, factory outputs easy mode
            return createEasyOrder();
        } else if (round <= 7) {       
            return createMediumOrder();        // difficulty gets harder and harder
        } else {
            return createHardOrder();
        }
    }
    
    private static String[] randomToppings(int min, int max) {
        List<String> all = ToppingData.getToppings();       // get list of all toppings
        Collections.shuffle(all);       // rearranges the list (to make it random)

        int count = rand.nextInt(max - min + 1) + min;  // chooses how many toppings (eg. hard round, random between 0 to 3 <- (5-3+1) + 3 (min topping count) and round down (turns it into int) so its btw 3 and 5.
        return all.subList(0, count).toArray(new String[0]);   // creates a sublist of toppingdata with a length of the thing we just randomized and adds it to an empty string array
    }

    private static Order createEasyOrder() {
        return new Order(randomToppings(1, 2), new BasicProfitStrategy());
    }

    private static Order createMediumOrder() {
        return new Order(randomToppings(2, 3), new BasicProfitStrategy());
    }

    private static Order createHardOrder() {
        return new Order(randomToppings(3, 5), new TimeBonusStrategy());
    }
}
package game;

import observer.GameObserver;
import observer.GameSubject;
import java.util.*;


public class GameManager implements GameSubject {      // where most of the game stuff happens (besides UI and some delays cos of the UI)
    private List<GameObserver> observers = new ArrayList<>();   //observer design pattern
    private static GameManager instance;

    private int profit;
    private int lives;
    private int round;

    public static GameManager getInstance() {       // singleton (only one instance can be running)
        if (instance == null) instance = new GameManager();
        return instance;
    }

    public void reset() {
        profit = 0;
        lives = 3;
        round = 1;
        notifyObservers();
    }

    public int calculateProfit(Order order, int timeLeft) {
        String[] toppings = order.getToppings();

        crepe.Crepe crepeObj = new crepe.BasicCrepe();  // crepe.Crepe means from package crepe (not a crepe object that has a Crepe object)

        for (String name : toppings) {
            crepe.ToppingData data =
                new crepe.ToppingData(name, crepe.ToppingData.getPrice(name));

            crepeObj = new crepe.Topping(crepeObj, data);
        }

        int baseCost = calculateCrepeCost(toppings);    // this method is below
        int bonus = order.getStrategy().calculateProfit(toppings, timeLeft);

        int earned = baseCost + bonus;

        profit += earned;
        notifyObservers();  // notifies UI (the only observer)

        System.out.println("Crepe cost: " + crepeObj.getCost());
        return earned;
    }

    public void loseLife() {
        lives--;
        notifyObservers();
    }
    public int getLives() { return lives; }
    public int getProfit() { return profit; }

    public void nextRound() {
        round++;
        notifyObservers();
    }
    public int getRound() { return round; }

    @Override
    public void addObserver(GameObserver o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(GameObserver o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (GameObserver o : observers) {
            o.onGameStateChange();
        }
    }

    public int calculateCrepeCost(String[] toppings) {      // shows preview price in UI, also used in other method to find basecost
    crepe.Crepe crepeObj = new crepe.BasicCrepe();

    for (String t : toppings) { // layers all the toppings on top of each other like what we learned with sth sth mocha and then chocolate
        crepeObj = new crepe.Topping(
            crepeObj,
            new crepe.ToppingData(t, crepe.ToppingData.getPrice(t))
        );
    }

    return (int) crepeObj.getCost();
}
}
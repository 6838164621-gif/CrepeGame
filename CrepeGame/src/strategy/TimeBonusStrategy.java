package strategy;

public class TimeBonusStrategy implements ScoreStrategy {

    public int calculateProfit(String[] toppings, int timeLeft) {
        return timeLeft;    // adds time bonus
    }
}
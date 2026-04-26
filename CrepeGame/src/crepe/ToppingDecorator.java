package crepe;

public abstract class ToppingDecorator implements Crepe { // decorator pattern to implement diff types of crepe and toppings
    protected Crepe crepe;

    public ToppingDecorator(Crepe crepe) { // constructor
        this.crepe = crepe;
    }

    @Override
    public String getDescription() { // gets info of crepe (not used in actual program but useful for decorator pattern)
        return crepe.getDescription();
    }

    @Override
    public double getCost() {   // gets cost of crepe to add to profit/score
        return crepe.getCost();
    }
}
package crepe;

public class Topping extends ToppingDecorator {

    private ToppingData data;

    public Topping(Crepe crepe, ToppingData data) {
        super(crepe); // basically just crepe = crepe
        this.data = data;   // gets the data of the topping from ToppingData (has name and cost)
    }

    @Override
    public String getDescription() {        // read ToppingDecorator for info
        return crepe.getDescription() + ", " + data.getName();
    }

    @Override
    public double getCost() {
        return crepe.getCost() + data.getCost();    //crepe getCost from BasicCrepe (25 baht)
    }

    public String getName() {
        return data.getName();
    }
}
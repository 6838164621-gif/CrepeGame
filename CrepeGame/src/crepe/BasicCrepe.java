package crepe;

public class BasicCrepe implements Crepe {

    @Override
    public String getDescription() {
        return "Basic Crepe";
    }

    @Override
    public double getCost() {
        return 25;
    }
}
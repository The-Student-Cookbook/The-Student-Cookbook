package cs506.studentcookbook.Model;

public class Ingredient {

    private String name;
    private String unit;
    private double amount;

    public Ingredient() {
        unit = "";
        name = "";
        amount = 0.0;
    }

    public Ingredient(String name, String unit, double amount) {
        if(name != null) {

            this.name = name.toLowerCase().trim();
        }
        if(unit != null) {
            this.unit = unit.toLowerCase().trim();
        }

        this.amount = amount;
    }

    public void setName(String name) {
        if(name == null)
            return;

        this.name = name.toLowerCase().trim();
    }

    public void setUnit(String unit) {
        if(unit == null)
            return;

        this.name = unit.toLowerCase().trim();
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public double getAmount() {
        return amount;
    }

    public String toString() {
        return this.name;
    }
}

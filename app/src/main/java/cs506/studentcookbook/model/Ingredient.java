package cs506.studentcookbook.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Ingredient implements Parcelable {

    private String name;
    private String unit;
    private double amount;
    private static final String TAG = "IngredientObject";


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

    /*
    * Reconstruct from the Parcel
    */
    public Ingredient(Parcel parcel){

        Log.v(TAG, "Ingredient(Parcel source): Put the parcel back together");
        amount = parcel.readDouble();
        name = parcel.readString();
        unit = parcel.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public Ingredient createFromParcel(Parcel source) {
            return new Ingredient(source);
        }
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        Log.v(TAG, "writeToParcel..." + flags);
        dest.writeDouble(amount);
        dest.writeString(name);
        dest.writeString(unit);
    }

    public int describeContents(){
        return this.hashCode();
    }

    public void setName(String name) {
        if(name == null)
            return;

        this.name = name.toLowerCase().trim();
    }


    public void setUnit(String unit) {
        if(unit == null)
            return;

        this.unit = unit.toLowerCase().trim();
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
        return this.amount + " " + this.unit + " " + this.name;
    }
}

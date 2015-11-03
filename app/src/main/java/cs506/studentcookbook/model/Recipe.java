package cs506.studentcookbook.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Recipe implements Parcelable{

    private int recipeId;
    private int bigOvenId;
    private String name;
    private List<String> bases;
    private List<String> cuisines;
    private String instructions;

    private static final String TAG = "RecipeObject";

    private String imageURL;
    private List<Ingredient> ingredients;
    private List<Technique> techniques;
    private List<Tool> tools;

    private int prepTime;
    private int cookTime;
    private double cost;
    private int rating;
    private boolean isASide;

    public Recipe() {
        setupObjects();
    }

    private void setupObjects() {
        bases = new ArrayList<String>();
        cuisines = new ArrayList<String>();
        ingredients = new ArrayList<Ingredient>();
        techniques = new ArrayList<Technique>();
        tools = new ArrayList<Tool>();
    }

    /*
     * Reconstruct from the Parcel
     */
    public Recipe(Parcel parcel) {
        Log.v(TAG, "Ingredient(Parcel source): Put the parcel back together");
        setupObjects();

        recipeId = parcel.readInt();
        bigOvenId = parcel.readInt();
        name = parcel.readString();
        bases = parcel.readArrayList(bases.getClass().getClassLoader());
        cuisines = parcel.readArrayList(cuisines.getClass().getClassLoader());
        instructions = parcel.readString();
        imageURL = parcel.readString();
        ingredients = parcel.readArrayList(ingredients.getClass().getClassLoader());
        techniques = parcel.readArrayList(techniques.getClass().getClassLoader());
        tools = parcel.readArrayList(tools.getClass().getClassLoader());
        prepTime = parcel.readInt();
        cookTime = parcel.readInt();
        cost = parcel.readDouble();
        rating = parcel.readInt();
        isASide = parcel.readByte() != 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        Log.v(TAG, "writeToParcel..." + flags);
        dest.writeInt(recipeId);
        dest.writeInt(bigOvenId);
        dest.writeString(name);
        dest.writeList(bases);
        dest.writeList(cuisines);
        dest.writeString(instructions);
        dest.writeString(imageURL);
        dest.writeList(ingredients);
        dest.writeList(techniques);
        dest.writeList(tools);
        dest.writeInt(prepTime);
        dest.writeInt(cookTime);
        dest.writeDouble(cost);
        dest.writeInt(rating);
        dest.writeByte((byte) (isASide ? 1 : 0));
    }

    public int describeContents() {
        return this.hashCode();
    }

    public int getId() {
        return recipeId;
    }

    public int getBigOvenId() {
        return bigOvenId;
    }

    public String getName() {
        return name;
    }

    public List<String> getBases() {
        return bases;
    }

    public List<String> getCuisines() {
        return cuisines;
    }

    public String getInstructions() {
        return instructions;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public int getCookTime() {
        return cookTime;
    }

    public double getCost() {
        return cost;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public String getImageURL() {
        return imageURL;
    }

    public int getRating() {
        return rating;
    }

    public boolean getIsASide() {
        return isASide;
    }

    public List<Tool> getTools() {
        return tools;
    }

    public List<Technique> getTechniques() {
        return techniques;
    }

    public void setId(int id) {
        this.recipeId = id;
    }

    public void setBigOvenId(int id) {
        this.bigOvenId = id;
    }

    public void setName(String name) {
        if(name == null)
            return;

        name = name.trim();
        this.name = name;
    }

    public void setBases(List<String> bases) {
        this.bases = bases;
    }

    public void addBase(String base) {
        if(base == null)
            return;

        base = base.toLowerCase().trim();
        this.bases.add(base);
    }

    public void setCuisines(List<String> cuisines) {
        this.cuisines = cuisines;
    }

    public void addCuisine(String cuisine) {
        if(cuisine == null)
            return;

        cuisine = cuisine.toLowerCase().trim();
        this.cuisines.add(cuisine);
    }

    public void setInstructions(String instructions) {
        if(instructions == null)
            return;

        instructions = instructions.trim();
        this.instructions = instructions;
    }

    public void setPrepTime(int time) {
        this.prepTime = time;
    }

    public void setCookTime(int time) {
        this.cookTime = time;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
    }

    public void setImageURL(String imageURL) {
        if(imageURL == null)
            return;

        imageURL = imageURL.trim();
        this.imageURL = imageURL;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setIsASide(boolean side) {
        this.isASide = side;
    }

    public void setTechniques(List<Technique> techniques) {
        this.techniques = techniques;
    }

    public void setTools(List<Tool> tools) {
        this.tools = tools;
    }

    public void addTechnique(Technique technique) {
        this.techniques.add(technique);
    }

    public void addTool(Tool tool) {
        this.tools.add(tool);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Title: " + this.name + ", \n");
        sb.append("ID: " + this.recipeId + ", \n");
        sb.append("BigOvenId: " + this.bigOvenId + ", \n");

        sb.append("Bases: ");
        for(String base : this.bases) {
            sb.append(base + ", ");
        }
        sb.append("\n");

        sb.append("Cuisines: ");
        for(String cuisine : this.cuisines) {
            sb.append(cuisine + ", ");
        }
        sb.append("\n");

        sb.append("Ingredients: ");
        for(Ingredient ingredient : this.ingredients) {
            sb.append(ingredient + ", ");
        }
        sb.append("\n");

        sb.append("Tools: ");
        for(Tool tool : this.tools) {
            sb.append(tool + ", ");
        }
        sb.append("\n");

        sb.append("Technique: ");
        for(Technique technique : this.techniques) {
            sb.append(technique+ ", ");
        }
        sb.append("\n");

        sb.append("Cooktime: " + this.cookTime + ", \n");
        sb.append("Preptime: " + this.prepTime + ", \n");
        sb.append("Image URL: " + this.imageURL + ", \n");
        sb.append("Instructions: " + this.instructions + "\n");

        return sb.toString();
    }
}

package cs506.studentcookbook.model;

import java.util.ArrayList;
import java.util.List;

public class GroceryList {
    private List<String> ingredients;

    public List<String> getIngredients() {
        return ingredients;
    }

    public GroceryList() {
        ingredients = new ArrayList<String>();
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient.getName());
    }

    public void addIngredient(String ingredientName) {
        ingredients.add(ingredientName);
    }

    public void addIngredients(List<Ingredient> ingredients) {
        for (int i = 0; i < ingredients.size(); i++)
        {
            addIngredient(ingredients.get(i).getName());
        }
    }

    //This will add all ingredients within a recipe to grocery list
    public void addRecipe(Recipe recipe) {
        addIngredients(recipe.getIngredients());
    }

    public void removeIngredient(Ingredient ingredient) {
        removeIngredient(ingredient.getName());
    }

    public void removeIngredient(String ingredientName) {
        int index = ingredients.indexOf(ingredientName);

        ingredients.remove(index);
    }

    public void clear() {
        ingredients.clear();
    }

    public boolean equals(GroceryList groceryList) {
        if (!this.ingredients.equals(groceryList.getIngredients()))
            return false;
        else
            return true;
    }
}

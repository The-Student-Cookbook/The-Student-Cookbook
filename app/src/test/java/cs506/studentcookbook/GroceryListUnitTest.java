package cs506.studentcookbook;

import org.junit.Test;

import static org.junit.Assert.*;
import cs506.studentcookbook.model.Ingredient;
import java.util.ArrayList;
import java.util.List;
import cs506.studentcookbook.model.Recipe;
import cs506.studentcookbook.model.GroceryList;


/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class GroceryListUnitTest {
    @Test
    public void comprehensiveTest() throws Exception {
        //Test parameters

        //Some test ingredients
        Ingredient ingredient1 = new Ingredient();
            ingredient1.setName("ingredient1");
            ingredient1.setUnit("unit1");
            ingredient1.setAmount(1);

        Ingredient ingredient2 = new Ingredient();
            ingredient2.setName("ingredient2");
            ingredient2.setUnit("unit2");
            ingredient2.setAmount(2);

        Ingredient ingredient3 = new Ingredient();
            ingredient3.setName("ingredient3");
            ingredient3.setUnit("unit3");
            ingredient3.setAmount(3);

        Ingredient ingredient4 = new Ingredient();
            ingredient4.setName("ingredient4");
            ingredient4.setUnit("unit4");
            ingredient4.setAmount(4);

        //List of ingredients
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
            ingredients.add(ingredient1);
            ingredients.add(ingredient2);
            ingredients.add(ingredient3);
            ingredients.add(ingredient4);

        //List of strings
        List<String> ingredientNames = new ArrayList<String>();
            ingredientNames.add("ingredient1");
            ingredientNames.add("ingredient2");
            ingredientNames.add("ingredient3");
            ingredientNames.add("ingredient4");

        //Recipe object
        Recipe recipe = new Recipe();
        recipe.setIngredients(ingredients);

        //Comparison grocery list objects
        GroceryList g1 = new GroceryList();
        GroceryList g2 = new GroceryList();

        //Test that adding and removing same element gives us same list
        g1.addIngredients(ingredients);

        g1.addIngredient("test");
        g1.removeIngredient("test");
        assertEquals(g1.getIngredients(),ingredientNames);

        //Test adding using recipe object
        g2.addRecipe(recipe);
        assertEquals(g1.getIngredients(),g2.getIngredients());

        //Test clearing
        g1.clear();
        assertEquals(g1.getIngredients().size(), 0);
    }
}
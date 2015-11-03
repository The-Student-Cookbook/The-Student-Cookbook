package cs506.studentcookbook.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cs506.studentcookbook.R;
import cs506.studentcookbook.model.Ingredient;
import cs506.studentcookbook.model.Recipe;
import cs506.studentcookbook.model.Tool;

/**
 * Landing page that gives a brief overview of what the recipe was that was selected
 */
public class MealPlanActivity extends Activity {

    private final String CURRENT_RECIPE_PARCEL_KEY = "CURRENT_RECIPE_PARCEL_KEY";
    private Recipe selectedRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan);

        // Recover the recipe that was passed here
        Bundle savedRecipeBundle = getIntent().getExtras();
        selectedRecipe = savedRecipeBundle.getParcelable(CURRENT_RECIPE_PARCEL_KEY);


        //Setup the click listeners for the various buttons we have
        //Setup the Back Button
        Button backButton = (Button) findViewById(R.id.meal_plan_go_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //Setup the Add To Grocery List Button
        // TODO: Interation 2: Send the grocery list stuff?
        Button addToGroceryButton = (Button) findViewById(R.id.meal_plan_add_to_grocery_button);

        //Setup up the Let's Cook! button
        Button letsCookButton = (Button) findViewById(R.id.meal_plan_lets_cook_button);
        letsCookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Continue to the Cook Meal page
                Intent goToCookMealPage = new Intent(MealPlanActivity.this, CookMealActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(CURRENT_RECIPE_PARCEL_KEY, selectedRecipe);
                goToCookMealPage.putExtras(bundle);
                MealPlanActivity.this.startActivity(goToCookMealPage);
            }
        });


        //Update the textviews on the UI to contain the recipe
        // Recipe Title
        TextView recipeTitle = (TextView) findViewById(R.id.meal_plan_recipe_title);
        recipeTitle.setText(selectedRecipe.getName());

        //Ingredients
        TextView requiredIngredients = (TextView) findViewById(R.id.meal_plan_ingredients_body_text);
        String ingredientsFormattedString = "";
        for(Ingredient ingredientLine: selectedRecipe.getIngredients()){
            ingredientsFormattedString += ingredientLine.toString() + "\n";
        }
        requiredIngredients.setText(ingredientsFormattedString);

        //Tools
        TextView requiredTools = (TextView) findViewById(R.id.meal_plan_tools_body_text);
        String toolsFormattedString = "";
        for(Tool toolLine: selectedRecipe.getTools()){
            ingredientsFormattedString += toolLine.toString() + "\n";
        }
        requiredTools.setText(toolsFormattedString);
    }
}

package cs506.studentcookbook.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

import cs506.studentcookbook.R;
import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.Ingredient;
import cs506.studentcookbook.model.Recipe;
import cs506.studentcookbook.model.Tool;
import cs506.studentcookbook.utils.DownloadImageTask;

/**
 * Landing page that gives a brief overview of what the recipe was that was selected
 */
public class MealPlanActivity extends Activity {

    private static final String TAG = "MealPlanActivity";
    private final String CURRENT_RECIPE_PARCEL_KEY = "CURRENT_RECIPE_PARCEL_KEY";
    private Recipe selectedRecipe;
    private DBTools dbTools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan);

        dbTools = new DBTools(getApplicationContext());

        // Recover the recipe that was passed here
        Bundle savedRecipeBundle = getIntent().getExtras();
        selectedRecipe = savedRecipeBundle.getParcelable(CURRENT_RECIPE_PARCEL_KEY);

        // grabs the image from the web and puts it in the image view
        new DownloadImageTask((ImageView) findViewById(R.id.recipeImageView)).execute(selectedRecipe.getImageURL());

        Log.d(TAG, "Selected recipe = " + selectedRecipe);

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
        addToGroceryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // need list of Ingredients for grocery list
            List<Ingredient> ingredients = selectedRecipe.getIngredients();

            Log.d(TAG, "Ingredients = " + ingredients);
            Log.d(TAG, "Grocery list before adding ingredients = " + dbTools.getGroceryList());

            Iterator<Ingredient> itr = ingredients.iterator();
            while(itr.hasNext()) {
                Ingredient next = itr.next();
                dbTools.addIngredientToGroceryList(next);
            }

            Log.d(TAG, "Grocery list AFTER adding ingredients = " + dbTools.getGroceryList());

            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "Ingredients Added to Grocery List", Toast.LENGTH_SHORT);
            toast.show();
            }
        });

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

package cs506.studentcookbook.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.Preferences;
import cs506.studentcookbook.model.Recipe;
import cs506.studentcookbook.R;
import cs506.studentcookbook.utils.RecipeListViewAdapter;

public class RecipeQueryActivity extends Activity {

    private List<Recipe> chosenRecipeList;
    private DBTools dbTools;
    private View selectedRecipe = null;
    private Recipe selectedRecipeObj = null;

    private final String CURRENT_RECIPE_PARCEL_KEY = "CURRENT_RECIPE_PARCEL_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_query);

        //Give the buttons their click listeners
        // This is the accept button, go to the next activity
        Button nextButton = (Button) findViewById(R.id.recipe_query_nextbutton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Keep going into the recipe
                if (selectedRecipe == null)
                    Toast.makeText(RecipeQueryActivity.this, "Please select a recipe first!", Toast.LENGTH_SHORT).show();
                else{
                    // Continue to the recipe page
                    Intent goToLandingPage = new Intent(RecipeQueryActivity.this, MealPlanActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(CURRENT_RECIPE_PARCEL_KEY, selectedRecipeObj);
                    goToLandingPage.putExtras(bundle);
                    RecipeQueryActivity.this.startActivity(goToLandingPage);
                }
            }
        });


        // This is the cancel button and the alert that goes with it
        Button cancelButton = (Button) findViewById(R.id.recipe_query_cancelbutton);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Prompt to see if they want to leave
                new AlertDialog.Builder(RecipeQueryActivity.this).setTitle("Test").setMessage("Are you sure you want to return to the dashboard?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Return to Dash
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Close Menu/Do Nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        //TODO: Setup the listview with the recipes that were passed along
        ListView recipeList = (ListView) findViewById(R.id.recipe_query_listview);
        recipeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedRecipe != null)
                    selectedRecipe.setBackgroundColor(Color.BLACK);
                selectedRecipe = view;
                selectedRecipeObj = chosenRecipeList.get(position);
                // TODO: Figure out what color a selected recipe should be
                selectedRecipe.setBackgroundColor(Color.RED);
            }

        });

        // EVENTUALLY REMOVE THIS!
        /*chosenRecipeList = new ArrayList<Recipe>();
        Recipe temp = new Recipe();
        temp.setName("Test Recipe");
        temp.setRating(5);
        chosenRecipeList.add(temp);
        temp = new Recipe();
        temp.setName("2nd Recipe");
        temp.setRating(2);
        chosenRecipeList.add(temp);*/

        // TODO: Get rid of the temp var once the null branch of getSuggestedRecipes is setup
        dbTools = DBTools.getInstance(this);
        Preferences tempPref = new Preferences();
        tempPref.setName("");
        chosenRecipeList = dbTools.getSuggestedRecipes(tempPref);

        recipeList.setAdapter(new RecipeListViewAdapter(this, chosenRecipeList));


        //Prompt the user make a selection
        Toast.makeText(this, "Please select a recipe.", Toast.LENGTH_LONG).show();
    }

}

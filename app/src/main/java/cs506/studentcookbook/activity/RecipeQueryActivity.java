package cs506.studentcookbook.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import cs506.studentcookbook.Database.DBTools;
import cs506.studentcookbook.Model.Recipe;
import cs506.studentcookbook.R;

public class RecipeQueryActivity extends Activity {

    private List<Recipe> chosenRecipeList;
    private DBTools dbTools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_query);

        //Give the buttons their click listeners
        Button nextButton = (Button) findViewById(R.id.recipe_query_nextbutton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Keep going into the recipe
            }
        });

        Button cancelButton = (Button) findViewById(R.id.recipe_query_cancelbutton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Prompt to see if they want to leave
                new AlertDialog.Builder(RecipeQueryActivity.this).setTitle("Test").setMessage("Are you sure you want to return to the dashboard?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Return to Dash
                                Intent returnToDash = new Intent(RecipeQueryActivity.this, DashboardActivity.class);
                                RecipeQueryActivity.this.startActivity(returnToDash);
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

        //dbTools = new DBTools(this);
        //chosenRecipeList = dbTools.getSuggestedRecipes(dbTools.getPrefernces());

        //recipeList.setAdapter(new RecipeListViewAdapter(this, chosenRecipeList));


        //Prompt the user make a selection
        Toast.makeText(this, "Please select a recipe.", Toast.LENGTH_LONG).show();

    }

}

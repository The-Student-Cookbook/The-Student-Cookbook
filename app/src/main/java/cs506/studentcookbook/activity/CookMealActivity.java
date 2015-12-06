package cs506.studentcookbook.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import cs506.studentcookbook.R;
import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.Ingredient;
import cs506.studentcookbook.model.Recipe;
import cs506.studentcookbook.model.Tool;
import cs506.studentcookbook.database.DBTools;

import java.util.Date;
import java.text.SimpleDateFormat;

public class CookMealActivity extends Activity {

    //variables for graphical elements
    TextView recipeTitle;
    TextView instruction;
    Button doneCooking;
    DBTools dbTools;

    //variables for data elements
    private Recipe selectedRecipe;
    private String recipeName;
    private String instructions;
    private final String CURRENT_RECIPE_PARCEL_KEY = "CURRENT_RECIPE_PARCEL_KEY";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_meal);

        dbTools = new DBTools(getApplicationContext());

        //get graphical elements to use later
        recipeTitle = (TextView) findViewById(R.id.recipeTitle);
        instruction = (TextView) findViewById(R.id.instruction);
        instruction.setMovementMethod(new ScrollingMovementMethod());
        doneCooking = (Button) findViewById(R.id.doneCooking);
        dbTools = DBTools.getInstance(this);

        //get recipe passed from prior activity
        Bundle savedRecipeBundle = getIntent().getExtras();
        selectedRecipe = savedRecipeBundle.getParcelable(CURRENT_RECIPE_PARCEL_KEY);
        instructions = selectedRecipe.getInstructions();
        recipeName = selectedRecipe.getName();

        // display the instructionsa
        recipeTitle.setText(recipeName);
        instruction.setText(instructions);

        doneCooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Used to obtain date, used when adding recipe to history
                Date currDate = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                String date = sdf.format(currDate);

                //Add recipe to history using today's date
                dbTools.addHasCookedToDatabase(selectedRecipe.getId(),date);

                Intent goToMyRecipesPage = new Intent(CookMealActivity.this, MyRecipesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(CURRENT_RECIPE_PARCEL_KEY, selectedRecipe);
                goToMyRecipesPage.putExtras(bundle);
                CookMealActivity.this.startActivity(goToMyRecipesPage);

                finish();
            }
        });
    }
}
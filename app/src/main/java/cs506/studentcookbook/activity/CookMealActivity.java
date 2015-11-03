package cs506.studentcookbook.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import cs506.studentcookbook.R;
import cs506.studentcookbook.model.Ingredient;
import cs506.studentcookbook.model.Recipe;
import cs506.studentcookbook.model.Tool;

public class CookMealActivity extends Activity {

    //variables for graphical elements
    TextView recipeTitle;
    TextView stepTitle;
    TextView instruction;
    Button previousStep;
    Button nextStep;

    //variables for data elements
    private Recipe selectedRecipe;
    private String recipeName;
    private String instructions; //-------- IMPLEMENT AS LIST IN ITERATION 2
    private int step; //index of the current step not used until ITERATION 2
    private final String CURRENT_RECIPE_PARCEL_KEY = "CURRENT_RECIPE_PARCEL_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_meal);

        //get graphical elements to use later
        recipeTitle = (TextView) findViewById(R.id.recipeTitle);
        stepTitle = (TextView) findViewById(R.id.stepTitle);
        instruction = (TextView) findViewById(R.id.instruction);
        previousStep = (Button) findViewById(R.id.previousStep);
        nextStep = (Button) findViewById(R.id.nextStep);

        //set values to test values for now...
        /*
        recipeName = "Meatloaf";
        instructions = new LinkedList<String>();
        instructions.add("1. Do Something");
        instructions.add("2. Do Something Else");
        instructions.add("3. Bake Eddie at 350 degrees F for 15 minutes per pound.");
        */

        //get recipe passed from prior activity
        Bundle savedRecipeBundle = getIntent().getExtras();
        selectedRecipe = savedRecipeBundle.getParcelable(CURRENT_RECIPE_PARCEL_KEY);
        instructions = selectedRecipe.getInstructions();
        recipeName = selectedRecipe.getName();
        nextStep.setText("Done");

        // display the instructionsa
        recipeTitle.setText(recipeName);
        stepTitle.setText("Step " + (step+1));
        instruction.setText(instructions);
        previousStep.setEnabled(false); //previous step button starts disabled


        /*  --------- REIMPLEMENT IN ITERATION 2
        //set the values of the text fields using text values
        recipeTitle.setText(recipeName);  //set once and forget
        step = 0;
        stepTitle.setText("Step " + (step+1));
        instruction.setText(instructions.get(step));
        previousStep.setEnabled(false); //previous step button starts disabled
        */
        previousStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //not actually implemented/usable until ITERATION 2
                /*
                step--;
                //if we are at the first step, disable previous again
                if (step == 0) {
                    previousStep.setEnabled(false);
                }

                nextStep.setText("Next Step");
                stepTitle.setText("Step " + (step + 1));
                instruction.setText(instructions.get(step));
                */
            }
        });

        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goToMyRecipesPage = new Intent(CookMealActivity.this, MyRecipesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(CURRENT_RECIPE_PARCEL_KEY, selectedRecipe);
                goToMyRecipesPage.putExtras(bundle);
                CookMealActivity.this.startActivity(goToMyRecipesPage);
                finish();

                // ----------------- IMPLEMENT IN ITERATION 2
                //if going to the next step, enable previous step button
                /*
                if(!(previousStep.isEnabled())){
                    previousStep.setEnabled(true);
                }
                    if ((step+1) < instructions.size()){
                        //load the next step's instructions
                        step++;
                        stepTitle.setText("Step " + (step+1));
                        instruction.setText(instructions.get(step));

                    //if this is the last step, change the next step button to 'Done'
                    if((step + 1) == instructions.size()){
                        nextStep.setText("Done");
                    }
                }
                else {
                    //TODO: Go to the next Activity
                }
                */
            }
        });
    }
}
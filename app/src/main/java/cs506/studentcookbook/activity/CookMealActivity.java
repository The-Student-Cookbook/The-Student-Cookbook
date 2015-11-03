package cs506.studentcookbook.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import cs506.studentcookbook.R;

public class CookMealActivity extends Activity {

    //variables for graphical elements
    TextView recipeTitle;
    TextView stepTitle;
    TextView instruction;
    Button previousStep;
    Button nextStep;

    //variables for data elements
    private String recipeName;
    private List<String> instructions; //list of instructions for the recipe
    private int step; //index of the current step

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
        //TODO: change this to get values from the recipe in the bundle passed to this activity
        recipeName = "Meatloaf";
        instructions.add("1. Do Something");
        instructions.add("2. Do Something Else");
        instructions.add("3. Bake Eddie at 350 degrees F for 15 minutes per pound.");

        //set the values of the text fields using text values
        recipeTitle.setText(recipeName);  //set once and forget
        step = 0;
        stepTitle.setText("Step " + (step+1));
        instruction.setText(instructions.get(step));
        previousStep.setEnabled(false); //previous step button starts disabled

        previousStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step--;
                //if we are at the first step, disable previous again
                if(step == 0) {
                    previousStep.setEnabled(false);
                }
                stepTitle.setText("Step " + (step+1));
                instruction.setText(instructions.get(step));
            }
        });

        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if going to the next step, enable previous step button
                if(!(previousStep.isEnabled())){
                    previousStep.setEnabled(true);
                }
                if (step < instructions.size()){
                    //load the next step's instructions
                    step++;
                    stepTitle.setText("Step " + (step+1));
                    instruction.setText(instructions.get(step));

                    //if this is the last step, change the next step button to 'Done'
                    if(step == instructions.size()){
                        nextStep.setText("Done");
                    }
                }
                else {
                    //TODO: Go to the next Activity
                }
            }
        });
    }
}
package cs506.studentcookbook.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.session.PlaybackState;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cs506.studentcookbook.R;
import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.Preferences;
import cs506.studentcookbook.model.Recipe;
import cs506.studentcookbook.utils.RecipeListViewAdapter;

public class CuisineQuery extends Activity {



    private List<Recipe> returnedRecipes;
    private List<Recipe> selectedRecipes;
    private View selectedRecipeView = null;
    private DBTools dbTools;
    private Preferences prefs;
    private int cost = 0;
    private String cookingTime;
    private String prepTime;
    private String groupSize;
    private int numSelected = 0;
    private final String RECIPIES = "RECIPIES";
    private final String NUM_RECIPIES = "NUM_RECIPIES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisine_query);

        dbTools = DBTools.getInstance(this);
        selectedRecipes = new ArrayList<>();

        // Cancel Button
        Button cancelButton = (Button) findViewById(R.id.quisineQuery_cancelbutton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Next Button
        Button nextButton = (Button) findViewById(R.id.quisineQuery_nextbutton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (returnedRecipes.size() < 1)
                    Toast.makeText(CuisineQuery.this, "Please select at least one cuisine", Toast.LENGTH_LONG).show();

                    // Go to the meal plan page?
                else {
                    Intent mealBaseQIntent = new Intent(CuisineQuery.this, MealBaseQueryActivity.class);
                    // Potentially package stuff here
                    Bundle bundle = new Bundle();
                    for(Recipe r: selectedRecipes){
                        bundle.putParcelable(RECIPIES + numSelected, r);
                        numSelected++;
                    }
                    bundle.putInt(NUM_RECIPIES, numSelected);

                    CuisineQuery.this.startActivity(mealBaseQIntent);
                }

            }
        });

        prefs = dbTools.getPreferences();

        //Recover the saved info
        Bundle savedBundle = getIntent().getExtras();
        if(savedBundle != null){
            cookingTime = savedBundle.getString("cookTime");
            prepTime = savedBundle.getString("prepTime");
            groupSize = savedBundle.getString("groupSize");
            cost = savedBundle.getInt("cost");

            try{
                prefs.setCookTime(Integer.parseInt(cookingTime));
                prefs.setPrepTime(Integer.parseInt(prepTime));
                prefs.setGroupSize(Integer.parseInt(groupSize));
            }catch (Exception e) {
                prefs.setCookTime(60);
                prefs.setPrepTime(60);
                prefs.setGroupSize(1);
            }
        }

        //Get a list of recipes now
        returnedRecipes = dbTools.getSuggestedRecipes(prefs);


        ListView listView = (ListView) findViewById(R.id.quisineQuery_listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedRecipeView = view;

                if(selectedRecipes.contains(returnedRecipes.get(position))){
                    selectedRecipeView.setBackgroundResource(android.R.drawable.list_selector_background);
                    selectedRecipes.remove(returnedRecipes.get(position));
                }else {
                    selectedRecipes.add(returnedRecipes.get(position));
                    selectedRecipeView.setBackgroundColor(Color.CYAN);
                }
            }

        });

        listView.setAdapter(new RecipeListViewAdapter(this, returnedRecipes));


        //Prompt the user make a selection
        Toast.makeText(this, "Please select recipes.", Toast.LENGTH_SHORT).show();

    }
}


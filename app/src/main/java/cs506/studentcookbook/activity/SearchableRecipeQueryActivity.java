package cs506.studentcookbook.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cs506.studentcookbook.R;
import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.Recipe;

public class SearchableRecipeQueryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable_recipe_query);

        DBTools tools = DBTools.getInstance(this);

//        List<Recipe> recipes = tools.getSuggestedRecipes(null);
        // itr through and display each recipe

        // for text search
        // prefs.setName then call getSuggestedRecipes
    }


}

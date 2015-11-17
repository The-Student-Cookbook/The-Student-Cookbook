package cs506.studentcookbook.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import cs506.studentcookbook.R;
import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.Recipe;

public class MyRecipesActivity extends Activity {

    private static final String LOG_TAG = "MyRecipesActivty";

    private DBTools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);
        tools = new DBTools(this);

        Log.d(LOG_TAG, "test");

        // list out the past recipes the user has cooked, along with their "like" status

        // get the method to delete a recipe from history
        // get the method to like a recipe
        // get the method to dislike a recipe

//        List<Recipe> history = tools.getHasCooked();

    }
}

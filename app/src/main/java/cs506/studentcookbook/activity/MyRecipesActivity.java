package cs506.studentcookbook.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cs506.studentcookbook.R;
import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.Ingredient;
import cs506.studentcookbook.model.Preferences;
import cs506.studentcookbook.model.Recipe;
import cs506.studentcookbook.utils.HistoryListViewAdapter;

public class MyRecipesActivity extends Activity {

    private static final String LOG_TAG = "MyRecipesActivty";

    private DBTools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);
        tools = new DBTools(this);

        // hide empty msg initially
        TextView emptyView = (TextView) findViewById(R.id.empty_my_recipes);
        emptyView.setVisibility(View.INVISIBLE);

//        List<Recipe> history = tools.getHasCooked();
        // TODO: this is temporary -- need getHasCooked to be populated
        Preferences tempPref = new Preferences();
        tempPref.setName("");
        List<Recipe> history = tools.getSuggestedRecipes(tempPref);

        if(history == null || history.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        }

        Iterator<Recipe> itr = history.iterator();
        while(itr.hasNext()) {
            Recipe next = itr.next();

            boolean liked = tools.getRecipeLiked(next);
            String imageUrl = next.getImageURL();
            String title = next.getName();
        }

        // TODO: need following methods from backend
        // a way to set like/dislike status of a recipe
        // a way to delete a recipe from their history
        // the cook date for a history item

        ListView listView = (ListView) findViewById(R.id.history_listview);
        HistoryListViewAdapter adapter = new HistoryListViewAdapter(this, history);

        listView.setAdapter(adapter);

//        Button deleteBtn = (Button) findViewById(R.id.history_listitem_delete);
//        deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Context context = getApplicationContext();
//                Toast toast = Toast.makeText(context, "Need to delete this recipe from my recipes", Toast.LENGTH_SHORT);
//                toast.show();
//            }
//        });

        // also need to figure out how to move from MyRecipesActivity to MealPlanActivity
    }
}

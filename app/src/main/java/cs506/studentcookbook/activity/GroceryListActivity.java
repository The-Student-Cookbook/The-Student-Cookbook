package cs506.studentcookbook.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cs506.studentcookbook.R;
import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.GroceryList;

public class GroceryListActivity extends Activity {

    private static final String LOG_TAG = "GroceryListActivity";

    private DBTools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tools = new DBTools(this);

        setContentView(R.layout.activity_grocery_list);

        // hide 'shopping list empty' initially
        TextView emptyView = (TextView) findViewById(R.id.empty_grocery_list);
        emptyView.setVisibility(View.INVISIBLE);

        GroceryList groceryList = tools.getGroceryList();

        Log.d(LOG_TAG, "Grocery list: " + groceryList.toString());

        // Not hooked up yet in MealPlanActivity --> using mocked up data for now
         List<String> ingredients = tools.getGroceryList().getIngredients();

        if(ingredients == null || ingredients.isEmpty()) {
            // empty list -- nothing to show user
            emptyView.setVisibility(View.VISIBLE);
        } else {
            ListView listView = (ListView) findViewById(R.id.grocery_listview);
            listView.setAdapter(new ArrayAdapter<>(this, R.layout.ingredient_listitem, R.id.ingredient_name, ingredients));

        }

        //Setup up the Let's Cook! button
        Button deleteBtn = (Button) findViewById(R.id.grocery_list_delete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: delete from list and refresh new list
            }
        });
    }


}

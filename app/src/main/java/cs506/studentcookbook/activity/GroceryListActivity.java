package cs506.studentcookbook.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cs506.studentcookbook.R;
import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.GroceryList;
import cs506.studentcookbook.model.Model;
import cs506.studentcookbook.utils.IngredientListViewAdapter;

public class GroceryListActivity extends Activity {

    private static final String LOG_TAG = "GroceryListActivity";

    private DBTools tools;
    private ArrayAdapter adapter;
    ListView listView;

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
        // List<String> groceryList = tools.getGroceryList().getIngredients();
        List<String> ingredients = new ArrayList<>();
        ingredients.add("buttermilk");
        ingredients.add("chicken fryers");
        ingredients.add("flour");
        ingredients.add("salt");
        ingredients.add("vegetable oil");

        if(ingredients == null || ingredients.isEmpty()) {
            // empty list -- nothing to show user
            emptyView.setVisibility(View.VISIBLE);
        } else {
            Model[] modelItems = new Model[5];
            modelItems[0] = new Model("buttermilk", 0);
            modelItems[1] = new Model("chicken fryers", 0);
            modelItems[2] = new Model("flour", 0);
            modelItems[3] = new Model("salt", 0);
            modelItems[4] = new Model("vegetable oil", 0);

            IngredientListViewAdapter adapter = new IngredientListViewAdapter(this, modelItems);
            ListView listView = (ListView) findViewById(R.id.grocery_listview);
            listView.setAdapter(adapter);

            listView = (ListView) findViewById(R.id.grocery_listview);

            listView.setAdapter(adapter);
        }
    }


}

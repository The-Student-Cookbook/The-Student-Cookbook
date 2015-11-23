package cs506.studentcookbook.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import cs506.studentcookbook.R;
import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.GroceryList;
import cs506.studentcookbook.model.Ingredient;

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

        List<String> ingredients = tools.getGroceryList().getIngredients();

        if(ingredients == null || ingredients.isEmpty()) {
            // empty list -- nothing to show user
            emptyView.setVisibility(View.VISIBLE);

            // hide delete btn too
            Button deleteBtn = (Button) findViewById(R.id.grocery_list_delete);
            deleteBtn.setVisibility(View.INVISIBLE);
        } else {
            ListView listView = (ListView) findViewById(R.id.grocery_listview);
            listView.setAdapter(new ArrayAdapter<>(this, R.layout.ingredient_listitem, R.id.ingredient_name, ingredients));
        }

        //Setup up the delete button
        Button deleteBtn = (Button) findViewById(R.id.grocery_list_delete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView listView = (ListView) findViewById(R.id.grocery_listview);

                Log.d(LOG_TAG, "List view child count: " + listView.getChildCount());
                Log.d(LOG_TAG, "Checked item pos: " + listView.getCheckedItemPositions());
                Log.d(LOG_TAG, "Checked item ids: " + Arrays.toString(listView.getCheckedItemIds()));

                boolean checkedSomething = false;
                for (int i = 0; i < listView.getChildCount(); i++) {
                    CheckBox cb = (CheckBox) listView.getChildAt(i).findViewById(R.id.ingredient_checkbox);
                    if(cb.isChecked()) {
                        TextView name = (TextView) listView.getChildAt(i).findViewById(R.id.ingredient_name);
                        String ingredientName = name.getText().toString();

                        Log.d(LOG_TAG, "Removing \"" + ingredientName + "\" from gocery list");

                        tools.removeIngredientFromGroceryList(new Ingredient(ingredientName, null, -1));
                        checkedSomething = true;
                    }
                }

                if(!checkedSomething) {
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "Nothing selected to delete!", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                GroceryList groceryList = tools.getGroceryList();
                Log.d(LOG_TAG, "Grocery list after removing checked ingredients: " + groceryList.toString());

                // redraw list view
                List<String> ingredients = groceryList.getIngredients();
                if(ingredients == null || ingredients.isEmpty()) {
                    // hide empty list view
                    listView.setVisibility(View.INVISIBLE);

                    TextView emptyView = (TextView) findViewById(R.id.empty_grocery_list);
                    emptyView.setVisibility(View.VISIBLE);

                    // hide delete btn
                    Button deleteBtn = (Button) findViewById(R.id.grocery_list_delete);
                    deleteBtn.setVisibility(View.INVISIBLE);
                } else {
                    listView.setAdapter(new ArrayAdapter<>(getBaseContext(), R.layout.ingredient_listitem, R.id.ingredient_name, ingredients));
                }


            }
        });
    }


}

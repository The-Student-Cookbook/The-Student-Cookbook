package cs506.studentcookbook.activity;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.database.DatabaseTestActivity;
import cs506.studentcookbook.model.Preferences;
import cs506.studentcookbook.model.Recipe;
import cs506.studentcookbook.model.User;

public class DashboardActivity extends ListActivity {

    private final String CURRENT_RECIPE_PARCEL_KEY = "CURRENT_RECIPE_PARCEL_KEY";
    private static final String TAG = "DashboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //First check and see if there is a profile:
        DBTools db = DBTools.getInstance(this);
        db.populateDatabase();
        User user = db.getUserSettings();

        // First launch
        if(user.getEmail() == null){
            Intent firstLaunch = new Intent(this, AskAboutProfileSetupActivity.class);
            this.startActivity(firstLaunch);
        }

        // TODO: there's probably a more robust way to do this
        // we should be able to define a list of Activities that get started when clicking a list item
        String[] dashboardItems = {"Help me choose...", "Choose for me...", "Browse all...",
                "My Recipes", "Grocery List", "Settings" /*, "Start database activity (temporary)"*/};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dashboardItems);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        switch (position) {
            case 0:
                Intent intent = new Intent(this, PersistentDataQueryActivity.class);
                this.startActivity(intent);
                break;
            case 1:
                Intent goToLandingPage = new Intent(DashboardActivity.this, MealPlanActivity.class);
                DBTools dbTools = DBTools.getInstance(this);
                Preferences tempPref = new Preferences();
                tempPref.setName("");
                List<Recipe> chosenRecipeList = dbTools.getSuggestedRecipes(tempPref);
                Random gen = new Random();
                Bundle bundle = new Bundle();
                bundle.putParcelable(CURRENT_RECIPE_PARCEL_KEY, chosenRecipeList.get(gen.nextInt(chosenRecipeList.size())));
                goToLandingPage.putExtras(bundle);
                this.startActivity(goToLandingPage);
                break;
            case 2:
                intent = new Intent(this, SearchableRecipeQueryActivity.class);
                this.startActivity(intent);
                break;
            case 3:
                intent = new Intent(this, MyRecipesActivity.class);
                this.startActivity(intent);
                break;
            case 4:
                intent = new Intent(this, GroceryListActivity.class);
                this.startActivity(intent);
                break;
            case 5:
                intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                break;
            case 6:
                intent = new Intent(this, DatabaseTestActivity.class);
                this.startActivity(intent);
                break;
            default:
                String error = "No activity for position " + position;
                Log.w(TAG, error);
                Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, error, Toast.LENGTH_SHORT);
                toast.show();
        }
    }
}

package cs506.studentcookbook.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cs506.studentcookbook.R;
import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.Preferences;
import cs506.studentcookbook.utils.ProfileExpandableListAdapter;

public class ProfileActivity extends Activity {

    private ExpandableListView preferencesList;
    private ProfileExpandableListAdapter profileExpListAdapter;

    private DBTools dbTools;
    private Preferences preferences;
/*    private ArrayList<String> profileCategories;
    private ArrayList<ArrayList<String>> profileData;*/

    //Preferences Lists
    private final String[] listHeaders = {"Allergies", "Dietary Restrictions", "Preferences", "Tools"};
    private ArrayList<String> allergiesList;
    private final String PROFILE_PARCEL_ALLERGIES_KEY = "PROFILE_PARCEL_ALLERGIES_KEY";
    private ArrayList<String> dietaryList;
    private final String PROFILE_PARCEL_DIETARY_KEY = "PROFILE_PARCEL_DIETARY_KEY";
    private ArrayList<String> preferences_List;
    private final String PROFILE_PARCEL_PREFERENCES_KEY = "PROFILE_PARCEL_PREFERENCES_KEY";
    private ArrayList<String> toolsList;
    private final String PROFILE_PARCEL_TOOLS_KEY = "PROFILE_PARCEL_TOOLS_KEY";
    private ArrayList<ArrayList<String>> listOfLists;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile");

        dbTools = DBTools.getInstance(this);

        //Setup the preferences
        setupPreferences();

        //Grab the list view
        preferencesList = (ExpandableListView) findViewById(R.id.profile_expand_list);

        profileExpListAdapter = new ProfileExpandableListAdapter(this, listHeaders, listOfLists);
        preferencesList.setAdapter(profileExpListAdapter);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_activity_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_editProfile:
                // User chose the "Edit Profile" item
                Intent switchToEditProfile = new Intent(this, EditProfileActivity.class);

                Bundle bundle = new Bundle();
                bundle.putStringArrayList(PROFILE_PARCEL_ALLERGIES_KEY, allergiesList);
                bundle.putStringArrayList(PROFILE_PARCEL_TOOLS_KEY, toolsList);
                bundle.putStringArrayList(PROFILE_PARCEL_DIETARY_KEY, dietaryList);
                bundle.putStringArrayList(PROFILE_PARCEL_PREFERENCES_KEY, preferences_List);
                switchToEditProfile.putExtras(bundle);

                this.startActivity(switchToEditProfile);
                return true;


            default:
                // Error of some sort
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        // Update the data set
        setupPreferences();
        profileExpListAdapter.notifyDataSetChanged();
    }

    /**
     * Populates the lists with the relevant preference data
     */
    private void setupPreferences(){
        //Get the data here for the list
        preferences = dbTools.getPreferences();
        listOfLists = new ArrayList<>();


/*      allergiesList = preferences.getAllergies();

        dietaryList = preferences.getDietaryRestrictions();

        preferences_List = preferences.getPreferences();

        toolsList = preferences.getTools();*/

        allergiesList = new ArrayList<>();
        dietaryList = new ArrayList<>();
        preferences_List = new ArrayList<>();
        toolsList = new ArrayList<>();


        // TODO: remove the test data
        allergiesList.add("Deez");
        allergiesList.add("Nuts");

        dietaryList.add("eggs");
        dietaryList.add("milk");
        dietaryList.add("cheese");

        preferences_List.add("Shaken, not stirred");

        toolsList.add("Lincoln Park");
        toolsList.add("skillet");
        toolsList.add("stuff");
        toolsList.add("idk");

        listOfLists.add(allergiesList);
        listOfLists.add(dietaryList);
        listOfLists.add(preferences_List);
        listOfLists.add(toolsList);

    }

}

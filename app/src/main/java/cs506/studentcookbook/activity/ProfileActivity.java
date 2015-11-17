package cs506.studentcookbook.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cs506.studentcookbook.R;
import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.Preferences;
import cs506.studentcookbook.model.Tool;
import cs506.studentcookbook.model.User;
import cs506.studentcookbook.utils.ProfileExpandableListAdapter;

public class ProfileActivity extends Activity {

    private ExpandableListView preferencesList;
    private ProfileExpandableListAdapter profileExpListAdapter;

    private DBTools dbTools;
    private Preferences preferences;
    private User user;


    //Preferences Lists
    private final String[] listHeaders = {"Allergies", "Preferences", "Tools"};
    private ArrayList<String> allergiesList;
    private ArrayList<String> preferences_List;
    private ArrayList<String> toolsList;

    private ArrayList<ArrayList<String>> listOfLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile");

        //Get the user profile
        dbTools = DBTools.getInstance(this);

        //Setup the preferences
        setupPreferences();

        //Grab the list view
        preferencesList = (ExpandableListView) findViewById(R.id.profile_expand_list);

        profileExpListAdapter = new ProfileExpandableListAdapter(this, listHeaders, listOfLists);
        preferencesList.setAdapter(profileExpListAdapter);


        Button homeButton = (Button) findViewById(R.id.profile_homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnHome = new Intent(ProfileActivity.this, DashboardActivity.class);
                ProfileActivity.this.startActivity(returnHome);
            }
        });

        TextView emailLocation = (TextView) findViewById(R.id.profile_EmailLocation);
        emailLocation.setText("Email: " + user.getEmail());

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
        preferencesList.setAdapter(profileExpListAdapter);
    }


    /**
     * Populates the lists with the relevant preference data
     */
    private void setupPreferences(){
        //Get the data here for the list
        preferences = dbTools.getPreferences();
        user = dbTools.getUserSettings();
        listOfLists = new ArrayList<>();

        allergiesList = (ArrayList<String>) dbTools.getAllergicBases();

        preferences_List = (ArrayList<String>) preferences.getLikedBases();

        ArrayList<Tool> tempToolList = (ArrayList<Tool>)dbTools.getTools();
        toolsList = new ArrayList<>();
        for(Tool t: tempToolList){
            toolsList.add(t.getName());
        }

        listOfLists.add(allergiesList);
        listOfLists.add(preferences_List);
        listOfLists.add(toolsList);

    }

}

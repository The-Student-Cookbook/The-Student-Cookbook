package cs506.studentcookbook.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cs506.studentcookbook.R;
import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.Preferences;
import cs506.studentcookbook.utils.ProfileExpandableListAdapter;

public class EditProfileActivity extends Activity {

    private ExpandableListView preferencesList;
    private ProfileExpandableListAdapter profileExpListAdapter;
    private DBTools dbTools;
    private Preferences preferences;
    private List<String> profileCategories;
    private ArrayList<ArrayList<String>> profileData;


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
        setTitle("Edit Profile");

        // Recover the preferences that were passed here
        setupPreferences();

        // Grab an instance of the DBTools
        dbTools = DBTools.getInstance(this);




        //Grab the list view
        preferencesList = (ExpandableListView) findViewById(R.id.profile_expand_list);


        profileExpListAdapter = new ProfileExpandableListAdapter(this, listHeaders, listOfLists);
        preferencesList.setAdapter(profileExpListAdapter);
        preferencesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                int childPosition = ExpandableListView.getPackedPositionChild(id);

                // Add item to the pref group
                if(ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                    builder.setTitle("Add Item to " + listHeaders[groupPosition] + ":");

                    // Set up the input
                    final EditText input = new EditText(EditProfileActivity.this);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT );
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String toAdd = input.getText().toString();
                            if(toAdd == null)
                                Toast.makeText(EditProfileActivity.this, "Text must be added", Toast.LENGTH_SHORT).show();
                            else
                                listOfLists.get(groupPosition).add(toAdd);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }

                // Delete the item that was clicked from pref's
                else if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {


                    // TODO: Finish this ish up once the DB stuff is good
                    // Removed and update prefs
                    String removed = listOfLists.get(groupPosition).remove(childPosition);
                    //dbTools.
                    profileExpListAdapter.notifyDataSetChanged();

                    // Return true as we are handling the event.
                    return true;
                }



                return false;
            }
        });

        // TODO: Get Done/Cancel buttons working
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_profile_activity_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_editProfile_help:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("How to Edit Profile:")
                        .setMessage(R.string.edit_profile_helpDialog)
                        .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                                            }
                        }).show();
            return true;


            default:
                // Error of some sort
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);

        }
    }

    private void setupPreferences(){
        Bundle savedProfileInfo = getIntent().getExtras();
        allergiesList = savedProfileInfo.getStringArrayList(PROFILE_PARCEL_ALLERGIES_KEY);
        dietaryList = savedProfileInfo.getStringArrayList(PROFILE_PARCEL_DIETARY_KEY);
        preferences_List = savedProfileInfo.getStringArrayList(PROFILE_PARCEL_PREFERENCES_KEY);
        toolsList = savedProfileInfo.getStringArrayList(PROFILE_PARCEL_TOOLS_KEY);

        listOfLists = new ArrayList<>();
        listOfLists.add(allergiesList);
        listOfLists.add(dietaryList);
        listOfLists.add(preferences_List);
        listOfLists.add(toolsList);
    }

    // TODO: Setup this list with real data
    private void setupList(){
        //Get the data here for the list
        preferences = dbTools.getPreferences();
        profileCategories = preferences.getDislikedCuisines();
        profileData = new ArrayList<>();





        // TEMP
        profileCategories.add("Allergies");
        profileCategories.add("Tools");
        profileCategories.add("Dietary Restrictions");
        List<String> tempData = new ArrayList<>();
        tempData.add("First");
        tempData.add("1");
        tempData.add("2");

        for(String cat: profileCategories)
            profileData.get(profileCategories.indexOf(cat)).addAll(tempData);


    }
}

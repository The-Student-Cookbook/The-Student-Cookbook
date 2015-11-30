package cs506.studentcookbook.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cs506.studentcookbook.R;
import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.Preferences;
import cs506.studentcookbook.model.Tool;
import cs506.studentcookbook.model.User;
import cs506.studentcookbook.utils.ProfileExpandableListAdapter;

public class EditProfileActivity extends Activity {

    private ExpandableListView preferencesList;
    private ProfileExpandableListAdapter profileExpListAdapter;
    private DBTools dbTools;
    private User user;
    private Preferences preferences;
    private List<String> profileCategories;
    private ArrayList<ArrayList<String>> profileData;


    //Preferences Lists
    private final String[] listHeaders = {"Allergies", "Preferences", "Tools"};
    private ArrayList<String> allergiesList;
    private ArrayList<String> preferences_List;
    private ArrayList<String> toolsList;
    private ArrayList<ArrayList<String>> listOfLists;
    private ArrayList<Tool> actualToolList;
    private ArrayList<Tool> toolToRemove;
    private ArrayList<String> allergiesToRemove;
    private ArrayList<String> preferencesToRemove;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setTitle("Edit Profile");

        // Grab an instance of the DBTools
        dbTools = DBTools.getInstance(this);
        user = dbTools.getUserSettings();
        preferences = dbTools.getPreferences();
        toolToRemove = new ArrayList<>();
        allergiesToRemove = new ArrayList<>();
        preferencesToRemove = new ArrayList<>();

        // Recover the preferences that were passed here
        setupPreferences();

        //Grab the list view
        preferencesList = (ExpandableListView) findViewById(R.id.editProfile_expandListview);

        setupExpandableMenu();

        //Setup Email edit button
        Button editEmailButton = (Button) findViewById(R.id.editProfile_editEmail);
        String email = user.getEmail();
        if(email.equals(null))
            email = "Not Available";
        editEmailButton.setText(email);
        editEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Setup alert dialog to get user's email
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                builder.setTitle("Set Email:");

                // Set up the input
                final EditText input = new EditText(EditProfileActivity.this);
                // Specify the type of input expected
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS );
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newEmail = input.getText().toString();
                        if(newEmail == null)
                            Toast.makeText(EditProfileActivity.this, "Not a valid Email Address!", Toast.LENGTH_SHORT).show();
                        else{
                            user.setEmail(newEmail);
                            ((Button) findViewById(R.id.editProfile_editEmail)).setText(newEmail);
                        }

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
        });

        // Done button, returns to the profile screen
        Button doneButton = (Button) findViewById(R.id.editprofile_done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setAllergicBases(listOfLists.get(0));
                for(String prefs: listOfLists.get(1)){
                    dbTools.incrementCuisineRating(0, prefs, 1, DBTools.LIKE);
                }
                //user.setTools();

                for(String allergy: allergiesToRemove)
                    dbTools.removeAllergicBase(allergy);

                for(String pref: preferencesToRemove)
                    preferences.removeLikedBase(pref);

                for(Tool tool: toolToRemove)
                    dbTools.removeTool(tool);

                dbTools.setPreferences(preferences);
                dbTools.setUserSettings(user);

                finish();
            }
        });

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

    /**
     * Sets up all of the various things associated with the expandable menu
     */
    private void setupExpandableMenu(){
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
                    // Specify the type of input expected
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

                    // Removed and update prefs
                    String removed = listOfLists.get(groupPosition).remove(childPosition);
                    switch (groupPosition) {
                        case 0:
                            allergiesToRemove.add(removed);
                            break;
                        case 1:
                            dbTools.incrementBaseRating(0, removed, 1, DBTools.DISLIKE);
                            break;

                        case 2:
                            Tool toRemove = actualToolList.get(childPosition);
                            toolToRemove.add(toRemove);
                            break;

                        default:
                            break;
                    }

                    profileExpListAdapter.notifyDataSetChanged();
                    // Return true as we are handling the event.
                    return true;
                }

                return false;
            }
        });
    }

    private void setupPreferences(){
        //Get the data here for the list
        preferences = dbTools.getPreferences();
        user = dbTools.getUserSettings();
        listOfLists = new ArrayList<>();


        allergiesList = (ArrayList<String>) dbTools.getAllergicBases();

        preferences_List = (ArrayList<String>) preferences.getLikedBases();

        toolsList = new ArrayList<>();
        actualToolList = (ArrayList<Tool>)dbTools.getTools();
        for(Tool t: actualToolList){
            toolsList.add(t.getName());
        }

        listOfLists.add(allergiesList);
        listOfLists.add(preferences_List);
        listOfLists.add(toolsList);
    }
}

package cs506.studentcookbook.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import cs506.studentcookbook.R;
import cs506.studentcookbook.database.DBTools;

public class ClearActivity extends Activity {

    DBTools dbTools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear);

        dbTools = DBTools.getInstance(this);

        Button confirmClear = (Button) findViewById(R.id.confirmClear);

        confirmClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox profile = (CheckBox) findViewById(R.id.deleteProfile);
                CheckBox ratings = (CheckBox) findViewById(R.id.deleteRatings);
                CheckBox history = (CheckBox) findViewById(R.id.deleteCookedHistory);

                //Give a warning dialog before deleting
                if(profile.isChecked() || ratings.isChecked() || history.isChecked()){
                    //TODO: Add a dialog to confirm deletion of data
                }

                if(profile.isChecked()) {
                    //TODO: Delete Profile
                }
                if(ratings.isChecked()) {
                    dbTools.clearRecipeRatings();
                }
                if(history.isChecked()) {
                    dbTools.clearHasCooked();
                }
                Intent goToSettings = new Intent(ClearActivity.this, SettingsActivity.class);
                ClearActivity.this.startActivity(goToSettings);
                finish();
            }
        });
    }
}

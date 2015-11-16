package cs506.studentcookbook.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import cs506.studentcookbook.R;

public class ClearActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear);

        Button confirmClear = (Button) findViewById(R.id.confirmClear);

        confirmClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox profile = (CheckBox) findViewById(R.id.deleteProfile);
                CheckBox history = (CheckBox) findViewById(R.id.deleteHistory);

                if(profile.isChecked()) {
                    //TODO: Delete Profile
                }
                if(history.isChecked()) {
                    //TODO: Delete History
                }
                Intent goToSettings = new Intent(ClearActivity.this, SettingsActivity.class);
                ClearActivity.this.startActivity(goToSettings);
                finish();
            }
        });
    }
}

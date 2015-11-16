package cs506.studentcookbook.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cs506.studentcookbook.R;
import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.User;

public class AskAboutProfileSetupActivity extends Activity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_about_profile_setup);
        setTitle("Account Setup:");

        final DBTools dbTools = DBTools.getInstance(this);
        user = dbTools.getUserSettings();

        user.setId(100);
        dbTools.setUserSettings(user);

        Button yes = (Button) findViewById(R.id.firstsetup_yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get an email address:
                AlertDialog.Builder builder = new AlertDialog.Builder(AskAboutProfileSetupActivity.this);
                builder.setTitle("Enter Email Address:");

                // Set up the input
                final EditText input = new EditText(AskAboutProfileSetupActivity.this);
                // Specify the type of input expected
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String emailAdr = input.getText().toString();
                        if (emailAdr == null)
                            Toast.makeText(AskAboutProfileSetupActivity.this, "Please enter an email address", Toast.LENGTH_SHORT).show();
                        else {
                            user.setEmail(emailAdr);
                            dbTools.setUserSettings(user);

                            Intent goToEditProfile = new Intent(AskAboutProfileSetupActivity.this, ProfileActivity.class);
                            AskAboutProfileSetupActivity.this.startActivity(goToEditProfile);
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

        Button never = (Button) findViewById(R.id.firstsetup_never);
        never.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setEmail("Never");
                dbTools.setUserSettings(user);
                finish();
            }
        });

    }
}

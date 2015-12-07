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

public class SettingsActivity extends ListActivity {

    private static final String TAG = "SettingsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_dashboard);

        // TODO: there's probably a more robust way to do this
        // we should be able to define a list of Activities that get started when clicking a list item
        String[] settingsItems = {"Profile", "Clear Data" /*"Backup", "Restore",*/};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, settingsItems);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Bundle b = new Bundle();
        boolean isBackup;

        switch (position) {
            case 0:
                Intent intent = new Intent(this, ProfileActivity.class);
                this.startActivity(intent);
                break;
            case 1:
                intent = new Intent(this, ClearActivity.class);
                this.startActivity(intent);
                break;
            case 2:
                isBackup = true;
                b = new Bundle();
                b.putBoolean("isBackup", isBackup);
                intent = new Intent(this, BackupAndRestoreActivity.class);
                intent.putExtras(b);
                this.startActivity(intent);
                break;
            case 3:
                isBackup = false;
                b = new Bundle();
                b.putBoolean("isBackup", isBackup);
                intent = new Intent(this, BackupAndRestoreActivity.class);
                intent.putExtras(b);
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

package cs506.studentcookbook.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import cs506.studentcookbook.R;

public class BackupAndRestoreActivity extends Activity {

    private boolean isBackup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_and_restore);

        TextView title = (TextView) findViewById(R.id.backupAndRestoreTitle);

        Bundle b = getIntent().getExtras();
        isBackup = b.getBoolean("isBackup");

        if(isBackup){
            title.setText("BACKUP");
        } else {
            title.setText("RESTORE");
        }
    }
}

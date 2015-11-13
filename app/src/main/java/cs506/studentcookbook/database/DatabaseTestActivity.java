package cs506.studentcookbook.database;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import cs506.studentcookbook.R;

public class DatabaseTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);
    }

    public void onButtonClick(View view) {
        DBTools db = DBTools.getInstance(this);
        db.populateDatabase();
    }

    public void onClearDatabase(View view) {
        DBTools db = DBTools.getInstance(this);
        db.resetDatabase();
    }
}

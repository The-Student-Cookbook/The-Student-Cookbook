package cs506.studentcookbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import cs506.studentcookbook.Database.DatabaseTestActivity;

public class HelloWorldActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //TextView text = new TextView(this);
        //text.setText("STUDENT COOKBOOK TEST");
        //setContentView(text);
        setContentView(R.layout.main);
    }

    public void startDatabase(View view) {
        Intent intent = new Intent(this, DatabaseTestActivity.class);
        this.startActivity(intent);
    }
}
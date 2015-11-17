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

    public void likeBeef(View view) {
        DBTools db = DBTools.getInstance(this);
        db.incrementBaseRating(0, "beef", 1, DBTools.LIKE);
        System.out.println("One like for beef");

    }

    public void dislikeBeef(View view) {
        DBTools db = DBTools.getInstance(this);
        db.incrementBaseRating(0, "beef", 1, DBTools.DISLIKE);
        System.out.println("One dislike for beef");
    }

    public void likeChicken(View view) {
        DBTools db = DBTools.getInstance(this);
        db.incrementBaseRating(0, "chicken", 1, DBTools.LIKE);
        System.out.println("One like for chicken");
    }

    public void dislikeChicken(View view) {
        DBTools db = DBTools.getInstance(this);
        db.incrementBaseRating(0, "chicken", 1, DBTools.DISLIKE);
        System.out.println("One dislike for chicken");
    }

    public void likeTurkey(View view) {
        DBTools db = DBTools.getInstance(this);
        db.incrementBaseRating(0, "turkey", 1, DBTools.LIKE);
        System.out.println("One like for turkey");
    }

    public void dislikeTurkey(View view) {
        DBTools db = DBTools.getInstance(this);
        db.incrementBaseRating(0, "turkey", 1, DBTools.DISLIKE);
        System.out.println("One dislike for turkey");
    }

    public void likeEgg(View view) {
        DBTools db = DBTools.getInstance(this);
        db.incrementCuisineRating(0, "egg", 1, DBTools.LIKE);
        System.out.println("one like for egg");
    }

    public void dislikeEgg(View view) {
        DBTools db = DBTools.getInstance(this);
        db.incrementBaseRating(0, "egg", 1, DBTools.DISLIKE);
        System.out.println("one dilike for egg");
    }
}

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

    public void updateUserRatings(View view) {
        DBTools db = DBTools.getInstance(this);

        int userId = 1;

        db.incrementCuisineRating(userId, "pizza and calzones", 2, DBTools.DISLIKE);
        db.incrementCuisineRating(userId, "pasta", 2, DBTools.LIKE);
        db.incrementCuisineRating(userId, "pasta salads", 1, DBTools.LIKE);

        db.incrementBaseRating(userId, "chicken", 2, DBTools.DISLIKE);
        db.incrementBaseRating(userId, "tomato", 5, DBTools.LIKE);
        db.incrementBaseRating(userId, "pasta", 3, DBTools.LIKE);
    }

    public void likeTomato(View view) {
        DBTools db = DBTools.getInstance(this);
        db.incrementBaseRating(1, "tomatoes", 1, DBTools.LIKE);
        System.out.println("One like for tomato");

    }

    public void dislikeTomato(View view) {
        DBTools db = DBTools.getInstance(this);
        db.incrementBaseRating(1, "tomatoes", 1, DBTools.DISLIKE);
        System.out.println("One dislike for tomato");
    }

    public void likeChicken(View view) {
        DBTools db = DBTools.getInstance(this);
        db.incrementBaseRating(1, "chicken", 1, DBTools.LIKE);
        System.out.println("One like for chicken");

    }

    public void dislikeChicken(View view) {
        DBTools db = DBTools.getInstance(this);
        db.incrementBaseRating(1, "chicken", 1, DBTools.DISLIKE);
        System.out.println("One dislike for chicken");
    }

    public void likeSandwich(View view) {
        DBTools db = DBTools.getInstance(this);
        db.incrementBaseRating(1, "sandwiches and wraps", 1, DBTools.LIKE);
        System.out.println("One like for sandwiches and wraps");

    }

    public void dislikeSandwich(View view) {
        DBTools db = DBTools.getInstance(this);
        db.incrementBaseRating(1, "sandwiches and wraps", 1, DBTools.DISLIKE);
        System.out.println("One dislike for sandwiches and wraps");
    }

    public void likePasta(View view) {
        DBTools db = DBTools.getInstance(this);
        db.incrementBaseRating(1, "pasta", 1, DBTools.LIKE);
        db.incrementCuisineRating(1, "pasta salads", 1, DBTools.LIKE);
        System.out.println("two likes for chicken");

    }

    public void dislikePasta(View view) {
        DBTools db = DBTools.getInstance(this);
        db.incrementBaseRating(1, "pasta", 1, DBTools.DISLIKE);
        db.incrementCuisineRating(1, "pasta salads", 1, DBTools.DISLIKE);
        System.out.println("two likes for chicken");
    }
}

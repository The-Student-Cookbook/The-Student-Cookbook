package cs506.studentcookbook.Database;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import cs506.studentcookbook.Model.APIGrabber;
import cs506.studentcookbook.Model.Ingredient;
import cs506.studentcookbook.Model.Recipe;
import cs506.studentcookbook.R;

public class DatabaseTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);
        showIngredients();
    }

    public void onButtonClick(View view) {
        EditText text = (EditText)findViewById(R.id.ingredientText);
        String name = text.getText().toString();
        double cost = Math.random() * 20;

        if(name != null && name.length() > 0) {
            DatabaseCreator db = new DatabaseCreator(this);
            db.addIngredient(name, cost);
        }

        showIngredients();
        text.setText("");

        addARecipe();
    }

    public void showIngredients() {
        DatabaseCreator db = new DatabaseCreator(this);

        for(String s : db.getIngredients()) {
            Log.d("Ingredient", s);
        }
    }

    public void addARecipe() {
        final DatabaseCreator db = new DatabaseCreator(this);

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    //List<Recipe> recipes = APIGrabber.getRecipes("pasta");
                    //for(Recipe recipe : recipes) {
                    //    db.addRecipeToDatabase(recipe);
                    //    Log.d("recipe", recipe.toString());
                    //}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}

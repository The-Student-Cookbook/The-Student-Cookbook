package cs506.studentcookbook;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.util.ArrayList;
import java.util.List;

import cs506.studentcookbook.database.APIGrabber;
import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.Ingredient;
import cs506.studentcookbook.model.Preferences;
import cs506.studentcookbook.model.Recipe;

public class AISearchTest extends AndroidTestCase {
    private DBTools db;
    private RenamingDelegatingContext context;

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testBasic() {
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBTools(context);
        db.createTables();

        //test population
        Recipe r = new Recipe();
        r.setId(1);
        r.setInstructions("");
        r.setName("recipe1");
        r.addCuisine("cuisine1");
        r.addBase("base1");
        db.addRecipeToDatabase(r);

        r = new Recipe();
        r.setId(2);
        r.setInstructions("");
        r.setName("recipe2");
        r.addCuisine("cuisine2");
        r.addBase("base2");
        db.addRecipeToDatabase(r);

        r = new Recipe();
        r.setId(3);
        r.setInstructions("");
        r.setName("recipe3");
        r.addCuisine("cuisine1");
        r.addBase("base2");
        db.addRecipeToDatabase(r);

        r = new Recipe();
        r.setId(3);
        r.setInstructions("");
        r.setName("recipe4");
        r.addCuisine("cuisine1");
        r.addBase("base3");
        db.addRecipeToDatabase(r);

        int userId = 1;

        List<Recipe> list = db.getSuggestedRecipes(null);
        assertTrue(list.size() > 0);

        assertTrue(db.recipeIsSuggested(1, userId));
        assertTrue(db.recipeIsSuggested(2, userId));
        assertTrue(db.recipeIsSuggested(3, userId));
        assertTrue(db.recipeIsSuggested(4, userId));

        db.incrementBaseRating(userId, "base2", 1, DBTools.DISLIKE);
        assertFalse(db.recipeIsSuggested(2, userId));
        assertFalse(db.recipeIsSuggested(3, userId));

        db.incrementBaseRating(userId, "base1", 1, DBTools.LIKE);
        assertTrue(db.recipeIsSuggested(1, userId));

        db.incrementCuisineRating(userId, "cuisine1", 1, DBTools.LIKE);
        assertTrue(db.recipeIsSuggested(1, userId));
        assertTrue(db.recipeIsSuggested(3, userId));

        db.incrementBaseRating(userId, "base3", 1, DBTools.DISLIKE);
        assertTrue(db.recipeIsSuggested(3, userId));

        // this changes the classification for recipe 2
        db.incrementCuisineRating(userId, "cuisine2", 2, DBTools.LIKE);
        assertTrue(db.recipeIsSuggested(2, userId));

        // all recipes pass
        assertTrue(db.recipeIsSuggested(1, userId));
        assertTrue(db.recipeIsSuggested(2, userId));
        assertTrue(db.recipeIsSuggested(3, userId));
        assertTrue(db.recipeIsSuggested(4, userId));
    }

    public void testListFunction() {
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBTools(context);
        db.createTables();

        int userId = 1;

        //test population
        Recipe r = new Recipe();
        r.setId(1);
        r.setInstructions("");
        r.setName("recipe1");
        r.addCuisine("cuisine1");
        r.addBase("base1");
        db.addRecipeToDatabase(r);
        // cuisine1
        // base1
        // dislike
        // dislike

        r = new Recipe();
        r.setId(2);
        r.setInstructions("");
        r.setName("recipe2");
        r.addCuisine("cuisine2");
        r.addBase("base2");
        db.addRecipeToDatabase(r);
        // cuisine2
        // base2
        // like
        // like
        // like

        r = new Recipe();
        r.setId(3);
        r.setInstructions("");
        r.setName("recipe3");
        r.addCuisine("cuisine1");
        r.addBase("base2");
        r.addBase("base4");
        db.addRecipeToDatabase(r);
        // cuisine1
        // base2
        // like
        // dislike
        // like
        // dislike
        // dislike

        r = new Recipe();
        r.setId(3);
        r.setInstructions("");
        r.setName("recipe4");
        r.addCuisine("cuisine3");
        r.addBase("base3");
        db.addRecipeToDatabase(r);
        // cuisine3
        // base3
        // like
        // like

        db.incrementBaseRating(userId, "base3", 1, DBTools.LIKE);
        db.incrementBaseRating(userId, "base1", 1, DBTools.DISLIKE);
        db.incrementBaseRating(userId, "base2", 1, DBTools.LIKE);
        db.incrementBaseRating(userId, "base2", 1, DBTools.LIKE);
        db.incrementBaseRating(userId, "base4", 1, DBTools.DISLIKE);
        db.incrementBaseRating(userId, "base4", 1, DBTools.DISLIKE);

        db.incrementCuisineRating(userId, "cuisine1", 1, DBTools.DISLIKE);
        db.incrementCuisineRating(userId, "cuisine2", 1, DBTools.LIKE);
        db.incrementCuisineRating(userId, "cuisine3", 1, DBTools.LIKE);

        assertFalse(db.recipeIsSuggested(1, userId));
        assertFalse(db.recipeIsSuggested(3, userId));

        assertTrue(db.recipeIsSuggested(2, userId));
        assertTrue(db.recipeIsSuggested(4, userId));

        List<Recipe> suggested = db.performAISearch(userId);
        boolean recipe1 = false;
        boolean recipe2 = false;
        boolean recipe3 = false;
        boolean recipe4 = false;

        for(Recipe re : suggested) {
            if(re.getName().equals("recipe1")) {
                recipe1 = true;
            }
            if(re.getName().equals("recipe2")) {
                recipe2 = true;
            }
            if(re.getName().equals("recipe3")) {
                recipe3 = true;
            }
            if(re.getName().equals("recipe4")) {
                recipe4 = true;
            }
        }

        assertFalse(recipe1);
        assertFalse(recipe3);
        assertTrue(recipe2);
        assertTrue(recipe4);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
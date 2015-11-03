package cs506.studentcookbook;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.util.List;

import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.Ingredient;
import cs506.studentcookbook.model.Preferences;
import cs506.studentcookbook.model.Recipe;

public class DBToolsUnitTest extends AndroidTestCase {
    private DBTools db;
    private RenamingDelegatingContext context;

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testPopulate() {
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBTools(context);

        assertEquals(false, db.databaseIsPopulated());

        if(true)
            return;

        db.populateDatabase();

        // wait a while...
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }

        assertEquals(true, db.databaseIsPopulated());
    }

    public void testCreateRecipe() {
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBTools(context);

        Recipe r = new Recipe();
        String originalName = "TEST RECIPE";
        String name = "     " + originalName + "                ";
        r.setName(name);

        assertEquals(r.getName(), originalName);

        Ingredient i = new Ingredient("ingredient", "", 1.5);
        r.addIngredient(i);

        assertTrue(r.getIngredients().contains(i));

        String inst = "do the thing with the stuff";
        r.setInstructions("      " + inst + "        ");

        assertEquals(r.getInstructions(), inst);
    }

    public void testAddRecipe() {
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBTools(context);
        db.resetDatabase();

        String name = "TEST RECIPE";
        String instructions = "These are the instructions";

        Recipe r = new Recipe();
        r.setName(name);
        r.setInstructions(instructions);
        Preferences p = new Preferences();
        p.setName(name);
        List<Recipe> rs = db.getSuggestedRecipes(p);

        // empty at first
        assertEquals(rs.size(), 0);

        db.addRecipeToDatabase(r);
        rs = db.getSuggestedRecipes(p);

        // now contains one recipe
        assertEquals(rs.size(), 1);

        // that has the correct name
        assertEquals(rs.get(0).getName(), name);

        // and instructions
        assertEquals(rs.get(0).getInstructions(), instructions);

        p.setName(name.toLowerCase());
        rs = db.getSuggestedRecipes(p);

        // and we also find the recipe if it is lower case
        assertEquals(rs.size(), 1);
    }

    public void testGetSuggestedRecipes() {
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBTools(context);
        db.resetDatabase();

        String name = "recipe1";
        String instructions = "These are the instructions for " + name;
        Recipe r = new Recipe();
        r.setName(name);
        r.setInstructions(instructions);
        db.addRecipeToDatabase(r);
        List<Recipe> rs = db.getSuggestedRecipes(null);

        // now contains one recipe
        assertEquals(rs.size(), 1);

        rs = db.getSuggestedRecipes(new Preferences());

        // still contains one recipe
        assertEquals(rs.size(), 1);

        name = "recipe2";
        instructions = "These are the instructions for " + name;
        r = new Recipe();
        r.setName(name);
        r.setInstructions(instructions);
        db.addRecipeToDatabase(r);
        rs = db.getSuggestedRecipes(null);

        // now contains two recipes
        assertEquals(rs.size(), 2);

        rs = db.getSuggestedRecipes(new Preferences());

        // still contains two recipes
        assertEquals(rs.size(), 2);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
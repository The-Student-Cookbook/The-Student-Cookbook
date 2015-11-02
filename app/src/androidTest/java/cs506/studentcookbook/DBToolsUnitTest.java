package cs506.studentcookbook;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.Ingredient;
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

    public void testAddRecipe() {
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBTools(context);

        Recipe r = new Recipe();
        String name = "    name    ";
        r.setName(name);
        assertEquals(r.getName(), "name");

        Ingredient i = new Ingredient("ingredient", "", 1.5);
        r.addIngredient(i);
        assertTrue(r.getIngredients().contains(i));

        String inst = "do the thing with the stuff";
        r.setInstructions("      " + inst + "        ");
        assertEquals(r.getInstructions(), inst);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
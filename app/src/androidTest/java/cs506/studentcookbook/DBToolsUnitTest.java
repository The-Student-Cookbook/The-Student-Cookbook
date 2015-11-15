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

        // we have a daily limit on calling the online API, so we won't call this test very often...
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
        db.createTables();

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
        db.createTables();

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

        Preferences p = new Preferences();
        p.setName("");
        rs = db.getSuggestedRecipes(p);

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

    public void testCuisineQuery() {
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBTools(context);
        db.createTables();

        List<String> c = new ArrayList<>();
        c.add("chicken");
        c.add("toast");
        c.add("test");

        String test = db.prepareQueryLogic(c, "c.recipeId = r.recipeId", "c.cuisineName");
        String shouldBe = "(c.recipeId = r.recipeId AND (c.cuisineName LIKE '%chicken%' OR c.cuisineName LIKE '%toast%' OR c.cuisineName LIKE '%test%'))";

        // query is correct
        assertEquals(test, shouldBe);

        String name = "recipe1";
        String instructions = "These are the instructions for " + name;
        String cuisine = "chicken";
        Recipe r = new Recipe();
        r.setName(name);
        r.setInstructions(instructions);
        r.addCuisine(cuisine);

        db.addRecipeToDatabase(r);
        List<Recipe> rs = db.getSuggestedRecipes(null);

        // now contains one recipe
        assertEquals(rs.size(), 1);

        Preferences p = new Preferences();
        p.addLikedCuisine(cuisine);
        rs = db.getSuggestedRecipes(p);

        // still contains one recipe
        assertEquals(rs.size(), 1);

        name = "recipe2";
        instructions = "These are the instructions for " + name;
        String cuisine2 = "beef";
        String cuisine3 = "apple";
        r = new Recipe();
        r.setName(name);
        r.setInstructions(instructions);
        r.addCuisine(cuisine);
        r.addCuisine(cuisine2);
        r.addCuisine(cuisine3);
        db.addRecipeToDatabase(r);

        p = new Preferences();
        p.addLikedCuisine(cuisine2);
        rs = db.getSuggestedRecipes(p);

        // still contains one recipe
        assertEquals(rs.size(), 1);

        p = new Preferences();
        p.addLikedCuisine(cuisine2);
        p.addLikedCuisine(cuisine3);
        rs = db.getSuggestedRecipes(p);

        // still contains one recipe
        assertEquals(rs.size(), 1);

        p = new Preferences();
        p.addLikedCuisine(cuisine);
        p.addLikedCuisine(cuisine2);
        p.addLikedCuisine(cuisine3);
        rs = db.getSuggestedRecipes(p);

        // contains both
        assertEquals(rs.size(), 2);

        p = new Preferences();
        p.addLikedCuisine("orange");
        rs = db.getSuggestedRecipes(p);

        // empty
        assertEquals(rs.size(), 0);
    }

    public void testBaseAndCuisineTogether() {
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBTools(context);
        db.createTables();

        String name = "recipe1";
        String instructions = "These are the instructions for " + name;
        String cuisine = "chicken";
        Recipe r = new Recipe();
        r.setName(name);
        r.setInstructions(instructions);
        r.addCuisine(cuisine);
        db.addRecipeToDatabase(r);

        name = "recipe2";
        instructions = "These are the instructions for " + name;
        String base = "something else";
        r = new Recipe();
        r.setName(name);
        r.setInstructions(instructions);
        r.addBase(base);
        db.addRecipeToDatabase(r);

        List<Recipe> rs = db.getSuggestedRecipes(null);

        // now contains two recipes
        assertEquals(rs.size(), 2);

        Preferences p = new Preferences();
        p.addLikedCuisine(cuisine);
        rs = db.getSuggestedRecipes(p);

        // only the first one
        assertEquals(rs.size(), 1);

        p = new Preferences();
        p.addLikedBase(base);
        rs = db.getSuggestedRecipes(p);

        // only the second one
        assertEquals(rs.size(), 1);

        p = new Preferences();
        p.addLikedBase(base);
        p.addLikedCuisine(cuisine);
        rs = db.getSuggestedRecipes(p);

        // both
        assertEquals(rs.size(), 2);

        p = new Preferences();
        p.addLikedBase("lalalalalal");
        rs = db.getSuggestedRecipes(p);

        // nothing
        assertEquals(rs.size(), 0);

        p = new Preferences();
        p.addLikedCuisine("lalalalalal");
        rs = db.getSuggestedRecipes(p);

        // nothing
        assertEquals(rs.size(), 0);
        p = new Preferences();
        p.setName("");
        rs = db.getSuggestedRecipes(p);

        // still contains one recipe
        assertEquals(rs.size(), 2);
    }

    public void testGetAndSetLike() {
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBTools(context);
        db.createTables();

        int userId = 1337;
        String cuisine = "chicken";

        // like
        int likeCount = db.getCuisineLikeOrDislikeCount(userId, cuisine, DBTools.LIKE);
        assertEquals(0, likeCount);

        db.incrementCuisineRating(userId, cuisine, 10, DBTools.LIKE);
        likeCount = db.getCuisineLikeOrDislikeCount(userId, cuisine, DBTools.LIKE);
        assertEquals(10, likeCount);

        db.incrementCuisineRating(userId, cuisine, 2, DBTools.LIKE);
        likeCount = db.getCuisineLikeOrDislikeCount(userId, cuisine, DBTools.LIKE);
        assertEquals(12, likeCount);

        db.incrementCuisineRating(userId, cuisine, -6, DBTools.LIKE);
        likeCount = db.getCuisineLikeOrDislikeCount(userId, cuisine, DBTools.LIKE);
        assertEquals(6, likeCount);

        // dislike
        int dislikeCount = db.getCuisineLikeOrDislikeCount(userId, cuisine, DBTools.DISLIKE);
        assertEquals(0, dislikeCount);

        db.incrementCuisineRating(userId, cuisine, 3, DBTools.DISLIKE);
        dislikeCount = db.getCuisineLikeOrDislikeCount(userId, cuisine, DBTools.DISLIKE);
        assertEquals(3, dislikeCount);

        db.incrementCuisineRating(userId, cuisine, 2, DBTools.DISLIKE);
        dislikeCount = db.getCuisineLikeOrDislikeCount(userId, cuisine, DBTools.DISLIKE);
        assertEquals(5, dislikeCount);

        db.incrementCuisineRating(userId, cuisine, -1, DBTools.DISLIKE);
        dislikeCount = db.getCuisineLikeOrDislikeCount(userId, cuisine, DBTools.DISLIKE);
        assertEquals(4, dislikeCount);

        //meal base
        // like
        String base = cuisine;

        likeCount = db.getBaseLikeOrDislikeCount(userId, base, DBTools.LIKE);
        assertEquals(0, likeCount);

        db.incrementBaseRating(userId, base, 7, DBTools.LIKE);
        likeCount = db.getBaseLikeOrDislikeCount(userId, base, DBTools.LIKE);
        assertEquals(7, likeCount);

        db.incrementBaseRating(userId, base, 5, DBTools.LIKE);
        likeCount = db.getBaseLikeOrDislikeCount(userId, base, DBTools.LIKE);
        assertEquals(12, likeCount);

        // make sure the methods are actually updating different tables
        int baselikeCount = db.getBaseLikeOrDislikeCount(userId, base, DBTools.LIKE);
        int cuisinelikeCount = db.getCuisineLikeOrDislikeCount(userId, cuisine, DBTools.LIKE);
        assertFalse(baselikeCount == cuisinelikeCount);
    }

    public void testGetProbability() {
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBTools(context);
        db.createTables();

        int userId = 1337;
        double like = 0.0;
        double dislike = 0.0;

        assertEquals(0.0, db.getProbability(userId, DBTools.LIKE));
        assertEquals(0.0, db.getProbability(userId, DBTools.DISLIKE));

        // first only consider cuisines
        db.incrementCuisineRating(userId, "cuisine1", 10, DBTools.LIKE);
        like += 10.0;
        assertEquals(like / (dislike + like), db.getProbability(userId, DBTools.LIKE));
        assertEquals(dislike / (dislike + like), db.getProbability(userId, DBTools.DISLIKE));

        db.incrementCuisineRating(userId, "cuisine1", 10, DBTools.LIKE);
        like += 10.0;
        assertEquals(like / (dislike + like), db.getProbability(userId, DBTools.LIKE));
        assertEquals(dislike / (dislike + like), db.getProbability(userId, DBTools.DISLIKE));

        db.incrementCuisineRating(userId, "cuisine1", 5, DBTools.DISLIKE);
        dislike += 5.0;
        assertEquals(like / (dislike + like), db.getProbability(userId, DBTools.LIKE));
        assertEquals(dislike / (dislike + like), db.getProbability(userId, DBTools.DISLIKE));

        db.incrementCuisineRating(userId, "cuisine2", 5, DBTools.DISLIKE);
        dislike += 5.0;
        assertEquals(like / (dislike + like), db.getProbability(userId, DBTools.LIKE));
        assertEquals(dislike / (dislike + like), db.getProbability(userId, DBTools.DISLIKE));

        db.incrementCuisineRating(userId, "cuisine3", 5, DBTools.LIKE);
        like += 5.0;
        assertEquals(like / (dislike + like), db.getProbability(userId, DBTools.LIKE));
        assertEquals(dislike / (dislike + like), db.getProbability(userId, DBTools.DISLIKE));

        // now take bases into account
        db.incrementBaseRating(userId, "base1", 16, DBTools.LIKE);
        like += 16.0;
        assertEquals(like / (dislike + like), db.getProbability(userId, DBTools.LIKE));
        assertEquals(dislike / (dislike + like), db.getProbability(userId, DBTools.DISLIKE));

        db.incrementBaseRating(userId, "base1", 7, DBTools.DISLIKE);
        dislike += 7.0;
        assertEquals(like / (dislike + like), db.getProbability(userId, DBTools.LIKE));
        assertEquals(dislike / (dislike + like), db.getProbability(userId, DBTools.DISLIKE));

        db.incrementBaseRating(userId, "base2", 4, DBTools.DISLIKE);
        dislike += 4.0;
        assertEquals(like / (dislike + like), db.getProbability(userId, DBTools.LIKE));
        assertEquals(dislike / (dislike + like), db.getProbability(userId, DBTools.DISLIKE));

        db.incrementBaseRating(userId, "base3", 9, DBTools.LIKE);
        like += 9.0;
        assertEquals(like / (dislike + like), db.getProbability(userId, DBTools.LIKE));
        assertEquals(dislike / (dislike + like), db.getProbability(userId, DBTools.DISLIKE));

        assertEquals(db.getProbability(userId, DBTools.LIKE), 1.0 - db.getProbability(userId, DBTools.DISLIKE), 0.01);
    }

    public void testConditionalProb() {

    }

    public void testAPIHardcodedDescriptions() {
        assertEquals(APIGrabber.TOOLS_DESCRIPTION.length, APIGrabber.TOOLS_LIST.length);
        assertEquals(APIGrabber.TOOLS_URLS.length, APIGrabber.TOOLS_LIST.length);
        assertEquals(APIGrabber.TOOLS_URLS.length, APIGrabber.TOOLS_DESCRIPTION.length);

        assertEquals(APIGrabber.TECHNIQUES_DESCRIPTION.length, APIGrabber.TECHNIQUES_LIST.length);
        assertEquals(APIGrabber.TECHNIQUES_HELP_URLS.length, APIGrabber.TECHNIQUES_LIST.length);
        assertEquals(APIGrabber.TECHNIQUES_HELP_URLS.length, APIGrabber.TECHNIQUES_DESCRIPTION.length);
        assertEquals(APIGrabber.TECHNIQUES_HELP_URLS.length, APIGrabber.TECHNIQUES_IMAGE_URLS.length);
    }

        @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
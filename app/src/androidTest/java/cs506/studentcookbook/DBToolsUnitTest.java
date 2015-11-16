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
import cs506.studentcookbook.model.User;
import cs506.studentcookbook.model.Tool;
import cs506.studentcookbook.model.Technique;
import cs506.studentcookbook.model.GroceryList;

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

    public void testConditionalProbCuisine() {
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBTools(context);
        db.createTables();

        int userId = 1337;
        double likeTotal = 0.0;
        double dislikeTotal = 0.0;

        double likeC1 = 0.0;
        double dislikeC1 = 0.0;

        double treshold = 0.001;

        // add the cuisine 1
        db.incrementCuisineRating(userId, "cuisine1", 10, DBTools.LIKE);
        likeC1 += 10.0;
        likeTotal += 10.0;
        assertEquals(likeC1 / likeTotal, db.getConditionalProbabilityCuisine(userId, "cuisine1", DBTools.LIKE), treshold);

        db.incrementCuisineRating(userId, "cuisine1", 7, DBTools.DISLIKE);
        dislikeC1 += 7.0;
        dislikeTotal += 7.0;
        assertEquals(likeC1 / likeTotal, db.getConditionalProbabilityCuisine(userId, "cuisine1", DBTools.LIKE), treshold);
        assertEquals(dislikeC1 / dislikeTotal, db.getConditionalProbabilityCuisine(userId, "cuisine1", DBTools.DISLIKE), treshold);

        // add cuisine 2
        db.incrementCuisineRating(userId, "cuisine2", 4, DBTools.LIKE);
        likeTotal += 4.0;
        assertEquals(likeC1 / likeTotal, db.getConditionalProbabilityCuisine(userId, "cuisine1", DBTools.LIKE), treshold);

        db.incrementCuisineRating(userId, "cuisine2", 4, DBTools.LIKE);
        likeTotal += 4.0;
        assertEquals(likeC1 / likeTotal, db.getConditionalProbabilityCuisine(userId, "cuisine1", DBTools.LIKE), treshold);

        db.incrementCuisineRating(userId, "cuisine2", 2, DBTools.LIKE);
        likeTotal += 2.0;
        assertEquals(likeC1 / likeTotal, db.getConditionalProbabilityCuisine(userId, "cuisine1", DBTools.LIKE), treshold);

        db.incrementCuisineRating(userId, "cuisine2", 1, DBTools.LIKE);
        likeTotal += 1.0;
        assertEquals(likeC1 / likeTotal, db.getConditionalProbabilityCuisine(userId, "cuisine1", DBTools.LIKE), treshold);

        // add cuisine 3
        db.incrementCuisineRating(userId, "cuisine3", 5, DBTools.LIKE);
        likeTotal += 5.0;
        assertEquals(likeC1 / likeTotal, db.getConditionalProbabilityCuisine(userId, "cuisine1", DBTools.LIKE), treshold);

        db.incrementCuisineRating(userId, "cuisine3", 6, DBTools.LIKE);
        likeTotal += 6.0;
        assertEquals(likeC1 / likeTotal, db.getConditionalProbabilityCuisine(userId, "cuisine1", DBTools.LIKE), treshold);

        // go back to cuisine 1
        db.incrementCuisineRating(userId, "cuisine1", 3, DBTools.LIKE);
        likeC1 += 3.0;
        likeTotal += 3.0;
        assertEquals(likeC1 / likeTotal, db.getConditionalProbabilityCuisine(userId, "cuisine1", DBTools.LIKE), treshold);

        // add in some dislikes
        db.incrementCuisineRating(userId, "cuisine3", 6, DBTools.DISLIKE);
        dislikeTotal += 6.0;
        assertEquals(dislikeC1 / dislikeTotal, db.getConditionalProbabilityCuisine(userId, "cuisine1", DBTools.DISLIKE), treshold);

        db.incrementCuisineRating(userId, "cuisine3", 2, DBTools.DISLIKE);
        dislikeTotal += 2.0;
        assertEquals(dislikeC1 / dislikeTotal, db.getConditionalProbabilityCuisine(userId, "cuisine1", DBTools.DISLIKE), treshold);

        db.incrementCuisineRating(userId, "cuisine2", 1, DBTools.DISLIKE);
        dislikeTotal += 1.0;
        assertEquals(dislikeC1 / dislikeTotal, db.getConditionalProbabilityCuisine(userId, "cuisine1", DBTools.DISLIKE), treshold);

        // go back to cuisine 1 again
        db.incrementCuisineRating(userId, "cuisine1", 1, DBTools.DISLIKE);
        dislikeC1 += 1.0;
        dislikeTotal += 1.0;
        assertEquals(likeC1 / likeTotal, db.getConditionalProbabilityCuisine(userId, "cuisine1", DBTools.LIKE), treshold);
        assertEquals(dislikeC1 / dislikeTotal, db.getConditionalProbabilityCuisine(userId, "cuisine1", DBTools.DISLIKE), treshold);
    }

    public void testConditionalProbBase() {
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBTools(context);
        db.createTables();

        int userId = 1337;
        double likeTotal = 0.0;
        double dislikeTotal = 0.0;

        double likeB1 = 0.0;
        double dislikeB1 = 0.0;

        double treshold = 0.001;

        // add the cuisine 1
        db.incrementBaseRating(userId, "base1", 10, DBTools.LIKE);
        likeB1 += 10.0;
        likeTotal += 10.0;
        assertEquals(likeB1 / likeTotal, db.getConditionalProbabilityBase(userId, "base1", DBTools.LIKE), treshold);

        db.incrementBaseRating(userId, "base1", 7, DBTools.DISLIKE);
        dislikeB1 += 7.0;
        dislikeTotal += 7.0;
        assertEquals(likeB1 / likeTotal, db.getConditionalProbabilityBase(userId, "base1", DBTools.LIKE), treshold);
        assertEquals(dislikeB1 / dislikeTotal, db.getConditionalProbabilityBase(userId, "base1", DBTools.DISLIKE), treshold);

        // add base 2
        db.incrementBaseRating(userId, "base2", 4, DBTools.LIKE);
        likeTotal += 4.0;
        assertEquals(likeB1 / likeTotal, db.getConditionalProbabilityBase(userId, "base1", DBTools.LIKE), treshold);

        db.incrementBaseRating(userId, "base2", 4, DBTools.LIKE);
        likeTotal += 4.0;
        assertEquals(likeB1 / likeTotal, db.getConditionalProbabilityBase(userId, "base1", DBTools.LIKE), treshold);

        db.incrementBaseRating(userId, "base2", 2, DBTools.LIKE);
        likeTotal += 2.0;
        assertEquals(likeB1 / likeTotal, db.getConditionalProbabilityBase(userId, "base1", DBTools.LIKE), treshold);

        db.incrementBaseRating(userId, "base2", 1, DBTools.LIKE);
        likeTotal += 1.0;
        assertEquals(likeB1 / likeTotal, db.getConditionalProbabilityBase(userId, "base1", DBTools.LIKE), treshold);

        // add base 3
        db.incrementBaseRating(userId, "base3", 5, DBTools.LIKE);
        likeTotal += 5.0;
        assertEquals(likeB1 / likeTotal, db.getConditionalProbabilityBase(userId, "base1", DBTools.LIKE), treshold);

        db.incrementBaseRating(userId, "base3", 6, DBTools.LIKE);
        likeTotal += 6.0;
        assertEquals(likeB1 / likeTotal, db.getConditionalProbabilityBase(userId, "base1", DBTools.LIKE), treshold);

        // go back to base 1
        db.incrementBaseRating(userId, "base1", 3, DBTools.LIKE);
        likeB1 += 3.0;
        likeTotal += 3.0;
        assertEquals(likeB1 / likeTotal, db.getConditionalProbabilityBase(userId, "base1", DBTools.LIKE), treshold);

        // add in some dislikes
        db.incrementBaseRating(userId, "base3", 6, DBTools.DISLIKE);
        dislikeTotal += 6.0;
        assertEquals(dislikeB1 / dislikeTotal, db.getConditionalProbabilityBase(userId, "base1", DBTools.DISLIKE), treshold);

        db.incrementBaseRating(userId, "base3", 2, DBTools.DISLIKE);
        dislikeTotal += 2.0;
        assertEquals(dislikeB1 / dislikeTotal, db.getConditionalProbabilityBase(userId, "base1", DBTools.DISLIKE), treshold);

        db.incrementBaseRating(userId, "base2", 1, DBTools.DISLIKE);
        dislikeTotal += 1.0;
        assertEquals(dislikeB1 / dislikeTotal, db.getConditionalProbabilityBase(userId, "base1", DBTools.DISLIKE), treshold);

        // go back to base 1 again
        db.incrementBaseRating(userId, "base1", 1, DBTools.DISLIKE);
        dislikeB1 += 1.0;
        dislikeTotal += 1.0;
        assertEquals(likeB1 / likeTotal, db.getConditionalProbabilityBase(userId, "base1", DBTools.LIKE), treshold);
        assertEquals(dislikeB1 / dislikeTotal, db.getConditionalProbabilityBase(userId, "base1", DBTools.DISLIKE), treshold);

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

    public void testGetCuisinesAndBases() {
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBTools(context);
        db.createTables();

        Recipe r = new Recipe();
        r.setInstructions("");
        r.setName("recipe1");
        r.setId(1);
        r.addCuisine("cuisine1");
        r.addCuisine("cuisine2");
        r.addCuisine("cuisine3");
        r.addBase("base1");
        db.addRecipeToDatabase(r);

        List<String> list = db.getCuisines(1);
        assertTrue(list.contains("cuisine1"));
        assertTrue(list.contains("cuisine2"));
        assertTrue(list.contains("cuisine3"));

        list = db.getBases(1);
        assertTrue(list.contains("base1"));

        r = new Recipe();
        r.setInstructions("");
        r.setName("recipe2");
        r.setId(2);
        r.addCuisine("cuisine3");
        r.addCuisine("cuisine4");
        r.addBase("base1");
        r.addBase("base2");
        db.addRecipeToDatabase(r);

        list = db.getCuisines(2);
        assertTrue(list.contains("cuisine3"));
        assertTrue(list.contains("cuisine4"));

        list = db.getBases(2);
        assertTrue(list.contains("base1"));
        assertTrue(list.contains("base2"));

        list = db.getCuisines(3);
        assertEquals(0, list.size());

        list = db.getBases(3);
        assertEquals(0, list.size());
    }

    public void testGetandSetPreferences(){
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBTools(context);
        db.createTables();
        populateEmptyTables(db);

        //Test parameters
        int groupSize = 4;
        int cookTime = 10;
        int prepTime = 25;

        //Make sure test database has some values for us to work with


        //Build objects to test methods with
        Preferences preferences1 = new Preferences();
        Preferences preferences2;

        List<String> likedCuisines = new ArrayList<String>();
        List<String> dislikedCuisines = new ArrayList<String>();
        List<String> likedBases = new ArrayList<String>();
        List<String> dislikedBases = new ArrayList<String>();

        likedCuisines.add("cuisine2");
        dislikedCuisines.add("cuisine1");
        likedBases.add("base2");
        dislikedBases.add("base1");

        preferences1.setGroupSize(groupSize);
        preferences1.setCookTime(cookTime);
        preferences1.setPrepTime(prepTime);
        preferences1.setLikedCuisines(likedCuisines);
        preferences1.setDislikedCuisines(dislikedCuisines);
        preferences1.setLikedBases(likedBases);
        preferences1.setDislikedBases(dislikedBases);

        //Potential issue: database has not filled in values for table User. To ensure
        //it is filled in so we can test normally, insert values into User.
        //(Preferences information is stored in table User, simply running this method once
        //either inputs in value or
        db.setUserSetting("userId", 0);


        //Try setting
        db.setPreferences(preferences1);

        //Try getting
        preferences2 = db.getPreferences();

        //Check results are equal ("You get what you set" --Yoda)
        assertEquals(preferences1.equals(preferences2),true);
    }

    public void testAllergicBases() {
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBTools(context);
        db.createTables();

        //Test parameters
        List<String> allergicBases1 = new ArrayList<String>();
        List<String> allergicBases2;
        allergicBases1.add("Peanut");
        allergicBases1.add("Soy");

        //Test add
        db.addAllergicBase("Peanut");
        db.addAllergicBase("Soy");

        //Test get
        allergicBases2 = db.getAllergicBases();
        assertEquals(allergicBases1, allergicBases2);

        //Test remove
        allergicBases1.remove("Soy");
        db.removeAllergicBase("Soy");
        assertEquals(allergicBases1, allergicBases2);
    }

    public void testGroceryList() {
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBTools(context);
        db.resetDatabase();
        db.createTables();
        populateEmptyTables(db);

        //Test parameters
        //Ingredient objects
        Ingredient ingredient1 = new Ingredient();
        Ingredient ingredient2 = new Ingredient();
        Ingredient ingredient3 = new Ingredient();
        ingredient1.setName("ingredient2-1");
        ingredient2.setName("ingredient2-2");
        ingredient3.setName("ingredient2-3");

        //Grocery list comparison objects
        GroceryList g1 = new GroceryList();
        GroceryList g2 = new GroceryList();

        //Will use this to compare to our set and gets
        g1.addIngredient(ingredient1);
        g1.addIngredient(ingredient2);

        //Add and remove from database
        //Don't need to add these because they are added already from populateEmptyTables helper method
        //db.addIngredientToGroceryList(ingredient1);
        //db.addIngredientToGroceryList(ingredient2);
        db.addIngredientToGroceryList(ingredient3);
        db.removeIngredientFromGroceryList(ingredient3);

        //Check if the return of get is this identical grocery list
        g2 = db.getGroceryList();
        assertTrue(g1.equals(g2));
    }

    public void testGetAndSetUserSettings() {
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBTools(context);
        db.resetDatabase();
        db.createTables();
        populateEmptyTables(db);

        //Test parameters
        //Tools for User
        List<Tool> tools1 = new ArrayList<Tool>();

        Tool tool11 = new Tool();
        tool11.setImageURL("tool11:url");
        tool11.setName("tool11");
        tool11.setDescription("tool11:description");

        Tool tool12 = new Tool();
        tool12.setImageURL("tool12:url");
        tool12.setName("tool12");
        tool12.setDescription("tool12:description");

        tools1.add(tool11);
        tools1.add(tool12);

        //User object, compare with values set in DB from populate method
        User user1 = new User();
        user1.setId(0);
        user1.setEmail("user:email");
        user1.setCountRecipesDisliked(1);
        user1.setCountRecipesLiked(1);
        user1.setGroupSize(1);
        user1.setPrepTime(5);
        user1.setCookTime(20);
        user1.setEstimateCost(3);
        user1.addAllergicBase("base1");
        user1.setTools(tools1);

        //Compare val to DB vals (they call the setter methods in populateEmptyTables(), so
        //everything is tested
        User user2 = db.getUserSettings();

        boolean bool = user1.equals(user2);

        assertTrue(user1.equals(user2));

        //Test setUserSetting(string int), setUserSetting(string, string), and getUserSetting(string)
        db.setUserSetting("userId", 25);
        db.setUserSetting("email", "email.net");

        int getId = Integer.parseInt(db.getUserSetting("userId"));
        String getEmail = db.getUserSetting("email");

        assertEquals(25, getId);
        assertEquals("email.net",getEmail);
    }

    public void testPinnedRecipes() {
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBTools(context);
        db.resetDatabase();
        db.createTables();
        populateEmptyTables(db);

        //Test parameters
        List<String> bases1 = new ArrayList<String>();
        bases1.add("base1");

        List<String> cuisines1 = new ArrayList<String>();
        cuisines1.add("cuisine1");

        List<Ingredient> ingredients1 = new ArrayList<Ingredient>();

        Ingredient ingredient11 = new Ingredient();
        ingredient11.setName("ingredient1-1");
        ingredient11.setAmount(1);
        ingredient11.setUnit("ingredient1-1:unit");

        Ingredient ingredient12 = new Ingredient();
        ingredient12.setName("ingredient1-2");
        ingredient12.setAmount(2);
        ingredient12.setUnit("ingredient1-2:unit");

        ingredients1.add(ingredient11);
        ingredients1.add(ingredient12);

        List<Tool> tools1 = new ArrayList<Tool>();

        Tool tool11 = new Tool();
        tool11.setImageURL("tool11:url");
        tool11.setName("tool11");
        tool11.setDescription("tool11:description");

        Tool tool12 = new Tool();
        tool12.setImageURL("tool12:url");
        tool12.setName("tool12");
        tool12.setDescription("tool12:description");

        tools1.add(tool11);
        tools1.add(tool12);

        List<Technique> techniques1 = new ArrayList<Technique>();

        List<String> urls = new ArrayList<String>();
        urls.add("url1");
        urls.add("url2");

        Technique technique11 = new Technique();
        technique11.setDescription("technique11:description");
        technique11.setName("technique11");
        technique11.setImageURL("technique11:imageURL");
        technique11.setExternalURLs(urls);
        technique11.setTools(tools1);

        Recipe recipe1 = new Recipe();
        recipe1.setId(1); //This would be 0, but Recipe table has autoincrement quality
        recipe1.setBigOvenId(0);
        recipe1.setName("recipe1");
        recipe1.setBases(bases1);
        recipe1.setCuisines(cuisines1);
        recipe1.setInstructions("recipe1:instructions");
        recipe1.setImageURL("recipe1:url");
        recipe1.setIngredients(ingredients1);
        recipe1.setTechniques(techniques1);
        recipe1.setTools(tools1);
        recipe1.setPrepTime(10);
        recipe1.setCookTime(30);
        recipe1.setCost(5);
        recipe1.setRating(4);
        recipe1.setIsASide(true);

        List<Recipe> recipeList1 = new ArrayList<Recipe>();
        List<Recipe> recipeList2 = db.getPinnedRecipes();

        recipeList1.add(recipe1);

        //Should be equal to value already in db from populateEmptyTables helper method
        assertEquals(recipeList1, recipeList2);

        db.removeRecipeFromPinned(recipe1);
        db.addRecipeToPinned(recipe1);

        assertEquals(recipeList1, recipeList2);

    }

    public void testRateRecipes() {
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBTools(context);
        db.resetDatabase();
        db.createTables();
        populateEmptyTables(db);

        //Test parameters
        List<String> bases1 = new ArrayList<String>();
        bases1.add("base1");

        List<String> cuisines1 = new ArrayList<String>();
        cuisines1.add("cuisine1");

        List<Ingredient> ingredients1 = new ArrayList<Ingredient>();

        Ingredient ingredient11 = new Ingredient();
        ingredient11.setName("ingredient1-1");
        ingredient11.setAmount(1);
        ingredient11.setUnit("ingredient1-1:unit");

        Ingredient ingredient12 = new Ingredient();
        ingredient12.setName("ingredient1-2");
        ingredient12.setAmount(2);
        ingredient12.setUnit("ingredient1-2:unit");

        ingredients1.add(ingredient11);
        ingredients1.add(ingredient12);

        List<Tool> tools1 = new ArrayList<Tool>();

        Tool tool11 = new Tool();
        tool11.setImageURL("tool11:url");
        tool11.setName("tool11");
        tool11.setDescription("tool11:description");

        Tool tool12 = new Tool();
        tool12.setImageURL("tool12:url");
        tool12.setName("tool12");
        tool12.setDescription("tool12:description");

        tools1.add(tool11);
        tools1.add(tool12);

        List<Technique> techniques1 = new ArrayList<Technique>();

        List<String> urls = new ArrayList<String>();
        urls.add("url1");
        urls.add("url2");

        Technique technique11 = new Technique();
        technique11.setDescription("technique11:description");
        technique11.setName("technique11");
        technique11.setImageURL("technique11:imageURL");
        technique11.setExternalURLs(urls);
        technique11.setTools(tools1);

        Recipe recipe1 = new Recipe();
        recipe1.setId(1); //This would be 0, but Recipe table has autoincrement quality
        recipe1.setBigOvenId(0);
        recipe1.setName("recipe1");
        recipe1.setBases(bases1);
        recipe1.setCuisines(cuisines1);
        recipe1.setInstructions("recipe1:instructions");
        recipe1.setImageURL("recipe1:url");
        recipe1.setIngredients(ingredients1);
        recipe1.setTechniques(techniques1);
        recipe1.setTools(tools1);
        recipe1.setPrepTime(10);
        recipe1.setCookTime(30);
        recipe1.setCost(5);
        recipe1.setRating(4);
        recipe1.setIsASide(true);

        //Check add recipe rating true
        //Already set in populateEmptyTables helper method
        //db.addRecipeRating(recipe1, true);
        assertTrue(db.getRecipeLiked(recipe1));
        db.removeRecipeRating(recipe1);

        //Check add recipe rating false
        db.addRecipeRating(recipe1, false);
        assertFalse(db.getRecipeLiked(recipe1));
        db.removeRecipeRating(recipe1);

        //Check clear()
        db.addRecipeRating(recipe1,true);
        db.clearRecipeRatings();
        assertFalse(db.getRecipeLiked(recipe1));
    }

    //This is a hack-y solution to filling in some empty tables for testing purposes/
    private void populateEmptyTables(DBTools db) {
        //Bases
        List<String> bases1 = new ArrayList<String>();
        bases1.add("base1");
        List<String> bases2 = new ArrayList<String>();
        bases2.add("base2");


        db.addMealBaseToDatabase("base3");
        db.addMealBaseToDatabase("base4");

        //Cuisines
        List<String> cuisines1 = new ArrayList<String>();
        cuisines1.add("cuisine1");

        List<String> cuisines2 = new ArrayList<String>();
        cuisines2.add("cuisine2");

        db.addCuisineToDatabase("cuisine3");
        db.addCuisineToDatabase("cuisine4");

        //Ingredients
        List<Ingredient> ingredients1 = new ArrayList<Ingredient>();
        List<Ingredient> ingredients2 = new ArrayList<Ingredient>();

        Ingredient ingredient11 = new Ingredient();
        ingredient11.setName("ingredient1-1");
        ingredient11.setAmount(1);
        ingredient11.setUnit("ingredient1-1:unit");

        Ingredient ingredient12 = new Ingredient();
        ingredient12.setName("ingredient1-2");
        ingredient12.setAmount(2);
        ingredient12.setUnit("ingredient1-2:unit");

        ingredients1.add(ingredient11);
        ingredients1.add(ingredient12);

        Ingredient ingredient21 = new Ingredient();
        ingredient11.setName("ingredient2-1");
        ingredient11.setAmount(1);
        ingredient11.setUnit("ingredient2-1:unit");

        Ingredient ingredient22 = new Ingredient();
        ingredient12.setName("ingredient2-2");
        ingredient12.setAmount(2);
        ingredient12.setUnit("ingredient2-2:unit");

        ingredients2.add(ingredient21);
        ingredients2.add(ingredient22);

        //Tools
        List<Tool> tools1 = new ArrayList<Tool>();
        List<Tool> tools2 = new ArrayList<Tool>();

        Tool tool11 = new Tool();
        tool11.setImageURL("tool11:url");
        tool11.setName("tool11");
        tool11.setDescription("tool11:description");

        Tool tool12 = new Tool();
        tool12.setImageURL("tool12:url");
        tool12.setName("tool12");
        tool12.setDescription("tool12:description");

        tools1.add(tool11);
        tools1.add(tool12);

        Tool tool21 = new Tool();
        tool21.setImageURL("tool21:url");
        tool21.setName("tool21");
        tool21.setDescription("tool21:description");

        Tool tool22 = new Tool();
        tool22.setImageURL("tool22:url");
        tool22.setName("tool22");
        tool22.setDescription("tool22:description");

        tools2.add(tool21);
        tools2.add(tool22);

        //Techniques
        List<Technique> techniques1 = new ArrayList<Technique>();
        List<Technique> techniques2 = new ArrayList<Technique>();

        List<String> urls = new ArrayList<String>();
        urls.add("url1");
        urls.add("url2");

        Technique technique11 = new Technique();
        technique11.setDescription("technique11:description");
        technique11.setName("technique11");
        technique11.setImageURL("technique11:imageURL");
        technique11.setExternalURLs(urls);
        technique11.setTools(tools1);

        Technique technique21 = new Technique();
        technique21.setDescription("technique21:description");
        technique21.setName("technique21");
        technique21.setImageURL("technique21:imageURL");
        technique21.setExternalURLs(urls);
        technique21.setTools(tools2);

        //Recipes
        Recipe recipe1 = new Recipe();
        Recipe recipe2 = new Recipe();

        recipe1.setId(0);
        recipe1.setBigOvenId(0);
        recipe1.setName("recipe1");
        recipe1.setBases(bases1);
        recipe1.setCuisines(cuisines1);
        recipe1.setInstructions("recipe1:instructions");
        recipe1.setImageURL("recipe1:url");
        recipe1.setIngredients(ingredients1);
        recipe1.setTechniques(techniques1);
        recipe1.setTools(tools1);
        recipe1.setPrepTime(10);
        recipe1.setCookTime(30);
        recipe1.setCost(5);
        recipe1.setRating(4);
        recipe1.setIsASide(true);

        recipe2.setId(1);
        recipe2.setBigOvenId(1);
        recipe2.setName("recipe2");
        recipe2.setBases(bases2);
        recipe2.setCuisines(cuisines2);
        recipe2.setInstructions("recipe2:instructions");
        recipe2.setImageURL("recipe2:url");
        recipe2.setIngredients(ingredients2);
        recipe2.setTechniques(techniques2);
        recipe2.setTools(tools2);
        recipe2.setPrepTime(20);
        recipe2.setCookTime(60);
        recipe2.setCost(10);
        recipe2.setRating(5);
        recipe2.setIsASide(false);

        //Recipe
        db.addRecipeToDatabase(recipe1);
        db.addRecipeToDatabase(recipe2);

        //Tools
        db.addToolToDatabaseDebug(tool11);
        db.addToolToDatabaseDebug(tool12);
        db.addToolToDatabaseDebug(tool21);
        db.addToolToDatabaseDebug(tool22);

        //Allergic_To
        //Can be set this way or done by updating user settings with user object
//        db.addAllergicBase("allergy1");
//        db.addAllergicBase("allergy2");

        //Cuisine_Type
        //Called on addRecipeToDatabase
        //db.addCuisineToDatabase("cuisine1");
        //db.addCuisineToDatabase("cuisine2");

        //Has_Cooked
        db.addHasCookedToDatabase(0, 0, "2000-10-10");
        db.addHasCookedToDatabase(0, 1, "2010-12-12");

        //Has_Cuisine_Type
        //Called on addRecipeToDatabase
        //db.addHasCuisineToDatabase(recipe1, cuisines1.get(0));
        //db.addHasCuisineToDatabase(recipe2, cuisines2.get(0));

        //Meal_Base
        //Called on addRecipeToDatabase
        //db.addMealBaseToDatabase("chicken");
        //db.addMealBaseToDatabase("beef");

        //Has_External_URL
        //Populated when techniques are added

        //Has_Meal_Base
        //Called on addRecipeToDatabase

        //Has_On_Grocery_List
        db.addIngredientToGroceryList(ingredient11);
        db.addIngredientToGroceryList(ingredient12);

        //Owns_Tool
        //Built when setting user prefs
        //db.addTool(tool11);
        //db.addTool(tool12);

        //Pinned_Recipe
        db.addRecipeToPinned(recipe1);

        //Rates_Cuisine
        db.incrementCuisineRating(0, cuisines1.get(0), 1, true);
        db.incrementCuisineRating(0, cuisines2.get(0), 1, false);

        //Rates_Meal_Base
        db.incrementBaseRating(0, bases1.get(0), 1, true);
        db.incrementBaseRating(0, bases2.get(0), 1, false);

        //Rates_Recipe
        db.addRecipeRating(recipe1, true);
        db.addRecipeRating(recipe2, false);

        //User Settings
        User user = new User();
        user.setId(0);
        user.setEmail("user:email");
        user.setCountRecipesDisliked(1);
        user.setCountRecipesLiked(1);
        user.setGroupSize(1);
        user.setPrepTime(5);
        user.setCookTime(20);
        user.setEstimateCost(3);
        user.addAllergicBase("base1");
        user.setTools(tools1);

        db.setUserSettings(user);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
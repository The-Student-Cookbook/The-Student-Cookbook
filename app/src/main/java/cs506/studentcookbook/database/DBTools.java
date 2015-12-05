package cs506.studentcookbook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cs506.studentcookbook.model.Tool;
import cs506.studentcookbook.model.Technique;
import cs506.studentcookbook.model.Recipe;
import cs506.studentcookbook.model.Ingredient;
import cs506.studentcookbook.model.Preferences;
import cs506.studentcookbook.model.User;
import cs506.studentcookbook.model.GroceryList;

/**
 * The DBTools class is a wrapper around the SQLite database. This class currently provides
 * functionality to create the database, delete the database, and insert into the database.
 *
 * This class calls out to APIGrabber to help populate the database.
 *
 * To populate the database with a test set of data, call populateDatabase(). All recipes added
 * into the database will be displayed on the console/Logcat.
 */
public class DBTools extends SQLiteOpenHelper {

    // Recipe tables
    public static final String TABLE_RECIPE = "Recipe";
    public static final String TABLE_TOOL = "Tool";
    public static final String TABLE_URL = "URL";
    public static final String TABLE_INGREDIENT = "Ingredient";
    public static final String TABLE_TECHNIQUE = "Technique";
    public static final String TABLE_MEAL_BASE = "Meal_Base";
    public static final String TABLE_CUISINE = "Cuisine_Type";
    public static final String TABLE_HAS_MEAL_BASE = "Has_Meal_Base";
    public static final String TABLE_HAS_CUISINE_TYPE = "Has_Cuisine_Type";
    public static final String TABLE_REQUIRES_INGREDIENT = "Requires_Ingredient";
    public static final String TABLE_REQUIRES_TOOL = "Requires_Tool";
    public static final String TABLE_REQUIRES_TECHNIQUE = "Requires_Technique";
    public static final String TABLE_HAS_EXTERNAL_URL = "Has_External_URL";

    // User tables
    public static final String TABLE_USER = "User";
    public static final String TABLE_ALLERGIC_TO = "Allergic_To";
    public static final String TABLE_OWNS_TOOL = "Owns_Tool";
    public static final String TABLE_PINNED_RECIPE = "Pinned_Recipe";
    public static final String TABLE_HAS_COOKED = "Has_Cooked";
    public static final String TABLE_HAS_ON_GROCERY_LIST = "Has_On_Grocery_List";

    // Suggestion Assistance Tables
    public static final String TABLE_RATES_RECIPE = "Rates_Recipe";
    public static final String TABLE_RATES_CUISINE = "Rates_Cuisine";
    public static final String TABLE_RATES_MEAL_BASE = "Rates_Meal_Base";

    public static final String[] ALL_TABLES = {TABLE_RECIPE, TABLE_TOOL, TABLE_URL, TABLE_INGREDIENT,
            TABLE_TECHNIQUE, TABLE_MEAL_BASE, TABLE_CUISINE, TABLE_HAS_MEAL_BASE, TABLE_HAS_CUISINE_TYPE,
            TABLE_REQUIRES_INGREDIENT, TABLE_REQUIRES_TOOL, TABLE_REQUIRES_TECHNIQUE, TABLE_HAS_EXTERNAL_URL,
            TABLE_USER, TABLE_ALLERGIC_TO, TABLE_OWNS_TOOL, TABLE_PINNED_RECIPE, TABLE_HAS_COOKED,
            TABLE_HAS_ON_GROCERY_LIST, TABLE_RATES_RECIPE, TABLE_RATES_CUISINE, TABLE_RATES_MEAL_BASE};

    public static String DATABASE_REAL_NAME = "TheStudentsCookbook.db";
    public static String DATABASE_TEST_NAME = "test_" + DATABASE_REAL_NAME;
    public static String DATABASE_NAME = DATABASE_REAL_NAME;
    public static String DATABASE_PATH = "/data/data/cs506.studentcookbook/databases/";
    public static String DATABASE_TEST_PATH = "/data/user/0/cs506.studentcookbook/databases/";

    private static DBTools dbTools;
    private static Context currentContext;

    public enum PopulationMode { WEB, LOCAL }
    public static PopulationMode CURRENT_POPULATION_MODE = PopulationMode.LOCAL;
    private static final boolean POPULATE_EVERY_TIME = false;

    public static final boolean LIKE = false;
    public static final boolean DISLIKE = true;
    public static final int INITIAL_PROFILE_LIKE = 10;

    private static final double SMOOTHING_FACTOR = 0.0001;
    private static boolean countDataIsFresh = false;
    private static double countLikeCuisine = -1.0;
    private static double countDislikeCuisine = -1.0;
    private static double countLikeBase = -1.0;
    private static double countDislikeBase = -1.0;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Constructor and helpers
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public DBTools(Context context) {
        super(context, DATABASE_NAME, null, 1);
        currentContext = context;
        System.out.println("Creating new DBTools instance");
    }

    public void onCreate(SQLiteDatabase db) {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        resetDatabase();
        onCreate(db);
    }

    public static DBTools getInstance(Context c) {
        if(dbTools == null)
            dbTools = new DBTools(c);
        return dbTools;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Database creation
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public void resetDatabase() {
        System.out.println("Resetting database");
        dropTables();
        currentContext.deleteDatabase(DATABASE_NAME);
    }

    public void populateDatabase() {
        if(CURRENT_POPULATION_MODE == PopulationMode.LOCAL) {
            populateDatabaseFromLocal();
        } else if(CURRENT_POPULATION_MODE == PopulationMode.WEB) {
            populateDatabaseFromWeb();
        }
    }

    public boolean databaseFileExists() {
        SQLiteDatabase checkDB = null;

        try {
            checkDB = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e){
            System.out.println("Database file does not exist");
            return false;
        }

        if(checkDB != null){
            checkDB.close();
            //System.out.println("Database file already exists");
            return true;
        }

        System.out.println("Database file does not exist");
        return false;
    }

    public boolean databaseIsPopulated() {
        String selectQuery = "SELECT * FROM Recipe";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(selectQuery, null);
        } catch (Exception e) {
        }

        boolean toReturn = true;

        if(cursor == null || cursor.getCount() == 0)
            toReturn = false;

        db.close();
        return toReturn;
    }

    public void createTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        createRecipeTables(db);
        createUserTables(db);
        createSuggestionTables(db);
        db.close();
        Log.d("Creating tables", "...");
    }

    private void dropTables() {
        SQLiteDatabase db = this.getWritableDatabase();

        boolean result = true;

        for(String table : ALL_TABLES) {
            String drop = "DROP TABLE " + table + ";";

            try {
                db.execSQL(drop);
                Log.d("Dropping", table);
            } catch (SQLException e) {
            }
        }
        db.close();
    }

    private void populateDatabaseFromLocal() {
        if(databaseFileExists() && databaseIsPopulated()) {
            System.out.println("Database already locally populated");
            return;
        }

        try {
            this.getReadableDatabase();

            InputStream myInput = currentContext.getAssets().open(DATABASE_NAME);
            String outFileName = DATABASE_PATH + DATABASE_NAME;
            OutputStream myOutput = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer))>0){
                myOutput.write(buffer, 0, length);
            }

            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();

        } catch (IOException e) {
            System.err.println("Error copying database.");
        }

        System.out.println("Database populated locally");
    }

    private void populateDatabaseFromWeb() {
        /**
         * Uses APIGrabber to populate the internal database.
         *
         * Returns true if the database was populated, false if no action was taken because the database
         * was already populated
         */

        if(!POPULATE_EVERY_TIME) {
            if (databaseIsPopulated()) {
                Log.d("database:", "already populated");
                return;
            }
        }

        System.out.println("Beginning database population from the web...");
        createTables();

        // network activity needs to be on a separate thread or Android will throw an exception
        Thread thread = new Thread(new Runnable(){
            public void run() {
                try {
                    int recipeCount = 0;
                    int ingredientCount = 0;

                    for(String s : APIGrabber.SIMPLE_POPULATION_KEYWORDS) {

                        List<Recipe> recipes = APIGrabber.getRecipes(s);
                        recipeCount += recipes.size();

                        for(Recipe recipe : recipes) {
                            addRecipeToDatabase(recipe);
                            //Log.d("Adding recipe", recipe.toString());
                            Log.d("Adding recipe", recipe.getName());
                            ingredientCount += recipe.getIngredients().size();
                        }
                    }

                    Log.d("Done populating", "...");
                    Log.d("Recipe count", recipeCount + "");
                    Log.d("Ingredient count", ingredientCount + "");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Search and preference methods
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public List<Recipe> getSuggestedRecipes(Preferences preferences) {

        List<Recipe> recipes = new ArrayList<Recipe>();

        if(!databaseFileExists() || !databaseIsPopulated()) {
            return recipes;
        }

        String selectQuery = "";
        SQLiteDatabase db = this.getWritableDatabase();
        boolean doingAISearch = false;

        //"Automatic/ML/Choose for me" search
        if (preferences == null || (preferences.getName() != null && preferences.getName().length() == 0))
        {
            //May need to change this later to add modularity
            int userId = 0;

            recipes = performAISearch(userId);
            doingAISearch = true;
        }

        //"Browse All" (preferences.getName != empty string or null)
        else if (preferences.getName() != null && preferences.getName().length() > 0)
        {
            selectQuery = "SELECT * FROM Recipe WHERE title LIKE '%" + preferences.getName() + "%'";
        }

        //"Questionnaire/Help me choose" search
        else
        {
            String cuisine = prepareQueryLogic(preferences.getLikedCuisines(), "c.recipeId = r.recipeId", "c.cuisineName");
            String base = prepareQueryLogic(preferences.getLikedBases(), "b.recipeId = r.recipeId", "b.baseName");

            if(cuisine == null && base == null) {
                selectQuery = "SELECT * FROM Recipe";
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("SELECT DISTINCT r.recipeId, r.bigOvenId, r.title, r.instructions, r.cookTime, r.prepTime, r.imageURL, r.isASide, r.estimateCost ");
                sb.append("FROM Recipe r");

                if(cuisine != null)
                    sb.append(", Has_Cuisine_Type c");
                if(base != null)
                    sb.append(", Has_Meal_Base b");

                sb.append(" WHERE ");

                if(cuisine != null)
                    sb.append(cuisine);
                if(base != null && cuisine != null)
                    sb.append(" OR " + base);
                else if (base != null)
                    sb.append(base);

                /*
                if(preferences.getIngredients() != null && preferences.getIngredients().size() > 0) {
                    List<String> ingredients = new ArrayList<String>();
                    for(Ingredient i : preferences.getIngredients())
                        ingredients.add(i.getName());

                    next = prepareQueryLogic(ingredients, "i.recipeId = r.recipeId", "i.ingredientName");
                    if (next != null)
                        sb.append("OR " + next + " ");
                }
                */

                selectQuery = sb.toString();
            }
        }

        Cursor cursor = null;
        int index;

        if(!doingAISearch) {
            cursor = db.rawQuery(selectQuery, null);
            index = 0;

            if (cursor.moveToFirst()) {
                do {
                    //Add new recipe to list of recipes
                    recipes.add(new Recipe());

                    //Adds values to recipe object in list that are pulled from Recipe table
                    recipes.get(index).setId(Integer.parseInt(cursor.getString(0)));
                    recipes.get(index).setBigOvenId(Integer.parseInt(cursor.getString(1)));
                    recipes.get(index).setName(cursor.getString(2));
                    recipes.get(index).setInstructions(cursor.getString(3));
                    recipes.get(index).setCookTime(Integer.parseInt(cursor.getString(4)));
                    recipes.get(index).setPrepTime(Integer.parseInt(cursor.getString(5)));
                    recipes.get(index).setImageURL(cursor.getString(6));
                    recipes.get(index).setIsASide(Boolean.parseBoolean(cursor.getString(7)));
                    try{
                        recipes.get(index).setCost(Double.parseDouble(cursor.getString(8)));}
                    catch (Exception e) { //The database doesn't have a lot of costs filled in
                    }

                    int recipeId = recipes.get(index).getId();

                    //Fill in bases,cuisines,ingredients,techniques,tools for this recipe in list
                    recipes.get(index).setBases(this.getBases(recipeId));
                    recipes.get(index).setCuisines(this.getCuisines(recipeId));
                    recipes.get(index).setIngredients(this.getIngredients(recipeId));
                    recipes.get(index).setTechniques(this.getTechniques(recipeId));
                    recipes.get(index).setTools(this.getTools(recipeId));

                    //Increment counter var
                    index++;
                } while (cursor.moveToNext());
            }
        }

        return recipes;

//        //Queries ingredient table to build list of ingredients for list of recipes
//        for (int i = 0; i < recipes.size(); i++) {
//            int currRecipeId = recipes.get(i).getId();
//            List<Ingredient> ingredients = new ArrayList<Ingredient>();
//            selectQuery = "SELECT * FROM Requires_Ingredient WHERE recipeId='" + currRecipeId + "'";
//
//            cursor = db.rawQuery(selectQuery, null);
//            index = 0;
//
//            if (cursor.moveToFirst()) {
//                do {
//                    //Add new recipe to list of recipes
//                    ingredients.add(new Ingredient());
//
//
//
//                    //Extract values from DB and add to ingredients object in list
//                    ingredients.get(index).setName(cursor.getString(1));
//                    try {
//                        ingredients.get(index).setAmount(Double.parseDouble(cursor.getString(2)));
//                    } catch (Exception e) { //The database doesn't have all amts filled in
//                    }   //Currently can be null.
//                    try {
//                        ingredients.get(index).setUnit(cursor.getString(3));
//                    } catch (Exception e) { //The database doesn't have all units filled in
//                    }   //Currently can be null.
//
//                    //Increment counter var
//                    index++;
//                } while (cursor.moveToNext());
//            }
//
//            recipes.get(i).setIngredients(ingredients);
//            cursor.close();
//        }
//
//        //NOTE: As of iteration 1, this ONLY acquires names for techniques.
//        //This is because we are lacking data entries in the database for descriptions, URLS, etc.
//        for (int i = 0; i < recipes.size(); i++) {
//            int currRecipeId = recipes.get(i).getId();
//            List<Technique> techniques = new ArrayList<Technique>();
//            selectQuery = "SELECT techniqueName FROM Requires_Technique WHERE recipeId='" + currRecipeId + "'";
//
//            cursor = db.rawQuery(selectQuery, null);
//            index = 0;
//
//            if (cursor.moveToFirst()) {
//                do {
//                    //Add new recipe to list of recipes
//                    techniques.add(new Technique());
//
//                    //Extract values from DB and add to ingredients object in list
//                    try {
//                        techniques.get(index).setName(cursor.getString(0));
//                    } catch (Exception e) { //The database doesn't have all amts filled in
//                    }   //Currently can be null, if there is no entry in db
//
//                    //Increment counter var
//                    index++;
//                } while (cursor.moveToNext());
//            }
//
//            recipes.get(i).setTechniques(techniques);
//            cursor.close();
//        }
//
//        //Queries Tool and Requires_Tool tables for list of tools
//        //NOTE: As of iteration 1, this ONLY acquires names for tools.
//        //This is because we are lacking data entries in the database for descriptions, URLS, etc.
//        for (int i = 0; i < recipes.size(); i++) {
//            int currRecipeId = recipes.get(i).getId();
//            List<Tool> tools = new ArrayList<Tool>();
//            selectQuery = "SELECT toolName FROM Requires_Tool WHERE recipeId='" + currRecipeId + "'";
//
//            cursor = db.rawQuery(selectQuery, null);
//            index = 0;
//
//            if (cursor.moveToFirst()) {
//                do {
//                    //Add new recipe to list of recipes
//                    tools.add(new Tool());
//
//                    //Extract values from DB and add to ingredients object in list
//                    try {
//                        tools.get(index).setName(cursor.getString(0));
//                    } catch (Exception e) { //The database doesn't have all amts filled in
//                    }   //Currently can be null, if there is no entry in db
//
//                    //Increment counter var
//                    index++;
//                } while (cursor.moveToNext());
//            }
//
//            recipes.get(i).setTools(tools);
//            cursor.close();
//        }
//
//        //Obtains bases in recipe from db
//        //All of your bases are belong to us
//        for (int i = 0; i < recipes.size(); i++) {
//            int currRecipeId = recipes.get(i).getId();
//            List<String> bases = new ArrayList<String>();
//            selectQuery = "SELECT baseName FROM Has_Meal_Base WHERE recipeId='" + currRecipeId + "'";
//
//            cursor = db.rawQuery(selectQuery, null);
//
//            if (cursor.moveToFirst()) {
//                do {
//                    bases.add(cursor.getString(0));
//                } while (cursor.moveToNext());
//            }
//
//            recipes.get(i).setBases(bases);
//            cursor.close();
//        }
//
//        //Obtains cuisines in recipe from db
//        for (int i = 0; i < recipes.size(); i++) {
//            int currRecipeId = recipes.get(i).getId();
//            List<String> cuisineTypes = new ArrayList<String>();
//            selectQuery = "SELECT cuisineName FROM Has_Cuisine_Type WHERE recipeId='" + currRecipeId + "'";
//
//            cursor = db.rawQuery(selectQuery, null);
//
//            if (cursor.moveToFirst()) {
//                do {
//                    cuisineTypes.add(cursor.getString(0));
//                } while (cursor.moveToNext());
//            }
//
//            recipes.get(i).setBases(cuisineTypes);
//            cursor.close();
//        }
//
//        db.close();
//
//        return recipes;
    }

    public Preferences getPreferences(){
        Preferences preferences = new Preferences();
        List<String> likedCuisines = new ArrayList<String>();
        List<String> disLikedCuisines = new ArrayList<String>();
        List<String> likedBases = new ArrayList<String>();
        List<String> disLikedBases = new ArrayList<String>();

        SQLiteDatabase db = this.getWritableDatabase();

        //Groupsize, prepTime, cookTime
        String selectQuery = "SELECT groupSize, prepTime, cookTime FROM User";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                preferences.setGroupSize(Integer.parseInt(cursor.getString(0)));
                preferences.setPrepTime(Integer.parseInt(cursor.getString(1)));
                preferences.setCookTime(Integer.parseInt(cursor.getString(2)));

            } while (cursor.moveToNext());
        }
        cursor.close();

        //Liked Cuisines
        selectQuery = "SELECT cuisineName FROM Rates_Cuisine WHERE countLiked > countDisliked;";
        cursor = db.rawQuery(selectQuery,null);

        if (cursor.moveToFirst()) {
            do {
                likedCuisines.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        //Disliked Cuisines
        selectQuery = "SELECT cuisineName FROM Rates_Cuisine WHERE countLiked < countDisliked;";
        cursor = db.rawQuery(selectQuery,null);

        if (cursor.moveToFirst()) {
            do {
                disLikedCuisines.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        //Liked Bases
        selectQuery = "SELECT baseName FROM Rates_Meal_Base WHERE countLiked > countDisliked;";
        cursor = db.rawQuery(selectQuery,null);

        if (cursor.moveToFirst()) {
            do {
                likedBases.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        //Disliked Bases
        selectQuery = "SELECT baseName FROM Rates_Meal_Base WHERE countLiked < countDisliked;";
        cursor = db.rawQuery(selectQuery,null);

        if (cursor.moveToFirst()) {
            do {
                disLikedBases.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        //Add liked/disliked cuisines/bases to prefs object
        preferences.setLikedCuisines(likedCuisines);
        preferences.setDislikedCuisines(disLikedCuisines);
        preferences.setLikedBases(likedBases);
        preferences.setDislikedBases(disLikedBases);

        return preferences;
    }

    //This only records the semipermanent information used in preferences.
    //for example, we don't store the name of a recipe from a text search for later use.
    //However, we want to keep the last used group size, prep time, cook time.
    //NOTE: DOES NOT CHANGE LIKED/DISLIKED CUISINES/BASES -- use incrementRating functions for that
    public void setPreferences(Preferences preferences) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE User\n"
                + "SET groupSize=" + preferences.getGroupSize()
                + ", prepTime=" + preferences.getPrepTime()
                + ", cookTime=" + preferences.getCookTime();

        db.execSQL(updateQuery);
    }

    public List<String> getAllergicBases() {
        List<String> allergicBases = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT baseName FROM Allergic_To";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                allergicBases.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        return allergicBases;
    }

    public void addAllergicBase(String baseName) {
        //Until we add functionality to handle multiple users, use this:
        int userId = 0;

        String insertStatement = "INSERT INTO Allergic_To (userId, baseName)\n"
                +"VALUES (" + userId
                +", '" + baseName + "');";

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL(insertStatement);
        } catch (SQLiteConstraintException e){
        }
    }

    public void removeAllergicBase(String baseName) {
        //Until we add functionality to handle multiple users, use this:
        int userId = 0;

        String insertStatement = "DELETE FROM Allergic_To\n"
                +"WHERE (" + "userId=" + userId + " AND baseName='"+baseName+"')";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(insertStatement);
    }

    public void addIngredientToGroceryList(Ingredient ingredient) {
        String ingredientName = ingredient.getName();
        SQLiteDatabase db = this.getWritableDatabase();

        //May need to change this later to add modularity
        int userId = 0;

        String insertStatement = "INSERT INTO Has_On_Grocery_List (userId, ingredientName)\n"
                +"VALUES (" + userId
                +", '" + ingredientName + "');";

        db.execSQL(insertStatement);
    }

    public void removeIngredientFromGroceryList(Ingredient ingredient) {
        String ingredientName = ingredient.getName();
        SQLiteDatabase db = this.getWritableDatabase();

        //May need to change this later to add modularity
        int userId = 0;

        String removeStatement = "DELETE FROM Has_On_Grocery_List\n"
                +"WHERE (" + "userId=" + userId + " AND ingredientName='"+ingredientName+"')";

        db.execSQL(removeStatement);
    }

    //NOTE: has only ingredient names as per our design doc
    public GroceryList getGroceryList() {
        GroceryList groceryList = new GroceryList();
        List<String> ingredientNames = new ArrayList<String>();

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT ingredientName FROM Has_On_Grocery_List";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ingredientNames.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        groceryList.setIngredients(ingredientNames);

        return groceryList;
    }

    public void addRecipeToPinned(Recipe recipe) {
        int recipeId = recipe.getId();

        SQLiteDatabase db = this.getWritableDatabase();

        //May need to change this later to add modularity
        int userId = 0;

        String insertStatement = "INSERT INTO Pinned_Recipe (userId, recipeId)\n"
                +"VALUES (" + userId
                +", " + recipeId + ");";

        db.execSQL(insertStatement);
    }

    public List<Recipe> getPinnedRecipes() {
        List<Recipe> recipes = new ArrayList<Recipe>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT Pinned_Recipe.recipeId, Recipe.*"
                +"FROM Pinned_Recipe\n"
                +"INNER JOIN\n"
                +"Recipe\n"
                +"ON Pinned_Recipe.recipeId = Recipe.recipeId;";

        Cursor cursor = db.rawQuery(selectQuery, null);
        int index = 0;

        if (cursor.moveToFirst()) {
            do {
                //Add new recipe to list of recipes
                recipes.add(new Recipe());

                //Extract values from DB and add to new recipe object in list
                recipes.get(index).setId(Integer.parseInt(cursor.getString(1)));
                recipes.get(index).setBigOvenId(Integer.parseInt(cursor.getString(2)));
                recipes.get(index).setName(cursor.getString(3));
                recipes.get(index).setInstructions(cursor.getString(4));
                recipes.get(index).setCookTime(Integer.parseInt(cursor.getString(5)));
                recipes.get(index).setPrepTime(Integer.parseInt(cursor.getString(6)));
                recipes.get(index).setImageURL(cursor.getString(7));
                try {
                    recipes.get(index).setIsASide(Boolean.parseBoolean(cursor.getString(8)));
                }
                catch (Exception e1) { //The database doesn't have a lot of isASide filled in
                }
                try{
                    recipes.get(index).setCost(Double.parseDouble(cursor.getString(9)));
                }
                catch (Exception e2) { //The database doesn't have a lot of costs filled in
                }

                int recipeId = recipes.get(index).getId();

                recipes.get(index).setBases(this.getBases(recipeId));
                recipes.get(index).setCuisines(this.getCuisines(recipeId));
                recipes.get(index).setIngredients(this.getIngredients(recipeId));
                recipes.get(index).setTechniques(this.getTechniques(recipeId));
                recipes.get(index).setTools(this.getTools(recipeId));

                //Increment counter var
                index++;
            } while (cursor.moveToNext());
        }

        return recipes;
    }

    public void removeRecipeFromPinned(Recipe recipe) {
        int recipeId = recipe.getId();

        SQLiteDatabase db = this.getWritableDatabase();

        //May need to change this later to add modularity
        int userId = 0;

        String removeStatement = "DELETE FROM Pinned_Recipe\n"
                +"WHERE (" + "userId=" + userId + " AND recipeId="+recipeId+")";

        db.execSQL(removeStatement);
    }

    public void addRecipeRating(Recipe recipe, boolean isLiked) {
        int recipeId = recipe.getId();

        String bool;
        //if (isLiked)
        //    bool = "\"TRUE\"";
        //else
        //    bool = "\"FALSE\"";

        SQLiteDatabase db = this.getWritableDatabase();

        //May need to change this later to add modularity
        int userId = 0;

        //String insertStatement = "INSERT INTO Rates_Recipe (userId, recipeId, isLiked)\n"
        //        +"VALUES (" + userId
        //        +", " + recipeId
        //        +", " + bool + ");";

        //db.execSQL(insertStatement);

        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("recipeId", recipeId);
        values.put("isLiked", isLiked);
        db.insertWithOnConflict(TABLE_RATES_RECIPE, null, values, SQLiteDatabase.CONFLICT_REPLACE);


        //Adjust cuisine / base ratings for ML search
        for (int i = 0; i < recipe.getCuisines().size(); i++)
        {
            String currCuisine = recipe.getCuisines().get(i);

            incrementCuisineRating(userId, currCuisine, 1, isLiked);
        }

        for (int i = 0; i < recipe.getBases().size(); i++)
        {
            String currBase = recipe.getBases().get(i);

            incrementBaseRating(userId, currBase, 1, isLiked);
        }
    }

    //Returns whether or not a recipe was liked.
    public boolean getRecipeLiked(Recipe recipe) {
        boolean value = false;

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT isLiked FROM Rates_Recipe \n"
                + "WHERE recipeId=" + recipe.getId() + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            value = Boolean.parseBoolean(cursor.getString(0));
        }
        else
            System.out.println("No entry found in table");

        return value;
    }

    public void clearRecipeRatings() {
        SQLiteDatabase db = this.getWritableDatabase();

        String deleteStatement = "DELETE FROM Rates_Recipe;";

        db.execSQL(deleteStatement);
    }

    //This doesn't update cuisine/base like/dislike statistics, currently.
    public void removeRecipeRating(Recipe recipe) {
        int recipeId = recipe.getId();

        SQLiteDatabase db = this.getWritableDatabase();

        //May need to change this later to add modularity
        int userId = 0;

        String removeStatement = "DELETE FROM Rates_Recipe\n"
                +"WHERE (" + "userId=" + userId + " AND recipeId="+recipeId+")";

        db.execSQL(removeStatement);
    }

    //NOTE: This draws values from User table. This means only try to use this method for fields:
    //userId, email, countRecipesLiked, countRecipesDisliked, groupSize, prepTime, cookTime, estimateCost
    public String getUserSetting(String field) {
        String value = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT " + field + " FROM User";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            value = cursor.getString(0);
        }
        else
            System.out.println("No entries found in table User, column " + field);

        return value;
    }

    public User getUserSettings() {
        User user = new User();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM User";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                user.setId(cursor.getInt(0));
                user.setEmail(cursor.getString(1));
                user.setCountRecipesLiked(cursor.getInt(2));
                user.setCountRecipesDisliked(cursor.getInt(3));
                user.setGroupSize(cursor.getInt(4));
                user.setPrepTime(cursor.getInt(5));
                user.setCookTime(cursor.getInt(6));
                user.setEstimateCost(cursor.getInt(7));

            } while (cursor.moveToNext());
        }

        user.setAllergicBases(this.getAllergicBases());
        user.setTools(this.getUserTools());

        return user;
    }

    //To make this easier to use, the method automatically checks whether it needs to write an
    //update or an insert statement in the db
    //NOTE: This draws values from User table. This means only try to use this method for fields:
    //userId, email, countRecipesLiked, countRecipesDisliked, groupSize, prepTime, cookTime, estimateCost
    public void setUserSetting(String field, String value) {
        final int DEFAULT_ID = 0;
        final String DEFAULT_EMAIL = "";
        final int DEFAULT_CRL = 0;
        final int DEFAULT_CRD = 0;
        final int DEFAULT_GROUPSIZE = 1;
        final int DEFAULT_PREPTIME = 0;
        final int DEFAULT_COOKTIME = 0;
        final int DEFAULT_ESTIMATECOST = 0;

        //Find right table
        String table;
        if (field == "allergicBases") table = "Allergic_To";
        else table = "User";

        //Checks if user table is empty
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "SELECT * FROM " + table;
        Cursor cursor = db.rawQuery(updateQuery, null);
        try {
            cursor = db.rawQuery(updateQuery, null);
        } catch (Exception e) {
        }

        //If there is no inserted row in the table, we have to insert
        //rather than update
        if(cursor == null || cursor.getCount() == 0)
        {


            updateQuery = "INSERT INTO User (userId, email, countRecipesLiked,"
                    + " countRecipesDisliked, groupSize, prepTime, cookTime, "
                    + "estimateCost)\n"
                    + "VALUES (" + DEFAULT_ID
                    + ", '" + DEFAULT_EMAIL
                    + "', " + DEFAULT_CRL
                    + ", "  + DEFAULT_CRD
                    + ", "  + DEFAULT_GROUPSIZE
                    + ", "  + DEFAULT_PREPTIME
                    + ", "  + DEFAULT_COOKTIME
                    + ", "  + DEFAULT_ESTIMATECOST
                    + ");";

            db.execSQL(updateQuery);
        }
        else
        {
            //Note: this changes the values of the entire column.
            //If we add functionality to support the storage of many user
            //accounts, this must be modified (uncomment out below);
            updateQuery = "UPDATE " + table + "\n" +
                    "SET " + field + "='" + value + "'";
            //updateQuery += "\nWHERE userId=" + userId;
            updateQuery += ";";

            db.execSQL(updateQuery);
        }
    }

    //To make this easier to use, the method automatically checks whether it needs to write an
    //update or an insert statement in the db
    //NOTE: This draws values from User table. This means only try to use this method for fields:
    //userId, email, countRecipesLiked, countRecipesDisliked, groupSize, prepTime, cookTime, estimateCost
    public void setUserSetting(String field, int value) {
        final int DEFAULT_ID = 0;
        final String DEFAULT_EMAIL = "";
        final int DEFAULT_CRL = 0;
        final int DEFAULT_CRD = 0;
        final int DEFAULT_GROUPSIZE = 1;
        final int DEFAULT_PREPTIME = 0;
        final int DEFAULT_COOKTIME = 0;
        final int DEFAULT_ESTIMATECOST = 0;

        //Checks if user table is empty
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "SELECT * FROM User";
        Cursor cursor = db.rawQuery(updateQuery, null);
        try {
            cursor = db.rawQuery(updateQuery, null);
        } catch (Exception e) {
        }

        //If there is no inserted row in the table, we have to insert
        //rather than update
        if(cursor == null || cursor.getCount() == 0)
        {


            updateQuery = "INSERT INTO User (userId, email, countRecipesLiked,"
                    + " countRecipesDisliked, groupSize, prepTime, cookTime, "
                    + "estimateCost)\n"
                    + "VALUES (" + DEFAULT_ID
                    + ", '" + DEFAULT_EMAIL
                    + "', " + DEFAULT_CRL
                    + ", "  + DEFAULT_CRD
                    + ", "  + DEFAULT_GROUPSIZE
                    + ", "  + DEFAULT_PREPTIME
                    + ", "  + DEFAULT_COOKTIME
                    + ", "  + DEFAULT_ESTIMATECOST
                    + ");";

            db.execSQL(updateQuery);
        }

        //Note: this changes the values of the entire column.
        //If we add functionality to support the storage of many user
        //accounts, this must be modified (uncomment out below);
        updateQuery = "UPDATE User\n" +
                "SET " + field + "=" + value;
        //updateQuery += "\nWHERE userId=" + userId;
        updateQuery += ";";

        db.execSQL(updateQuery);
    }

    //To make this easier to use, the method automatically checks whether it needs to write an
    //update or an insert statement in the db
    public void setUserSettings(User user) {
        final int DEFAULT_ID = 0;
        final String DEFAULT_EMAIL = "";
        final int DEFAULT_CRL = 0;
        final int DEFAULT_CRD = 0;
        final int DEFAULT_GROUPSIZE = 1;
        final int DEFAULT_PREPTIME = 0;
        final int DEFAULT_COOKTIME = 0;
        final int DEFAULT_ESTIMATECOST = 0;

        //Checks if user table is empty
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "SELECT * FROM User";
        Cursor cursor = db.rawQuery(updateQuery, null);
        try {
            cursor = db.rawQuery(updateQuery, null);
        } catch (Exception e) {
        }

        //If there is no inserted row in the table, we have to insert
        //rather than update
        if(cursor == null || cursor.getCount() == 0)
        {
            updateQuery = "INSERT INTO User (userId, email, countRecipesLiked,"
                    + " countRecipesDisliked, groupSize, prepTime, cookTime, "
                    + "estimateCost)\n"
                    + "VALUES (" + DEFAULT_ID
                    + ", '" + DEFAULT_EMAIL
                    + "', " + DEFAULT_CRL
                    + ", "  + DEFAULT_CRD
                    + ", "  + DEFAULT_GROUPSIZE
                    + ", "  + DEFAULT_PREPTIME
                    + ", "  + DEFAULT_COOKTIME
                    + ", "  + DEFAULT_ESTIMATECOST
                    + ");";

            db.execSQL(updateQuery);
        }

        //Note: this changes the values of the entire column.
        //If we add functionality to support the storage of many user
        //accounts, this must be modified (uncomment out below);
        updateQuery = "UPDATE User\n" +
                "SET userId=" + user.getId() + "," +
                "email='" + user.getEmail() + "'," +
                "countRecipesLiked=" + user.getCountRecipesLiked() + "," +
                "countRecipesDisliked=" + user.getCountRecipesDisliked() + "," +
                "groupSize=" + user.getGroupSize() + "," +
                "prepTime=" + user.getPrepTime() + "," +
                "cookTime=" + user.getCookTime() + "," +
                "estimateCost=" + user.getEstimateCost()
        ;
        //updateQuery += "\nWHERE userId=" + userId;
        updateQuery += ";";
        db.execSQL(updateQuery);

        //Adds tools
        for (int i = 0; i < user.getTools().size(); i++)
        {
            addTool(user.getTools().get(i));
        }

        //Adds allergic bases
        for (int i = 0; i < user.getAllergicBases().size(); i++)
        {
            addAllergicBase(user.getAllergicBases().get(i));
        }
    }

    public List<Tool> getUserTools() {
        List<Tool> tools = new ArrayList<Tool>();

        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT Owns_Tool.toolName, Tool.*"
                + "FROM Owns_Tool\n"
                + "INNER JOIN\n"
                + "Tool\n"
                + "ON Owns_Tool.toolName = Tool.toolName;";

        Cursor cursor = db.rawQuery(selectQuery, null);
        int index = 0;

        if (cursor.moveToFirst()) {
            do {
                tools.add(new Tool());

                tools.get(index).setName(cursor.getString(1));
                tools.get(index).setDescription(cursor.getString(2));
                tools.get(index).setImageURL(cursor.getString(3));

                //Increment counter var
                index++;
            } while (cursor.moveToNext());
        }

        return tools;

    }

    //This gets all tools in the table Tool, this does NOT get the user's owned tools
    public List<Tool> getTools() {
        List<Tool> tools = new ArrayList<Tool>();

        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM Tool";

        Cursor cursor = db.rawQuery(selectQuery, null);
        int index = 0;

        if (cursor.moveToFirst()) {
            do {
                tools.add(new Tool());

                tools.get(index).setName(cursor.getString(0));
                tools.get(index).setDescription(cursor.getString(1));
                tools.get(index).setImageURL(cursor.getString(2));

                //Increment counter var
                index++;
            } while (cursor.moveToNext());
        }

        return tools;
    }

    public void addTool(Tool tool) {
        //Until we add functionality to handle multiple users, use this:
        int userId = 0;

        String toolName = tool.getName();

        String insertStatement = "INSERT INTO Owns_Tool (userId, toolName)\n"
                +"VALUES (" + userId
                +", '" + toolName + "');";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(insertStatement);
    }

    public void removeTool(Tool tool) {
        //Until we add functionality to handle multiple users, use this:
        int userId = 0;

        String toolName = tool.getName();

        String insertStatement = "DELETE FROM Owns_Tool\n"
                +"WHERE (" + "userId=" + userId + " AND toolName='"+toolName+"')";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(insertStatement);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Create tables methods
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void createRecipeTables(SQLiteDatabase db) {
        String tool = "CREATE TABLE IF NOT EXISTS " + TABLE_TOOL + "(" +
                "toolName TEXT PRIMARY KEY NOT NULL" + ", " +
                "description TEXT" + ", " +
                "imageURL TEXT" +
                ");";

        String url = "CREATE TABLE IF NOT EXISTS " + TABLE_URL + "(" +
                "urlText TEXT PRIMARY KEY NOT NULL" +
                ");";

        String ingredient = "CREATE TABLE IF NOT EXISTS " + TABLE_INGREDIENT + "(" +
                "ingredientName TEXT PRIMARY KEY NOT NULL" + ", " +
                "cost REAL" +
                ");";

        String technique = "CREATE TABLE IF NOT EXISTS " + TABLE_TECHNIQUE + "(" +
                "techniqueName TEXT PRIMARY KEY NOT NULL" + ", " +
                "description TEXT NOT NULL" + ", " +
                "difficulty INTEGER" + ", " +
                "imageUrlText TEXT" +
                ");";

        String mealBase = "CREATE TABLE IF NOT EXISTS " + TABLE_MEAL_BASE + "(" +
                "baseName TEXT PRIMARY KEY NOT NULL" +
                ");";

        String cuisine = "CREATE TABLE IF NOT EXISTS " + TABLE_CUISINE + "(" +
                "cuisineName TEXT PRIMARY KEY NOT NULL" +
                ");";

        String recipe = "CREATE TABLE IF NOT EXISTS " + TABLE_RECIPE + "(" +
                "recipeId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" + ", " +
                "bigOvenId INTEGER" + ", " +
                "title TEXT NOT NULL" + ", " +
                "instructions TEXT NOT NULL" + ", " +
                "cookTime INTEGER" + ", " +
                "prepTime INTEGER" + ", " +
                "imageURL TEXT" + ", " +
                "isASide BOOLEAN" + ", " +
                "estimateCost REAL" + ", " +
                "FOREIGN KEY(imageURL) REFERENCES " + TABLE_URL + "(urlText)" +
                ");";

        String hasMealBase = "CREATE TABLE IF NOT EXISTS " + TABLE_HAS_MEAL_BASE + "(" +
                "recipeId INTEGER NOT NULL" + ", " +
                "baseName TEXT NOT NULL" + ", " +
                "PRIMARY KEY (recipeId, baseName)" + ", " +
                "FOREIGN KEY(recipeId) REFERENCES " + TABLE_RECIPE + "(recipeId)" + ", " +
                "FOREIGN KEY(baseName) REFERENCES " + TABLE_MEAL_BASE + "(baseName)" +
                ");";

        String hasCuisineType = "CREATE TABLE IF NOT EXISTS " + TABLE_HAS_CUISINE_TYPE + "(" +
                "recipeId INTEGER NOT NULL" + ", " +
                "cuisineName TEXT NOT NULL" + ", " +
                "PRIMARY KEY (recipeId, cuisineName)" + ", " +
                "FOREIGN KEY(recipeId) REFERENCES " + TABLE_RECIPE + "(recipeId)" + ", " +
                "FOREIGN KEY(cuisineName) REFERENCES " + TABLE_CUISINE + "(cuisineName)" +
                ");";

        String requiresIngredient = "CREATE TABLE IF NOT EXISTS " + TABLE_REQUIRES_INGREDIENT + "(" +
                "recipeId INTEGER NOT NULL" + ", " +
                "ingredientName TEXT NOT NULL" + ", " +
                "quantity REAL" + ", " +
                "unit TEXT" + ", " +
                "PRIMARY KEY (recipeId, ingredientName)" + ", " +
                "FOREIGN KEY(recipeId) REFERENCES " + TABLE_RECIPE + "(recipeId)" + ", " +
                "FOREIGN KEY(ingredientName) REFERENCES " + TABLE_INGREDIENT + "(ingredientName)" +
                ");";

        String requiresTool = "CREATE TABLE IF NOT EXISTS " + TABLE_REQUIRES_TOOL + "(" +
                "recipeId INTEGER NOT NULL" + ", " +
                "toolName TEXT NOT NULL" + ", " +
                "PRIMARY KEY (recipeId, toolName)" + ", " +
                "FOREIGN KEY(recipeId) REFERENCES " + TABLE_RECIPE + "(recipeId)" + ", " +
                "FOREIGN KEY(toolName) REFERENCES " + TABLE_TOOL + "(toolName)" +
                ");";

        String requiresTechnique = "CREATE TABLE IF NOT EXISTS " + TABLE_REQUIRES_TECHNIQUE + "(" +
                "recipeId INTEGER NOT NULL" + ", " +
                "techniqueName TEXT NOT NULL" + ", " +
                "PRIMARY KEY (recipeId, techniqueName)" + ", " +
                "FOREIGN KEY(recipeId) REFERENCES " + TABLE_RECIPE + "(recipeId)" + ", " +
                "FOREIGN KEY(techniqueName) REFERENCES " + TABLE_TECHNIQUE + "(techniqueName)" +
                ");";

        String hasExternalURL = "CREATE TABLE IF NOT EXISTS " + TABLE_HAS_EXTERNAL_URL + "(" +
                "techniqueName TEXT NOT NULL" + ", " +
                "urlText TEXT NOT NULL" + ", " +
                "text TEXT" + ", " +
                "PRIMARY KEY (techniqueName, urlText)" + ", " +
                "FOREIGN KEY(techniqueName) REFERENCES " + TABLE_TECHNIQUE + "(techniqueName)" + ", " +
                "FOREIGN KEY(urlText) REFERENCES " + TABLE_URL + "(urlText)" +
                ");";

        db.execSQL(recipe);
        db.execSQL(tool);
        db.execSQL(url);
        db.execSQL(ingredient);
        db.execSQL(technique);
        db.execSQL(mealBase);
        db.execSQL(cuisine);
        db.execSQL(hasMealBase);
        db.execSQL(hasCuisineType);
        db.execSQL(requiresIngredient);
        db.execSQL(requiresTool);
        db.execSQL(requiresTechnique);
        db.execSQL(hasExternalURL);
    }

    private void createUserTables(SQLiteDatabase db) {
        String user = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "(" +
                "userId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" + ", " +
                "email TEXT" + ", " +
                "countRecipesLiked INTEGER NOT NULL DEFAULT 0" + ", " +
                "countRecipesDisliked INTEGER NOT NULL DEFAULT 0" + ", " +
                "groupSize INTEGER" + ", " +
                "prepTime INTEGER" + ", " +
                "cookTime INTEGER" + ", " +
                "estimateCost REAL" +
                ");";

        String allergicTo = "CREATE TABLE IF NOT EXISTS " + TABLE_ALLERGIC_TO + "(" +
                "userId INTEGER NOT NULL" + ", " +
                "baseName TEXT NOT NULL" + ", " +
                "PRIMARY KEY (userId, baseName)" + ", " +
                "FOREIGN KEY(userId) REFERENCES " + TABLE_USER + "(userId)" + ", " +
                "FOREIGN KEY(baseName) REFERENCES " + TABLE_MEAL_BASE + "(baseName)" +
                ");";

        String ownsTool = "CREATE TABLE IF NOT EXISTS " + TABLE_OWNS_TOOL + "(" +
                "userId INTEGER NOT NULL" + ", " +
                "toolName TEXT NOT NULL" + ", " +
                "PRIMARY KEY (userId, toolName)" + ", " +
                "FOREIGN KEY(userId) REFERENCES " + TABLE_USER + "(userId)" + ", " +
                "FOREIGN KEY(toolName) REFERENCES " + TABLE_TOOL + "(toolName)" +
                ");";

        String pinnedRecipe = "CREATE TABLE IF NOT EXISTS " + TABLE_PINNED_RECIPE + "(" +
                "userId INTEGER NOT NULL" + ", " +
                "recipeId INTEGER NOT NULL" + ", " +
                "PRIMARY KEY (userId, recipeId)" + ", " +
                "FOREIGN KEY(userId) REFERENCES " + TABLE_USER + "(userId)" + ", " +
                "FOREIGN KEY(recipeId) REFERENCES " + TABLE_RECIPE + "(recipeId)" +
                ");";

        String hasCooked = "CREATE TABLE IF NOT EXISTS " + TABLE_HAS_COOKED + "(" +
                "userId INTEGER NOT NULL" + ", " +
                "recipeId INTEGER NOT NULL" + ", " +
                "dateCooked DATE DEFAULT CURRENT_TIMESTAMP" + ", " +
                "PRIMARY KEY (userId, recipeId)" + ", " +
                "FOREIGN KEY(userId) REFERENCES " + TABLE_USER + "(userId)" + ", " +
                "FOREIGN KEY(recipeId) REFERENCES " + TABLE_RECIPE + "(recipeId)" +
                ");";

        String hasOnGroceryList = "CREATE TABLE IF NOT EXISTS " + TABLE_HAS_ON_GROCERY_LIST + "(" +
                "userId INTEGER NOT NULL" + ", " +
                "ingredientName TEXT NOT NULL" + ", " +
                "PRIMARY KEY (userId, ingredientName)" + ", " +
                "FOREIGN KEY(userId) REFERENCES " + TABLE_USER + "(userId)" + ", " +
                "FOREIGN KEY(ingredientName) REFERENCES " + TABLE_INGREDIENT + "(ingredientName)" +
                ");";

        db.execSQL(user);
        db.execSQL(allergicTo);
        db.execSQL(ownsTool);
        db.execSQL(pinnedRecipe);
        db.execSQL(hasCooked);
        db.execSQL(hasOnGroceryList);
    }

    private void createSuggestionTables(SQLiteDatabase db) {
        String ratesRecipe = "CREATE TABLE IF NOT EXISTS " + TABLE_RATES_RECIPE + "(" +
                "userId INTEGER NOT NULL" + ", " +
                "recipeId INTEGER NOT NULL" + ", " +
                "isLiked BOOLEAN NOT NULL" + ", " +
                "PRIMARY KEY (userId, recipeId)" + ", " +
                "FOREIGN KEY(userId) REFERENCES " + TABLE_USER + "(userId)" + ", " +
                "FOREIGN KEY(recipeId) REFERENCES " + TABLE_RECIPE + "(recipeId)" +
                ");";

        String ratesCuisine = "CREATE TABLE IF NOT EXISTS " + TABLE_RATES_CUISINE + "(" +
                "userId INTEGER NOT NULL" + ", " +
                "cuisineName TEXT NOT NULL" + ", " +
                "countLiked INTEGER DEFAULT 0" + ", " +
                "countDisliked INTEGER DEFAULT 0" + ", " +
                "PRIMARY KEY (userId, cuisineName)" + ", " +
                "FOREIGN KEY(userId) REFERENCES " + TABLE_USER + "(userId)" + ", " +
                "FOREIGN KEY(cuisineName) REFERENCES " + TABLE_CUISINE + "(cuisineName)" +
                ");";

        String ratesMealBase = "CREATE TABLE IF NOT EXISTS " + TABLE_RATES_MEAL_BASE + "(" +
                "userId INTEGER NOT NULL" + ", " +
                "baseName TEXT NOT NULL" + ", " +
                "countLiked INTEGER DEFAULT 0" + ", " +
                "countDisliked INTEGER DEFAULT 0" + ", " +
                "PRIMARY KEY (userId, baseName)" + ", " +
                "FOREIGN KEY(userId) REFERENCES " + TABLE_USER + "(userId)" + ", " +
                "FOREIGN KEY(baseName) REFERENCES " + TABLE_MEAL_BASE + "(baseName)" +
                ");";

        db.execSQL(ratesRecipe);
        db.execSQL(ratesCuisine);
        db.execSQL(ratesMealBase);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Add to database methods
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public void addRecipeToDatabase(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();

//        String query = "SELECT * FROM " + TABLE_RECIPE + " WHERE bigOvenId=" + recipe.getBigOvenId();
//        Cursor cursor = db.rawQuery(query, null);
//        if(cursor.moveToFirst()) {
//            return; // we already added the recipe
//        }
//        cursor.close();

        // add tools
        for(Tool tool : recipe.getTools())
            addToolToDatabase(tool);

        // add techniques
        for (Technique technique : recipe.getTechniques())
            addTechniqueToDatabase(technique);

        // add ingredients
        for(Ingredient ingredient : recipe.getIngredients())
            addIngredientToDatabase(ingredient.getName());

        // add meal base
        for(String base : recipe.getBases())
            addMealBaseToDatabase(base);

        // add cuisine
        for(String cuisine : recipe.getCuisines())
            addCuisineToDatabase(cuisine);

        // add url
        addURLToDatabase(recipe.getImageURL());

        // add recipe
        ContentValues values = new ContentValues();
        //values.put("recipeId", recipe.getBigOvenId());
        values.put("bigOvenId", recipe.getBigOvenId());
        values.put("title", recipe.getName());
        values.put("instructions", recipe.getInstructions());
        values.put("cookTime", recipe.getCookTime());
        values.put("prepTime", recipe.getPrepTime());
        values.put("imageURL", recipe.getImageURL());
        //values.put("isASide", false);
        //values.put("estimateCost", 0.0);
        int id = (int) db.insertWithOnConflict(TABLE_RECIPE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        recipe.setId(id);

        // add requires tool
        for(Tool tool : recipe.getTools())
            addRequiresToolToDatabase(recipe, tool);

        // add requires technique
        for (Technique technique : recipe.getTechniques())
            addRequiresTechniqueToDatabase(recipe, technique);

        // add requires ing
        for(Ingredient ingredient : recipe.getIngredients())
            addRequiresIngredientToDatabase(recipe, ingredient);

        // add has meal base
        for(String base : recipe.getBases())
            addHasMealBaseToDatabase(recipe, base);

        // add  has cuisine
        for(String cuisine : recipe.getCuisines())
            addHasCuisineToDatabase(recipe, cuisine);

        db.close();
    }

    public void addToolToDatabase(Tool tool) {
        int index = Arrays.asList(APIGrabber.TOOLS_LIST).indexOf(tool.getName());
        if(index < 0 || index > APIGrabber.TOOLS_LIST.length) {
            return;
        }

        String desc = APIGrabber.TOOLS_DESCRIPTION[index];
        String imageUrl = APIGrabber.TOOLS_URLS[index];
        tool.setDescription(desc);
        tool.setImageURL(imageUrl);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("toolName", tool.getName());
        values.put("description", tool.getDescription());
        values.put("imageURL", tool.getImageURL());
        db.insertWithOnConflict(TABLE_TOOL, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        values = new ContentValues();
        values.put("urlText", tool.getImageURL());
        db.insertWithOnConflict(TABLE_URL, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void addTechniqueToDatabase(Technique technique) {
        int index = Arrays.asList(APIGrabber.TECHNIQUES_LIST).indexOf(technique.getName());
        if(index < 0 || index > APIGrabber.TECHNIQUES_LIST.length) {
            return;
        }

        String desc = APIGrabber.TECHNIQUES_DESCRIPTION[index];
        String helpURl = APIGrabber.TECHNIQUES_HELP_URLS[index];
        String imageUrl = APIGrabber.TECHNIQUES_IMAGE_URLS[index];
        technique.setDescription(desc);
        technique.addExternalURL(helpURl);
        technique.setImageURL(imageUrl);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("techniqueName", technique.getName());
        values.put("description", technique.getDescription());
        values.put("imageUrlText", technique.getImageURL());
        db.insertWithOnConflict(TABLE_TECHNIQUE, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        values = new ContentValues();
        values.put("urlText", technique.getImageURL());
        db.insertWithOnConflict(TABLE_URL, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        if(technique.getExternalURLs() != null && technique.getExternalURLs().size() > 0) {
            values = new ContentValues();
            values.put("techniqueName", technique.getName());
            values.put("urlText", technique.getExternalURLs().get(0));
            db.insertWithOnConflict(TABLE_HAS_EXTERNAL_URL, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

    public void addIngredientToDatabase(String ingredient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("ingredientName", ingredient);
        db.insertWithOnConflict(TABLE_INGREDIENT, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void addURLToDatabase(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("urlText", url);
        db.insertWithOnConflict(TABLE_URL, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void addCuisineToDatabase(String cuisine) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cuisineName", cuisine);
        db.insertWithOnConflict(TABLE_CUISINE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void addHasCookedToDatabase(int recipeId, String date) {
        int userId = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        String insertStatement = "INSERT INTO Has_Cooked (userId, recipeId, dateCooked)\n"
                +"VALUES (" + userId
                +", " + recipeId
                +", \"" + date + "\");";

        db.execSQL(insertStatement);
    }

    public void addHasCookedToDatabase(List<Recipe> recipes, String date) {
        int userId = 0;

        SQLiteDatabase db = this.getWritableDatabase();

        String insertStatement = "INSERT INTO Has_Cooked (userId, recipeId, dateCooked)\n"
                +"VALUES ";

        for (int i = 0; i < recipes.size() - 1; i++)
        {
            insertStatement += "(" + userId
                    +", " + recipes.get(i).getId()
                    +", \"" + date + "\")"
                    +",";
        }

        insertStatement += "(" + userId
                +", " + recipes.get(recipes.size()-1).getId()
                +", \"" + date + "\")";

        db.execSQL(insertStatement);
    }

    public List<Recipe> getHasCooked() {
        List<Recipe> recipes = new ArrayList<Recipe>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT Has_Cooked.recipeId, Recipe.*"
                +"FROM Has_Cooked\n"
                +"INNER JOIN\n"
                +"Recipe\n"
                +"ON Has_Cooked.recipeId = Recipe.recipeId;";

        Cursor cursor = db.rawQuery(selectQuery, null);
        int index = 0;

        if (cursor.moveToFirst()) {
            do {
                //Add new recipe to list of recipes
                recipes.add(new Recipe());

                //Extract values from DB and add to new recipe object in list
                recipes.get(index).setId(Integer.parseInt(cursor.getString(1)));
                recipes.get(index).setBigOvenId(Integer.parseInt(cursor.getString(2)));
                recipes.get(index).setName(cursor.getString(3));
                recipes.get(index).setInstructions(cursor.getString(4));
                recipes.get(index).setCookTime(Integer.parseInt(cursor.getString(5)));
                recipes.get(index).setPrepTime(Integer.parseInt(cursor.getString(6)));
                recipes.get(index).setImageURL(cursor.getString(7));
                try {
                    recipes.get(index).setIsASide(Boolean.parseBoolean(cursor.getString(8)));
                }
                catch (Exception e1) { //The database doesn't have a lot of isASide filled in
                }
                try{
                    recipes.get(index).setCost(Double.parseDouble(cursor.getString(9)));
                }
                catch (Exception e2) { //The database doesn't have a lot of costs filled in
                }

                int recipeId = recipes.get(index).getId();

                //Fill in bases,cuisines,ingredients,techniques,tools for this recipe in list
                recipes.get(index).setBases(this.getBases(recipeId));
                recipes.get(index).setCuisines(this.getCuisines(recipeId));
                recipes.get(index).setIngredients(this.getIngredients(recipeId));
                recipes.get(index).setTechniques(this.getTechniques(recipeId));
                recipes.get(index).setTools(this.getTools(recipeId));

                //Increment counter var
                index++;
            } while (cursor.moveToNext());
        }

        return recipes;
    }

    public String getHasCookedDate(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        String date = "";

        String selectQuery = "SELECT dateCooked FROM Has_Cooked \n"
                + "WHERE recipeId=" + recipe.getId() + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            date = cursor.getString(0);
        }
        else
            System.out.println("No entry found in table");

        return date;
    }

    public void clearHasCooked() {
        SQLiteDatabase db = this.getWritableDatabase();

        String deleteStatement = "DELETE FROM Has_Cooked;";

        db.execSQL(deleteStatement);
    }

    public void addMealBaseToDatabase(String base) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("baseName", base);
        db.insertWithOnConflict(TABLE_MEAL_BASE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void addRequiresIngredientToDatabase(Recipe recipe, Ingredient ingredient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("recipeId", recipe.getId());
        values.put("ingredientName", ingredient.getName());
        values.put("quantity", ingredient.getAmount());
        values.put("unit", ingredient.getUnit());
        db.insertWithOnConflict(TABLE_REQUIRES_INGREDIENT, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void addRequiresToolToDatabase(Recipe recipe, Tool tool) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("recipeId", recipe.getId());
        values.put("toolName", tool.getName());
        db.insertWithOnConflict(TABLE_REQUIRES_TOOL, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void addRequiresTechniqueToDatabase(Recipe recipe, Technique technique) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("recipeId", recipe.getId());
        values.put("techniqueName", technique.getName());
        db.insertWithOnConflict(TABLE_REQUIRES_TECHNIQUE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void addHasCuisineToDatabase(Recipe recipe, String cuisine) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("recipeId", recipe.getId());
        values.put("cuisineName", cuisine);
        db.insertWithOnConflict(TABLE_HAS_CUISINE_TYPE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void addHasMealBaseToDatabase(Recipe recipe, String base) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("recipeId", recipe.getId());
        values.put("baseName", base);
        db.insertWithOnConflict(TABLE_HAS_MEAL_BASE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void incrementCuisineRating(int userId, String cuisineName, int quantity, boolean like) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_RATES_CUISINE + " WHERE userId=" + userId + " AND cuisineName='" + cuisineName + "'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            ContentValues values = new ContentValues();

            if(like == LIKE) {
                int likeIndex = cursor.getColumnIndex("countLiked");
                int numLiked = cursor.getInt(likeIndex);
                values.put("countLiked", numLiked + quantity);
            } else {
                int dislikeIndex = cursor.getColumnIndex("countDisliked");
                int numDisliked = cursor.getInt(dislikeIndex);
                values.put("countDisliked", numDisliked + quantity);
            }

            db.update(TABLE_RATES_CUISINE, values, "userId=" + userId + " AND cuisineName='" + cuisineName + "'", null);

        } else {
            String columnName = "countLiked";
            if(like == DISLIKE) {
                columnName = "countDisliked";
            }

            ContentValues values = new ContentValues();
            values.put("userId", userId);
            values.put("cuisineName", cuisineName);
            values.put(columnName, quantity);
            db.insert(TABLE_RATES_CUISINE, null, values);
        }

        cursor.close();
        countDataIsFresh = false;
    }

    public void incrementBaseRating(int userId, String baseName, int quantity, boolean like) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_RATES_MEAL_BASE + " WHERE userId=" + userId + " AND baseName='" + baseName + "'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            ContentValues values = new ContentValues();

            if(like == LIKE) {
                int likeIndex = cursor.getColumnIndex("countLiked");
                int numLiked = cursor.getInt(likeIndex);
                values.put("countLiked", numLiked + quantity);
            } else {
                int dislikeIndex = cursor.getColumnIndex("countDisliked");
                int numDisliked = cursor.getInt(dislikeIndex);
                values.put("countDisliked", numDisliked + quantity);
            }

            db.update(TABLE_RATES_MEAL_BASE, values, "userId=" + userId + " AND baseName='" + baseName + "'", null);

        } else {
            String columnName = "countLiked";
            if(like == DISLIKE) {
                columnName = "countDisliked";
            }

            ContentValues values = new ContentValues();
            values.put("userId", userId);
            values.put("baseName", baseName);
            values.put(columnName, quantity);
            db.insert(TABLE_RATES_MEAL_BASE, null, values);
        }

        cursor.close();
        countDataIsFresh = false;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Query helping methods
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public String prepareQueryLogic(List<String> strings, String prefix, String compared) {
        if(strings == null || strings.size() == 0)
            return null;

        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(prefix);
        sb.append(" AND (");
        //sb.append("(c.recipeId = r.recipeId AND (");

        boolean first = true;

        for(String s : strings) {
            if(!first)
                sb.append(" OR ");
            first = false;

            sb.append(compared);
            sb.append(" LIKE '%" + s + "%'");
            //sb.append("c.cuisineName LIKE '%" + s + "%'");
        }

        sb.append("))");
        return sb.toString();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // AI Search methods
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public int getCuisineLikeOrDislikeCount(int userId, String cuisineName, boolean like) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_RATES_CUISINE + " WHERE userId=" + userId + " AND cuisineName='" + cuisineName + "'";
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            String columnName = "countLiked";
            if(like == DISLIKE) {
                columnName = "countDisliked";
            }

            int index = cursor.getColumnIndex(columnName);
            String sVal = cursor.getString(index);

            cursor.close();
            return Integer.parseInt(sVal);
        }

        cursor.close();
        return 0;
    }

    public int getBaseLikeOrDislikeCount(int userId, String baseName, boolean like) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_RATES_MEAL_BASE + " WHERE userId=" + userId + " AND baseName='" + baseName + "'";
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            String columnName = "countLiked";
            if(like == DISLIKE) {
                columnName = "countDisliked";
            }

            int index = cursor.getColumnIndex(columnName);
            String sVal = cursor.getString(index);

            cursor.close();
            return Integer.parseInt(sVal);
        }

        cursor.close();
        return 0;
    }

    /**
     * P(LIKE) or P(DISLIKE)
     */
    public double getProbability(int userId, boolean like) {
        double[] likeAndDislike = totalLikeAndDislike(userId);
        double likeD = likeAndDislike[0];
        double dislikeD = likeAndDislike[1];

        if(likeD + dislikeD == 0) {
            return 0.0;
        }

        if(like == LIKE) {
            return likeD / (likeD + dislikeD);
        } else {
            return dislikeD / (likeD + dislikeD);
        }
    }

    private double[] totalCuisineCount(int userId) {
        boolean goodData = (countDataIsFresh && countDislikeCuisine >= 0.0 && countLikeCuisine >= 0.0 && countLikeBase >= 0.0 && countDislikeBase>= 0.0);
        if(!goodData) {
            totalLikeAndDislike(userId);
        }

        double result[] = {countLikeCuisine, countDislikeCuisine};
        return result;
    }

    private double[] totalBaseCount(int userId) {
        boolean goodData = (countDataIsFresh && countDislikeCuisine >= 0.0 && countLikeCuisine >= 0.0 && countLikeBase >= 0.0 && countDislikeBase>= 0.0);
        if(!goodData) {
            totalLikeAndDislike(userId);
        }

        double result[] = {countLikeBase, countDislikeBase};
        return result;
    }

    private double[] totalLikeAndDislike(int userId) {
        // returned as an array: [like, dislike]
        // cache the value because this operation is expensive

        if(countDataIsFresh && countDislikeCuisine >= 0.0 && countLikeCuisine >= 0.0 && countLikeBase >= 0.0 && countDislikeBase>= 0.0) {
            //System.out.println("Grabbed cached count data.");
            double result[] = {countLikeCuisine + countLikeBase, countDislikeCuisine + countDislikeBase};
            return result;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        // cuisine
        String query = "SELECT * FROM " + TABLE_RATES_CUISINE + " WHERE userId=" + userId;
        Cursor cursor = db.rawQuery(query, null);

        // get count like
        int likeSum = 0;
        int likeIndex = 0;
        String sVal = "";

        if(cursor.moveToFirst()) {
            likeIndex = cursor.getColumnIndex("countLiked");
            sVal = cursor.getString(likeIndex);
            likeSum += Integer.parseInt(sVal);
        }
        while (cursor.moveToNext()) {
            sVal = cursor.getString(likeIndex);
            likeSum += Integer.parseInt(sVal);
        }

        // get count dislike
        int dislikeSum = 0;
        int dislikeIndex = 0;

        if(cursor.moveToFirst()) {
            dislikeIndex = cursor.getColumnIndex("countDisliked");
            sVal = cursor.getString(dislikeIndex);
            dislikeSum += Integer.parseInt(sVal);
        }
        while (cursor.moveToNext()) {
            sVal = cursor.getString(dislikeIndex);
            dislikeSum += Integer.parseInt(sVal);
        }

        // cache the values
        countLikeCuisine = (double) likeSum;
        countDislikeCuisine = (double) dislikeSum;

        cursor.close();

        // base
        query = "SELECT * FROM " + TABLE_RATES_MEAL_BASE + " WHERE userId=" + userId;
        cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            likeIndex = cursor.getColumnIndex("countLiked");
            sVal = cursor.getString(likeIndex);
            likeSum += Integer.parseInt(sVal);
        }
        while (cursor.moveToNext()) {
            sVal = cursor.getString(likeIndex);
            likeSum += Integer.parseInt(sVal);
        }

        if(cursor.moveToFirst()) {
            dislikeIndex = cursor.getColumnIndex("countDisliked");
            sVal = cursor.getString(dislikeIndex);
            dislikeSum += Integer.parseInt(sVal);
        }
        while (cursor.moveToNext()) {
            sVal = cursor.getString(dislikeIndex);
            dislikeSum += Integer.parseInt(sVal);
        }

        cursor.close();

        countLikeBase = ((double) likeSum) - countLikeCuisine;
        countDislikeBase = ((double) dislikeSum) - countDislikeCuisine;

        double likeD = (double) likeSum;
        double dislikeD = (double) dislikeSum;

        countDataIsFresh = true;
        double result[] = {likeD, dislikeD};
        return result;
    }

    /**
     * P(cuisine|LIKE) or P(cuisine|DISLIKE)
     */
    public double getConditionalProbabilityCuisine(int userId, String cuisineName, boolean like) {
        // get how many times the cuisine was liked or disliked
        double unitCount = getCuisineLikeOrDislikeCount(userId, cuisineName, like);

        // get how many times any cuisine was liked or disliked
        double[] cuisineTotal = totalCuisineCount(userId);
        double totalCount = cuisineTotal[0];
        if(like == DISLIKE) {
            totalCount = cuisineTotal[1];
        }

        if(totalCount == 0) {
            return 0.0;
        }

        return (unitCount + SMOOTHING_FACTOR) / (totalCount * (1.0  + SMOOTHING_FACTOR));
    }

    /**
     * P(base|LIKE) or P(base|DISLIKE)
     */
    public double getConditionalProbabilityBase(int userId, String baseName, boolean like) {
        // get how many times the base was liked or disliked
        double unitCount = getBaseLikeOrDislikeCount(userId, baseName, like);

        // get how many times any base was liked or disliked
        double[] baseTotal = totalBaseCount(userId);
        double totalCount = baseTotal[0];
        if(like == DISLIKE) {
            totalCount = baseTotal[1];
        }

        if(totalCount == 0) {
            return 0.0;
        }

        return (unitCount + SMOOTHING_FACTOR) / (totalCount * (1.0  + SMOOTHING_FACTOR));
    }

    /**
     * Use getProbability(), getConditionalProbabilityCuisine(), and getConditionalProbabilityBase()
     * to classify the recipe with this ID as suggseted (true) or not suggested (false)
     */
    public boolean recipeIsSuggested(int recipeId, int userId) {
        List<String> cuisines = getCuisines(recipeId);
        List<String> bases = getBases(recipeId);

        // calculate sum for LIKE
        double probabilityOfLike = 0.0; //getProbability(userId, LIKE);
        for(String cuisine : cuisines) {
            double temp = getConditionalProbabilityCuisine(userId, cuisine, LIKE);
            probabilityOfLike += temp;
        }
        for(String base : bases) {
            double temp = getConditionalProbabilityBase(userId, base, LIKE);
            probabilityOfLike += temp;
        }

        // calculate sum for DISLIKE
        double probabilityOfDislike = 0.0; //getProbability(userId, DISLIKE);
        for(String cuisine : cuisines) {
            double temp = getConditionalProbabilityCuisine(userId, cuisine, DISLIKE);
            probabilityOfDislike += temp;
        }
        for(String base : bases) {
            double temp = getConditionalProbabilityBase(userId, base, DISLIKE);
            probabilityOfDislike += temp;
        }

        // not enough data
        if(probabilityOfLike < 0.01 && probabilityOfDislike < 0.01) {
            return true;
        }

        if(probabilityOfDislike >= 2.0 * probabilityOfLike) {
            return false;
        }

        //if(probabilityOfLike >= probabilityOfDislike) {
        //    return true;
        //}

        return true;
    }

    public List<Recipe> performAISearch(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_RECIPE;
        Cursor cursor = db.rawQuery(query, null);

        List<Integer> ids = new ArrayList<Integer>();

        while(cursor.moveToNext()) {
            String sVal = cursor.getString(0);
            if(sVal != null) {
                try{
                    int value = Integer.parseInt(sVal);
                    ids.add(value);
                } catch (Exception e) {
                }
            }
        }

        List<Recipe> suggested = new ArrayList<Recipe>();

        for(Integer id : ids) {
            if(recipeIsSuggested(id, userId)) {
                Recipe r = getRecipe(id);
                suggested.add(r);
            }
        }

        cursor.close();
        return suggested;
    }

    public List<String> getCuisines(int recipeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT cuisineName FROM " + TABLE_HAS_CUISINE_TYPE + " WHERE recipeId=" + recipeId;
        Cursor cursor = db.rawQuery(query, null);

        List<String> list = new ArrayList<String>();

        while(cursor.moveToNext()) {
            int index = cursor.getColumnIndex("cuisineName");
            String sVal = cursor.getString(index);
            list.add(sVal);
        }
        cursor.close();

        return list;
    }

    public List<String> getBases(int recipeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT baseName FROM " + TABLE_HAS_MEAL_BASE + " WHERE recipeId=" + recipeId;
        Cursor cursor = db.rawQuery(query, null);

        List<String> list = new ArrayList<String>();

        while(cursor.moveToNext()) {
            int index = cursor.getColumnIndex("baseName");
            String sVal = cursor.getString(index);
            list.add(sVal);
        }
        cursor.close();

        return list;
    }

    public List<Ingredient> getIngredients(int recipeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM Requires_Ingredient WHERE recipeId=" + recipeId + "";

        List<Ingredient> ingredients = new ArrayList<Ingredient>();

        //Queries ingredient table to build list of ingredients for a recipe
        Cursor cursor = db.rawQuery(selectQuery, null);
        int index = 0;

        if (cursor.moveToFirst()) {
            do {
                //Add new ingredient to list of ingredients
                ingredients.add(new Ingredient());

                //Extract values from DB and add to ingredients object in list
                ingredients.get(index).setName(cursor.getString(1));
                try {
                    ingredients.get(index).setAmount(Double.parseDouble(cursor.getString(2)));
                } catch (Exception e) { //The database doesn't have all amts filled in
                }   //Currently can be null.
                try {
                    ingredients.get(index).setUnit(cursor.getString(3));
                } catch (Exception e) { //The database doesn't have all units filled in
                }   //Currently can be null.

                //Increment counter var
                index++;
            } while (cursor.moveToNext());
        }

        cursor.close();

        return ingredients;
    }

    public List<Technique> getTechniques(int recipeId) {
        List<String> techniquesTableToString = getTechniquesTableToString();

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT Requires_Technique.techniqueName, Technique.*"
                + "FROM Requires_Technique\n"
                + "INNER JOIN\n"
                + "Technique\n"
                + "ON Requires_Technique.techniqueName = Technique.techniqueName\n"
                + "WHERE recipeId=" + recipeId + ";";

        List<Technique> techniques = new ArrayList<Technique>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        int index = 0;

        if (cursor.moveToFirst()) {
            do {
                //Add new recipe to list of recipes
                techniques.add(new Technique());

                //Extract values from DB and add to ingredients object in list
                try {
                    techniques.get(index).setName(cursor.getString(1));
                    techniques.get(index).setDescription(cursor.getString(2));
                    //techniques.get(index).setDifficulty(cursor.getString(3));
                    //techniques.set(index).setTools(...);
                    techniques.get(index).setImageURL(cursor.getString(4));

                } catch (Exception e) { //The database doesn't have all amts filled in
                }   //Currently can be null, if there is no entry in db

                //Increment counter var
                index++;
            } while (cursor.moveToNext());
        }

        cursor.close();

        return techniques;
    }

    //Gets tools per recipe
    public List<Tool> getTools(int recipeId) {
        SQLiteDatabase db = this.getWritableDatabase();

        List<Tool> tools = new ArrayList<Tool>();

        String selectQuery = "SELECT Requires_Tool.toolName, Tool.*"
                + "FROM Requires_Tool\n"
                + "INNER JOIN\n"
                + "Tool\n"
                + "ON Requires_Tool.toolName = Tool.toolName\n"
                + "WHERE recipeId=" + recipeId + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);
        int index = 0;

        if (cursor.moveToFirst()) {
            do {
                //Add new recipe to list of recipes
                tools.add(new Tool());

                //Extract values from DB and add to ingredients object in list
                try {
                    tools.get(index).setName(cursor.getString(1));
                    tools.get(index).setDescription(cursor.getString(2));
                    tools.get(index).setImageURL(cursor.getString(3));
                } catch (Exception e) { //The database doesn't have all amts filled in
                }   //Currently can be null, if there is no entry in db

                //Increment counter var
                index++;
            } while (cursor.moveToNext());
        }

        cursor.close();

        return tools;
    }

    public Recipe getRecipe(int recipeId) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_RECIPE + " WHERE recipeId=" + recipeId;
        Cursor cursor = db.rawQuery(query, null);

        Recipe r = new Recipe();

        if(cursor.moveToFirst()) {
            r.setId(recipeId);
            r.setBigOvenId(Integer.parseInt(cursor.getString(1)));
            r.setName(cursor.getString(2));
            r.setInstructions(cursor.getString(3));
            r.setCookTime(Integer.parseInt(cursor.getString(4)));
            r.setPrepTime(Integer.parseInt(cursor.getString(5)));
            r.setImageURL(cursor.getString(6));
            r.setIsASide(Boolean.parseBoolean(cursor.getString(7)));
        }

        r.setBases(this.getBases(recipeId));
        r.setCuisines(this.getCuisines(recipeId));
        r.setIngredients(this.getIngredients(recipeId));
        r.setTechniques(this.getTechniques(recipeId));
        r.setTools(this.getTools(recipeId));

        cursor.close();
        return r;
    }

    //Debugging method
    public ArrayList<String> getAllLikedRecipes() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<String> result = new ArrayList<String>();

        String selectQuery = "SELECT * FROM Rates_Recipe";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String currRecipe = "";

                currRecipe += cursor.getString(0);
                currRecipe += ", ";
                currRecipe += cursor.getString(1);
                currRecipe += ", ";
                currRecipe += cursor.getString(2);

                result.add(currRecipe);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return result;
    }

    //Debugging Method
    public ArrayList<String> recipeTableToString() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<String> result = new ArrayList<String>();

        String selectQuery = "SELECT * FROM Recipe";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String currRecipe = "";

                currRecipe += cursor.getString(0);
                currRecipe += ", ";
                currRecipe += cursor.getString(1);
                currRecipe += ", ";
                currRecipe += cursor.getString(2);
                currRecipe += ", ";
                currRecipe += cursor.getString(3);
                currRecipe += ", ";
                currRecipe += cursor.getString(4);
                currRecipe += ", ";
                currRecipe += cursor.getString(5);
                currRecipe += ", ";
                currRecipe += cursor.getString(6);
                currRecipe += ", ";
                currRecipe += cursor.getString(7);
                currRecipe += ", ";
                currRecipe += cursor.getString(8);
                currRecipe += ", ";

                result.add(currRecipe);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return result;
    }

    //Only use this method for adding custom tools to db for testing
    //Forcefully adds a tool to db, doesn't check if in list in APIGrabber
    public void addToolToDatabaseDebug(Tool tool) {
        String insertStatement = "INSERT INTO Tool (toolName, description, imageURL)\n"
                +"VALUES ('" + tool.getName() + "'"
                +", '" + tool.getDescription() + "'"
                +", '" + tool.getImageURL() + "');";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(insertStatement);
    }

    //Only use this method for adding custom techniques to db for testing
    //Forcefully adds a technique to db, doesn't check if in list in APIGrabber
    public void addTechniqueToDatabaseDebug(Technique technique) {
        String insertStatement = "INSERT INTO Technique (techniqueName, description, imageUrlText)\n"
                +"VALUES ('" + technique.getName() + "'"
                +", '" + technique.getDescription() + "'"
                +", '" + technique.getImageURL() + "');";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(insertStatement);
    }

    //Debugging Method.. gets  list of entries of techniques table as list of string
    public List<String> getTechniquesTableToString() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<String> result = new ArrayList<String>();

        String selectQuery = "SELECT * FROM Technique";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String currTechnique = "";

                currTechnique += cursor.getString(0);
                currTechnique += ", ";
                currTechnique += cursor.getString(1);
                currTechnique += ", ";
                currTechnique += cursor.getString(2);
                currTechnique += ", ";
                currTechnique += cursor.getString(3);

                result.add(currTechnique);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return result;
    }
}

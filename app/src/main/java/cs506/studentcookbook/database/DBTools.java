package cs506.studentcookbook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cs506.studentcookbook.model.Tool;
import cs506.studentcookbook.model.Technique;
import cs506.studentcookbook.model.Recipe;
import cs506.studentcookbook.model.Ingredient;
import cs506.studentcookbook.model.Preferences;

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

    private static final String DATABASE_NAME = "TheStudentsCookbook";

    private static DBTools dbTools;

    public DBTools(Context context) {
        super(context, DATABASE_NAME, null, 1);
        createTables();
    }

    public static DBTools getInstance(Context c) {
        if(dbTools == null)
            dbTools = new DBTools(c);
        return dbTools;
    }

    /**
     * Uses APIGrabber to populate the internal database.
     *
     * Returns true if the database was populated, false if no action was taken because the database
     * was already populated
     */
    public void populateDatabase() {
        if(databaseIsPopulated())
            return;

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
                            Log.d("Adding recipe", recipe.toString());
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

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTables();
        createTables();
        onCreate(db);
    }

    public void deleteDatabase() {
        dropTables();
        Log.d("Database cleared", "...");
    }

    public void resetDatabase() {
        deleteDatabase();
        createTables();
    }

    public List<Recipe> getSuggestedRecipes(Preferences preferences) {

        List<Recipe> recipes = new ArrayList<Recipe>();
        String selectQuery = "";
        SQLiteDatabase db = this.getWritableDatabase();

        //"Automatic/ML/Choose for me" search
        if (preferences == null)
        {
            // TODO in second iteration make this the AI search
            selectQuery = "SELECT * FROM Recipe";
        }

        //Text search (preferences.getName != empty string or null)
        else if (preferences.getName() != null && preferences.getName().length() > 0)
        {
            selectQuery = "SELECT * FROM Recipe WHERE title like '%" + preferences.getName() + "%'";
        }

        //"Browse All" search
        //NOTE: if preferences.getTitle = "", will pass all recipes back.
        else if (preferences.getName() == null || preferences.getName().length() == 0)
        {
            selectQuery = "SELECT * FROM Recipe";
        }

        //"Questionnaire/Help me choose" search
        else
        { //TODO
        }

        Cursor cursor = db.rawQuery(selectQuery, null);
        int index = 0;

        if (cursor.moveToFirst()) {
            do {
                //Add new recipe to list of recipes
                recipes.add(new Recipe());

                //Extract values from DB and add to new recipe object in list
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

                //Increment counter var
                index++;
            } while (cursor.moveToNext());
        }

        //TODO: use information to perform another SELECT to fill in other fields of recipe
        //such as base(s), cuisine(s), rating, technique(s), and tool(s).

        db.close();

        return recipes;
    }

    //TODO: Iteration 2+, optimize selects by columns
    public Preferences getPreferences(){
        Preferences preferences = new Preferences();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM User";

        Cursor cursor = db.rawQuery(selectQuery, null);
        int index = 0;

        if (cursor.moveToFirst()) {
            do {
                preferences.setGroupSize(Integer.parseInt(cursor.getString(4)));
                preferences.setPrepTime(Integer.parseInt(cursor.getString(5)));
                preferences.setCookTime(Integer.parseInt(cursor.getString(4)));

                //Increment counter var
                index++;
            } while (cursor.moveToNext());
        }

        //TODO: Iteration 2, finish base/cuisine/ingredientstock for prefs
        //        selectQuery = "SELECT * FROM Rates_Cuisine WHERE countLiked > 0 AND countLiked > countDisliked";
        //        if (cursor.moveToFirst()) {
        //            do {
        //                preferences.setGroupSize(Integer.parseInt(cursor.getString(4)));
        //                preferences.setPrepTime(Integer.parseInt(cursor.getString(5)));
        //                preferences.setCookTime(Integer.parseInt(cursor.getString(4)));
        //
        //                //Increment counter var
        //                index++;
        //            } while (cursor.moveToNext());
        //        }

        return preferences;
    }

    private void createTables() {
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
                "difficulty INTEGER NOT NULL" + ", " +
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
                "cuisineName INTEGER NOT NULL" + ", " +
                "countLiked INTEGER NOT NULL DEFAULT 0" + ", " +
                "countDisliked NOT NULL DEFAULT 0" + ", " +
                "PRIMARY KEY (userId, cuisineName)" + ", " +
                "FOREIGN KEY(userId) REFERENCES " + TABLE_USER + "(userId)" + ", " +
                "FOREIGN KEY(cuisineName) REFERENCES " + TABLE_CUISINE + "(cuisineName)" +
                ");";

        String ratesMealBase = "CREATE TABLE IF NOT EXISTS " + TABLE_RATES_MEAL_BASE + "(" +
                "userId INTEGER NOT NULL" + ", " +
                "baseName INTEGER NOT NULL" + ", " +
                "countLiked INTEGER NOT NULL DEFAULT 0" + ", " +
                "countDisliked NOT NULL DEFAULT 0" + ", " +
                "PRIMARY KEY (userId, baseName)" + ", " +
                "FOREIGN KEY(userId) REFERENCES " + TABLE_USER + "(userId)" + ", " +
                "FOREIGN KEY(baseName) REFERENCES " + TABLE_MEAL_BASE + "(baseName)" +
                ");";

        db.execSQL(ratesRecipe);
        db.execSQL(ratesCuisine);
        db.execSQL(ratesMealBase);
    }

    public void addRecipeToDatabase(Recipe recipe) {
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

        SQLiteDatabase db = this.getWritableDatabase();

        // add recipe
        ContentValues values = new ContentValues();
        //values.put("recipeId", note this is automatically done by the database);
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
        // TODO
    }

    public void addTechniqueToDatabase(Technique tool) {
        // TODO
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
}

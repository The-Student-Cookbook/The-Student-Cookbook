package cs506.studentcookbook.Database;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import cs506.studentcookbook.Model.Ingredient;
import cs506.studentcookbook.Model.Recipe;
import cs506.studentcookbook.Model.Technique;
import cs506.studentcookbook.Model.Tool;

/**
 * This class is used to automatically populate the database with recipes from the BigOven API.
 * Call the getRecipes(String keyword) method with a keyword from POPULATION_KEYWORDS to get a
 * list of Recipe objects containing data from the API.
 */
public class APIGrabber {

    public static final String[] TOOLS_LIST = {"skewer", "grill", "oven", "pot", "saucepan",
            "casserole", "dish", "skillet", "saucepan", "microwave", "broiler", "foil",
            "food processor", "barbecue", "frying pan", "baking pan", "pan", "bowl"};
    public static final String[] TECHNIQUES_LIST = {"marinate", "grill", "boil", "bake",
            "whisk", "fry", "simmer", "cut", "barbecue"};

    public static final String[] CUISINE = {"Cuisine", "Subcategory", "Category"};
    public static final String MEAL_BASE = "PrimaryIngredient";
    public static final String NAME = "Title";
    public static final String IMAGE_URL = "ImageURL";
    public static final String TOTAL_TIME = "TotalMinutes";
    public static final String PREP_TIME = "ActiveMinutes";
    public static final String INSTRUCTIONS = "Instructions";
    public static final String INGREDIENTS = "Ingredients";
    public static final String INGREDIENT = "Ingredient";
    public static final String BIG_OVEN_ID = "RecipeID";

    public static final String INGREDIENT_NAME = "Name";
    public static final String INGREDIENT_QUANTITY = "Quantity";
    public static final String INGREDIENT_UNIT = "Unit";

    public static final String[] POPULATION_KEYWORDS = {"beef", "pork", "lamb",
            "chicken", "turkey", "salmon", "tuna", "tilapia", "sardines", "trout",
            "snapper", "shellfish", "eggs", "honey", "tofu", "soy", "cereal", "pasta",
            "bread", "rice", "oatmeal", "beans", "lentils", "peas", "potato", "corn",
            "artichoke", "asparagus", "beet", "broccoli", "carrot", "sprouts",
            "celery", "garlic", "onion", "pepper", "mushroom", "tomato", "eggplant",
            "avocado", "peanut", "walnut", "pecan", "milk", "cheese", "yogurt",
            "apple", "banana", "grape", "strawberry", "raspberry", "blueberry",
            "mediterranean", "paleo", "vegetarian", "vegan", "italian", "chinese",
            "japanese", "sushi", "hispanic", "american", "submarine", "sandwich",
            "spicy", "indian", "israeli", "thai", "german", "russian", "middle-eastern",
            "breakfast", "lunch", "dinner", "snack", "dessert", "smoothie", "fish"};

    public static final String[] SIMPLE_POPULATION_KEYWORDS = {"beef", "pasta", "hispanic", "vegetarian" };

    private static final String API_KEY = "3h61BCUOSbbRbYq29wkD0gz6gcKItdRR";
    private static final String RECIPE_URL = "http://api.bigoven.com/recipe/";
    private static final String SEARCH_URL = "http://api.bigoven.com/recipes";

    private static final String PARAM_TITLE_KEYWORD = "title_kw=";
    private static final String PARAM_API_KEY = "api_key=";
    private static final String PARAM_PAGE = "pg=";
    private static final String PARAM_RESULTS_PER_PAGE = "rpp=";

    private static final double MIN_REVIEW = 2.0;
    private static final int MIN_REVIEW_COUNT = 2;

    /**
     * Run this with a keyword from POPULATION_KEYWORDS to get a series of recipes from the API.
     * Use these recipes to populate the database. Use all keywords from POPULATION_KEYWORDS to
     * completely populate the database. This should be done in multiple trials, because our free
     * key for the API only allows for 500 calls per hour. Each call to this function will run about
     * 10 - 20 calls to the API.
     *
     * This is the only method you need to call from the outside to get access to the functionality
     * of this class.
     */
    public static List<Recipe> getRecipes(String keyword) {
        return getRecipesFromAPIBasedOnKeywordAndPage(keyword, 1, 50);
    }

    private static List<Recipe> getRecipesFromAPIBasedOnKeywordAndPage(String keyword, int page, int resultsPerPage) {
        Document results = APIGrabber.getAPIRecipeSearchResults(keyword, Integer.toString(page), Integer.toString(resultsPerPage));
        List<Integer> idList = APIGrabber.getRecipeIds(results);

        List<Recipe> recipes = new LinkedList<Recipe>();
        for(Integer id : idList) {
            Document recipeResults = getAPIRecipe(id);
            Recipe recipe = createRecipeFromDocument(recipeResults);
            recipes.add(recipe);
        }

        return recipes;
    }

    private static Document getAPIRecipeSearchResults(String keyword, String page, String resultsPerPage) {
        String urlString = SEARCH_URL + "?" + PARAM_TITLE_KEYWORD + keyword
                + "&" + PARAM_API_KEY + API_KEY + "&" + PARAM_PAGE + page
                + "&" + PARAM_RESULTS_PER_PAGE + resultsPerPage;

        return makeHTTPRequest(urlString);
    }

    private static Document getAPIRecipe(int bigOvenId) {
        String urlString = RECIPE_URL + bigOvenId + "?" + PARAM_API_KEY + API_KEY;
        return makeHTTPRequest(urlString);
    }

    private static Document getDocumentFromFileName(String fileName) {
        File fXmlFile = new File(fileName);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
            return dBuilder.parse(fXmlFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static List<Integer> getRecipeIds(Document doc) {
        Element root = doc.getDocumentElement();
        NodeList recipeInfo = root.getElementsByTagName("RecipeInfo");
        ArrayList<Integer> recipeIds = new ArrayList<Integer>();

        // get a series of acceptable recipe IDs from BigOven
        for (int i = 0; i < recipeInfo.getLength(); i++) {
            Element recipe = (Element) recipeInfo.item(i);

            NodeList list = recipe.getElementsByTagName("StarRating");
            Node node = list.item(0);
            double rating = Double.parseDouble(node.getFirstChild().getNodeValue());

            list = recipe.getElementsByTagName("ReviewCount");
            node = list.item(0);
            int reviewCount = Integer.parseInt(node.getFirstChild().getNodeValue());

            // if the recipe is good enough
            if(rating > MIN_REVIEW && reviewCount > MIN_REVIEW_COUNT) {
                list = recipe.getElementsByTagName("RecipeID");
                node = list.item(0);
                int id = Integer.parseInt(node.getFirstChild().getNodeValue());
                recipeIds.add(id);
            }
        }

        return recipeIds;
    }

    private static Recipe createRecipeFromDocument(Document doc) {
        Recipe recipe = new Recipe();
        Element root = doc.getDocumentElement();

        setupRecipeBasics(root, recipe);
        setupIngredients(root, recipe);
        setupRecipeCuisinesAndBases(root, recipe);
        setupToolsAndTechniques(recipe);

        return recipe;
    }

    private static Document makeHTTPRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();

            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line + "\n");
            }
            rd.close();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(result.toString())));
            doc.getDocumentElement().normalize();

            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Recipe setupRecipeBasics(Element recipeElement, Recipe recipe) {
        NodeList list;
        Node node;

        try {
            list = recipeElement.getElementsByTagName(NAME);
            node = list.item(0);

            recipe.setName(node.getFirstChild().getNodeValue());
        } catch (Exception e) {
            System.err.println("Missing name on recipe: " + recipe.getName());
        }

        try {
            list = recipeElement.getElementsByTagName(BIG_OVEN_ID);
            node = list.item(0);

            int bigOvenId = Integer.parseInt(node.getFirstChild().getNodeValue());
            recipe.setBigOvenId(bigOvenId);
        } catch (Exception e) {
            System.err.println("Missing BigOven ID on recipe: " + recipe.getName());
        }

        try {
            list = recipeElement.getElementsByTagName(IMAGE_URL);
            node = list.item(0);

            recipe.setImageURL(node.getFirstChild().getNodeValue());
        } catch (Exception e) {
            System.err.println("Missing image URL on recipe: " + recipe.getName());
        }

        int totalTime = 0;

        try {
            list = recipeElement.getElementsByTagName(TOTAL_TIME);
            node = list.item(0);
            totalTime = Integer.parseInt(node.getFirstChild().getNodeValue());
        } catch(Exception e) {
            System.err.println("Missing total time on recipe: " + recipe.getName());
        }

        int prepTime = 0;

        try {
            list = recipeElement.getElementsByTagName(PREP_TIME);
            node = list.item(0);
            prepTime = Integer.parseInt(node.getFirstChild().getNodeValue());
        } catch(Exception e) {
            System.err.println("Missing prep time on recipe: " + recipe.getName());
        }

        recipe.setCookTime(totalTime - prepTime);
        recipe.setPrepTime(prepTime);

        try {
            list = recipeElement.getElementsByTagName(INSTRUCTIONS);
            node = list.item(0);
            recipe.setInstructions(node.getFirstChild().getNodeValue());
        } catch (Exception e) {
            System.err.println("Missing instructions on recipe: " + recipe.getName());
        }

        return recipe;
    }

    private static Recipe setupIngredients(Element recipeElement, Recipe recipe) {
        NodeList ingredientNodeList = recipeElement.getElementsByTagName(INGREDIENTS);
        Element element = (Element) ingredientNodeList.item(0);
        NodeList ingredients = element.getElementsByTagName(INGREDIENT);

        for (int i = 0; i < ingredients.getLength(); i++) {
            String name = "";
            String unit = "";
            double quantity = 0.0;

            Element ingElement = (Element) ingredients.item(i);
            NodeList list;

            try {
                list = ingElement.getElementsByTagName(INGREDIENT_NAME);
                name = list.item(0).getFirstChild().getNodeValue();
            } catch (Exception e) {
                System.err.println("Missing ingredient name on recipe: " + recipe.getName());
            }

            try {
                list = ingElement.getElementsByTagName(INGREDIENT_QUANTITY);
                String quantityString = list.item(0).getFirstChild().getNodeValue();
                quantity = Double.parseDouble(quantityString);
            } catch (Exception e) {
                System.err.println("Missing ingredient quantity on recipe: " + recipe.getName());
            }

            try {
                list = ingElement.getElementsByTagName(INGREDIENT_UNIT);
                unit = list.item(0).getFirstChild().getNodeValue();
            } catch (Exception e) {
                System.err.println("Missing ingredient unit on recipe: " + recipe.getName());
            }

            Ingredient ingredient = new Ingredient(name, unit, quantity);
            recipe.addIngredient(ingredient);
        }

        return recipe;
    }

    private static Recipe setupRecipeCuisinesAndBases(Element recipeElement, Recipe recipe) {
        NodeList list;
        Node node;

        try {
            list = recipeElement.getElementsByTagName(MEAL_BASE);
            node = list.item(0);

            recipe.addBase(node.getFirstChild().getNodeValue());
        } catch (Exception e) {
            System.err.println("Missing meal base on recipe: " + recipe.getName());
        }

        for(String s : CUISINE) {
            try {
                list = recipeElement.getElementsByTagName(s);
                node = list.item(0);

                recipe.addCuisine(node.getFirstChild().getNodeValue());
            } catch (Exception e) {
                System.err.println("Missing cuisine on recipe: " + recipe.getName());
            }
        }

        return recipe;
    }

    private static Recipe setupToolsAndTechniques(Recipe recipe) {
        String instructions = recipe.getInstructions().toLowerCase();

        for(String tool : TOOLS_LIST) {
            if(instructions.contains(tool))
                recipe.addTool(new Tool(tool));
        }

        for(String technique : TECHNIQUES_LIST) {
            if(instructions.contains(technique))
                recipe.addTechnique(new Technique(technique));
        }

        return recipe;
    }
}
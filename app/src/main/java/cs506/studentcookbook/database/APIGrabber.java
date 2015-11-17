package cs506.studentcookbook.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import cs506.studentcookbook.model.Ingredient;
import cs506.studentcookbook.model.Recipe;
import cs506.studentcookbook.model.Technique;
import cs506.studentcookbook.model.Tool;

/**
 * This class is used to automatically populate the database with recipes from the BigOven API.
 * Call the getRecipes(String keyword) method with a keyword from POPULATION_KEYWORDS to get a
 * list of Recipe objects containing data from the API.
 */
public class APIGrabber {

    public static final String[] TOOLS_LIST = {
            "skewer",
            "grill",
            "oven",
            "pot",
            "saucepan",
            "dish",
            "skillet",
            "microwave",
            "foil",
            "food processor",
            "barbecue",
            "frying pan",
            "baking pan",
            "pan",
            "bowl"
    };
    public static final String[] TOOLS_DESCRIPTION = {
            "A long piece of wood or metal used for holding pieces of food, typically meat, together during cooking.",
            "A metal framework used for cooking food over an open fire",
            "An enclosed compartment, as in a kitchen range, for cooking and heating food",
            "A container, typically rounded or cylindrical and of ceramic ware or metal, used for storage or cooking.",
            "A deep cooking pan, typically round, made of metal, and with one long handle and a lid.",
            "A usually ceramic dish in which items can be baked.",
            "A small metal cooking pot with a long handle, typically having legs. A frying pan.",
            "An oven that uses microwaves to cook or heat food.",
            "Foil made of aluminum or a similar silvery-gray metal, used especially for covering or wrapping food.",
            "An electric kitchen appliance used for chopping, mixing, or pureeing foods.",
            "A metal framework used for cooking food over an open fire",
            "A small metal cooking pot with a long handle, typically having legs.",
            "A usually ceramic dish in which items can be baked.",
            "A small metal cooking pot with a long handle, typically having legs. A frying pan.",
            "A round, deep dish or basin used for food or liquid."
    };
    public static final String[] TOOLS_URLS = {
            "http://www.eatingutensils.net/images/27/skewer-1.jpg",
            "http://ll-us-i5.wal.co/dfw/4ff9c6c9-d698/k2-_95c5bb53-1127-4d32-9c22-afa90766a86d.v1.jpg-0d059a68eccb7b239d1a0986322d6b362f1b9473-optim-180x180.jpg",
            "https://www.ameriproappliancerepair.com/wp-content/uploads/2012/05/Oven-repair-maintenance.jpg",
            "http://ecx.images-amazon.com/images/I/3113c7uDvAL.jpg",
            "http://tesco.scene7.com/is/image/tesco/212-9074_PI_TPS1137694?wid=493&ht=538",
            "http://simplygluten-free.com/giveaways/files/2011/08/Baking-Dish-350-web.jpg",
            "http://cdn.theawl.com/wp-content/uploads/2011/02/skillet.jpg",
            "http://www.avartawellness.com/wp-content/uploads/2014/08/microwave.png",
            "http://www.momgoesgreen.com/wp-content//alum-foil.jpg",
            "http://ll-us-i5.wal.co/dfw/dce07b8c-10fb/k2-_a3b46780-0f3d-439b-81dc-43e2a85f12c9.v1.jpg-7a45182d29d5b01299771abdf07e2f4f78e08acd-optim-450x450.jpg",
            "http://ll-us-i5.wal.co/dfw/4ff9c6c9-d698/k2-_95c5bb53-1127-4d32-9c22-afa90766a86d.v1.jpg-0d059a68eccb7b239d1a0986322d6b362f1b9473-optim-180x180.jpg",
            "http://cdn.theawl.com/wp-content/uploads/2011/02/skillet.jpg",
            "http://simplygluten-free.com/giveaways/files/2011/08/Baking-Dish-350-web.jpg",
            "http://cdn.theawl.com/wp-content/uploads/2011/02/skillet.jpg",
            "http://lgcdn.everythingkitchens.com/809F1B/mage/media/catalog/product/cache/1/image/496x/9df78eab33525d08d6e5fb8d27136e95/r/s/rsvp-endurance-mixing-bowl-popup.jpg"
    };
    public static final String[] TECHNIQUES_LIST = {
            "marinate",
            "grill",
            "boil",
            "bake",
            "whisk",
            "fry",
            "simmer",
            "cut",
            "barbecue"
    };
    public static final String[] TECHNIQUES_DESCRIPTION = {
            "Soak (meat, fish, or other food) in a marinade sauce, often in a plastic bag.",
            "To cook (food) on a metal frame over fire. To fry or toast (something, such as a sandwich) on a hot surface.",
            "Heating up water in a pan on the stovetop to its boiling point, where it begins to bubble.",
            "Cook (food) by dry heat without direct exposure to a flame, typically in an oven or on a hot surface.",
            "Beat or stir (a substance, especially cream or eggs) with a light, rapid movemen",
            "Cook (food) in hot fat or oil, typically in a shallow pan.",
            "(of water or food) stay just below the boiling point while being heated.",
            "Use a knife to cut food into smaller pieces.",
            "To cook (food) on a metal frame over fire. To fry or toast (something, such as a sandwich) on a hot surface.",
    };
    public static final String[] TECHNIQUES_IMAGE_URLS = {
            "http://www.filipino-food-lovers.com/site-images/pinoy_bbq/pinoy_bbq_marinate_before.jpg",
            "http://www.grill.com/wp-content/uploads/2011/08/grill-grills.jpg",
            "http://www.seriouseats.com/images/20100813-boiling-water-primary.jpg",
            "http://pad3.whstatic.com/images/thumb/e/e4/Bake-a-Ham-Step-8.jpg/670px-Bake-a-Ham-Step-8.jpg",
            "http://hamodia.com/hamod-uploads/2013/04/whisking.jpg",
            "https://theicook.files.wordpress.com/2012/09/dsc00925.jpg?w=614&h=461",
            "http://www.chefdoughty.com/blog/wp-content/uploads/2010/12/simmer-with-water.jpg",
            "http://www.feedyoursoul2.com/wp-content/uploads/2013/10/Slices-5001-500x300.jpg",
            "To cook (food) on a metal frame over fire. To fry or toast (something, such as a sandwich) on a hot surface.",
    };
    public static final String[] TECHNIQUES_HELP_URLS = {
            "http://www.ehow.com/how_17347_make-basic-marinade.html",
            "http://www.ehow.com/how_2084912_use-grill.html",
            "http://www.ehow.com/how_2295_boil-water.html",
            "http://www.ehow.com/how_7931956_use-electric-oven.html",
            "http://www.ehow.com/how_10015423_whisk-flour.html",
            "http://www.ehow.com/how_2891_stir-fry-anything.html",
            "http://www.ehow.com/how_8274234_simmer-soup.html",
            "http://www.ehow.com/list_6495238_kitchen-cutting-tools.html",
            "http://www.ehow.com/how_2084912_use-grill.html"
    };

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
            "snapper", "shellfish", "egg", "honey", "tofu", "soy", "cereal", "pasta",
            "bread", "rice", "oatmeal", "beans", "lentils", "peas", "potato", "corn",
            "artichoke", "asparagus", "beet", "broccoli", "carrot", "sprouts",
            "celery", "garlic", "onion", "pepper", "mushroom", "tomato", "eggplant",
            "avocado", "peanut", "walnut", "pecan", "milk", "cheese", "yogurt",
            "apple", "banana", "grape", "strawberry", "raspberry", "blueberry",
            "mediterranean", "paleo", "vegetarian", "vegan", "italian", "chinese",
            "japanese", "sushi", "hispanic", "american", "submarine", "sandwich",
            "spicy", "indian", "israeli", "thai", "german", "russian", "middle-eastern",
            "breakfast", "lunch", "dinner", "snack", "dessert", "smoothie", "fish"};

    public static final String[] SIMPLE_POPULATION_KEYWORDS = {"salmon", "tuna", "tilapia"};

    public static final String[] CORRECTED_SYNONYMS_DATA = {"chicken", "beef", "pizza", "pasta",
    "pork", "turkey", "salmon", "egg", "asian", "dessert", "marinade", "meat", "bread", "bbq", "seafood"};
    public static List<String> CORRECTED_SYNONYMS;

    public static final String[] EXCLUDED_WORDS_DATA = {"main dish", "other", "main dish - other", "side dish", "other - misc"};
    public static List<String> EXCLUDED_WORDS;

    private static final String API_KEY = "3h61BCUOSbbRbYq29wkD0gz6gcKItdRR";
    private static final String RECIPE_URL = "http://api.bigoven.com/recipe/";
    private static final String SEARCH_URL = "http://api.bigoven.com/recipes";

    private static final String PARAM_TITLE_KEYWORD = "any_kw=";
    private static final String PARAM_API_KEY = "api_key=";
    private static final String PARAM_PAGE = "pg=";
    private static final String PARAM_RESULTS_PER_PAGE = "rpp=";

    private static final double MIN_REVIEW = 2.0;
    private static final int MIN_REVIEW_COUNT = 1;
    private static final int MAX_INGREDIENTS = 6;
    private static final int MAX_PAGE = 2;
    private static final int RESULTS_PER_PAGE = 25;


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
        if(CORRECTED_SYNONYMS == null) {
            CORRECTED_SYNONYMS = Arrays.asList(CORRECTED_SYNONYMS_DATA);
        }
        if(EXCLUDED_WORDS == null) {
            EXCLUDED_WORDS = Arrays.asList(EXCLUDED_WORDS_DATA);
        }

        List<Recipe> list = getRecipesFromAPIBasedOnKeywordAndPage(keyword, 1, RESULTS_PER_PAGE);

        for(int i = 2; i <= MAX_PAGE; i++) {
            System.out.println("Grabbing new page...");
            list.addAll(getRecipesFromAPIBasedOnKeywordAndPage(keyword, i, RESULTS_PER_PAGE));
        }

        return list;
    }

    private static List<Recipe> getRecipesFromAPIBasedOnKeywordAndPage(String keyword, int page, int resultsPerPage) {
        Document results = APIGrabber.getAPIRecipeSearchResults(keyword, Integer.toString(page), Integer.toString(resultsPerPage));
        List<Integer> idList = APIGrabber.getRecipeIds(results);

        List<Recipe> recipes = new LinkedList<Recipe>();
        for(Integer id : idList) {
            Document recipeResults = getAPIRecipe(id);
            if(recipeResults != null) {
                Recipe recipe = createRecipeFromDocument(recipeResults);

                if (recipe != null) {
                    recipes.add(recipe);
                    System.out.println("Added new recipe...");
                }
            }
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
        System.out.println("Grabbing new recipe...");
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
                System.out.println("Found potential recipe...");
            }
        }

        return recipeIds;
    }

    private static Recipe createRecipeFromDocument(Document doc) {
        Recipe recipe = new Recipe();
        Element root = doc.getDocumentElement();

        setupIngredients(root, recipe);
        if(recipe == null || recipe.getIngredients().size() > MAX_INGREDIENTS) {
            return null;
        }

        setupRecipeBasics(root, recipe);
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
            //System.err.println("Missing name on recipe: " + recipe.getName());
        }

        try {
            list = recipeElement.getElementsByTagName(BIG_OVEN_ID);
            node = list.item(0);

            int bigOvenId = Integer.parseInt(node.getFirstChild().getNodeValue());
            recipe.setBigOvenId(bigOvenId);
        } catch (Exception e) {
            //System.err.println("Missing BigOven ID on recipe: " + recipe.getName());
        }

        try {
            list = recipeElement.getElementsByTagName(IMAGE_URL);
            node = list.item(0);

            recipe.setImageURL(node.getFirstChild().getNodeValue());
        } catch (Exception e) {
            //System.err.println("Missing image URL on recipe: " + recipe.getName());
        }

        int totalTime = 0;

        try {
            list = recipeElement.getElementsByTagName(TOTAL_TIME);
            node = list.item(0);
            totalTime = Integer.parseInt(node.getFirstChild().getNodeValue());
        } catch(Exception e) {
            //System.err.println("Missing total time on recipe: " + recipe.getName());
        }

        int prepTime = 0;

        try {
            list = recipeElement.getElementsByTagName(PREP_TIME);
            node = list.item(0);
            prepTime = Integer.parseInt(node.getFirstChild().getNodeValue());
        } catch(Exception e) {
            //System.err.println("Missing prep time on recipe: " + recipe.getName());
        }

        recipe.setCookTime(totalTime - prepTime);
        recipe.setPrepTime(prepTime);

        try {
            list = recipeElement.getElementsByTagName(INSTRUCTIONS);
            node = list.item(0);
            recipe.setInstructions(node.getFirstChild().getNodeValue());
        } catch (Exception e) {
            //System.err.println("Missing instructions on recipe: " + recipe.getName());
        }

        return recipe;
    }

    private static Recipe setupIngredients(Element recipeElement, Recipe recipe) {
        NodeList ingredientNodeList = recipeElement.getElementsByTagName(INGREDIENTS);
        Element element = (Element) ingredientNodeList.item(0);

        if(element == null) {
            recipe = null;
            return null;
        }

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
                //System.err.println("Missing ingredient name on recipe: " + recipe.getName());
            }

            try {
                list = ingElement.getElementsByTagName(INGREDIENT_QUANTITY);
                String quantityString = list.item(0).getFirstChild().getNodeValue();
                quantity = Double.parseDouble(quantityString);
            } catch (Exception e) {
                //System.err.println("Missing ingredient quantity on recipe: " + recipe.getName());
            }

            try {
                list = ingElement.getElementsByTagName(INGREDIENT_UNIT);
                unit = list.item(0).getFirstChild().getNodeValue();
            } catch (Exception e) {
                //System.err.println("Missing ingredient unit on recipe: " + recipe.getName());
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

            String base = node.getFirstChild().getNodeValue();
            base = base.toLowerCase();

            if(!EXCLUDED_WORDS.contains(base)) {

                for(int i = 0; i < CORRECTED_SYNONYMS_DATA.length; i++) {
                    String current = CORRECTED_SYNONYMS_DATA[i];
                    if(base.contains(current)) {
                        base = current;
                        break;
                    }
                }

                recipe.addBase(base);
            }
        } catch (Exception e) {
            //System.err.println("Missing meal base on recipe: " + recipe.getName());
        }

        for(String s : CUISINE) {
            try {
                list = recipeElement.getElementsByTagName(s);
                node = list.item(0);

                String cuisine = node.getFirstChild().getNodeValue();
                cuisine = cuisine.toLowerCase();

                if(!EXCLUDED_WORDS.contains(cuisine)) {

                    for (int i = 0; i < CORRECTED_SYNONYMS_DATA.length; i++) {
                        String current = CORRECTED_SYNONYMS_DATA[i];
                        if (cuisine.contains(current)) {
                            cuisine = current;
                            break;
                        }
                    }

                    recipe.addCuisine(cuisine);
                }

            } catch (Exception e) {
                //System.err.println("Missing cuisine on recipe: " + recipe.getName());
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
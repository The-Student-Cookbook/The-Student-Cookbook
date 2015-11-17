package cs506.studentcookbook;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.both;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.everyItem;
import static org.junit.matchers.JUnitMatchers.hasItems;

import cs506.studentcookbook.model.Preferences;
import cs506.studentcookbook.model.Ingredient;

import static org.junit.Assert.assertEquals;


public class PreferencesUnitTest {

    public Preferences preferences;

    @Before
    public void setup() {
        preferences = new Preferences();
    }

    @Test
    public void testPreferences() throws Exception {
         List<String> likedCuisines = new ArrayList<String>();
         List<String> dislikedCuisines = new ArrayList<String>();
         List<String> likedBases = new ArrayList<String>();
         List<String> dislikedBases = new ArrayList<String>();
         List<Ingredient> ingredients = new ArrayList<Ingredient>();

        likedCuisines.add("likedcuisines");
        dislikedCuisines.add("dislikedcuisines");
        likedBases.add("likedbases");
        dislikedBases.add("dislikedbases");

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

        preferences.setName("prefname");
        preferences.setPrepTime(10);
        preferences.setCookTime(20);
        preferences.setGroupSize(4);
        preferences.setIngredients(ingredients1);

        assertEquals(preferences.getName(), "prefname");
        assertEquals(preferences.getPrepTime(),10);
        assertEquals(preferences.getCookTime(),20);
        assertEquals(preferences.getGroupSize(),4);
        assertEquals(preferences.getIngredients(),ingredients1);
    }
}
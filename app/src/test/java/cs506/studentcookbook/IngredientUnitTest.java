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

import cs506.studentcookbook.model.Ingredient;

import static org.junit.Assert.assertEquals;


public class IngredientUnitTest {

    public Ingredient ingredient;

    @Before
    public void setup() {
        ingredient = new Ingredient();
    }

    @Test
    public void testIngredient() throws Exception {
        ingredient.setName("ingredient1-1");
        ingredient.setAmount(1);
        ingredient.setUnit("ingredient1-1:unit");

        assert(ingredient.getAmount() == 1);
        assertEquals(ingredient.getName(),"ingredient1-1");
        assertEquals(ingredient.getUnit(),"ingredient1-1:unit");
    }
}
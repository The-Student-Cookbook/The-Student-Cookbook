package cs506.studentcookbook;

import org.junit.Before;
import org.junit.Test;

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

import cs506.studentcookbook.model.Recipe;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class RecipeUnitTest {

    public Recipe r;

    @Before
    public void setup() {
        r = new Recipe();
    }

    @Test
    public void cuisine() throws Exception {
        String c = "       ChicKen                       ";
        String cshort = "chicken";

        assertEquals(r.getCuisines().size(), 0);

        r.addCuisine(c);
        assertEquals(r.getCuisines().size(), 1);
        assertThat(r.getCuisines(), hasItems(cshort));

        r.addCuisine(null);
        assertEquals(r.getCuisines().size(), 1);
    }

    @Test
    public void base() throws Exception {
        String b = "  BEEf                 ";
        String bshort = "beef";

        assertEquals(r.getBases().size(), 0);

        r.addBase(b);
        assertEquals(r.getBases().size(), 1);
        assertThat(r.getBases(), hasItems(bshort));

        r.addCuisine(null);
        assertEquals(r.getBases().size(), 1);
    }
}
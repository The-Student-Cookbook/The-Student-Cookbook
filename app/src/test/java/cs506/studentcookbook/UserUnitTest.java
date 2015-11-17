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

import cs506.studentcookbook.model.Tool;
import cs506.studentcookbook.model.User;

import static org.junit.Assert.assertEquals;


public class UserUnitTest {

    public User user;

    @Before
    public void setup() {
        user = new User();
    }

    @Test
    public void testUser() throws Exception {
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

        List<String> bases = new ArrayList<String>();
                bases.add("base1");

        assertEquals(user.getId(), 0);
        assertEquals(user.getEmail(),"user:email");
        assertEquals(user.getCountRecipesDisliked(),1);
        assertEquals(user.getCountRecipesLiked(),1);
        assertEquals(user.getGroupSize(),1);
        assertEquals(user.getPrepTime(),5);
        assertEquals(user.getCookTime(),20);
        assertEquals(user.getEstimateCost(),3);
        assertEquals(user.getAllergicBases(),bases);
        //assert(user.getTools().equals(tools1));

    }
}
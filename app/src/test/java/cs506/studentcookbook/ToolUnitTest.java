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

import static org.junit.Assert.assertEquals;


public class ToolUnitTest {

    public Tool tool;

    @Before
    public void setup() {
        tool = new Tool();
    }

    @Test
    public void testTool() throws Exception {
        tool.setImageURL("tool11:url");
        tool.setName("tool11");
        tool.setDescription("tool11:description");

        assertEquals(tool.getImageURL(), "tool11:url");
        assertEquals(tool.getName(),"tool11");
        assertEquals(tool.getDescription(),"tool11:description");
    }
}
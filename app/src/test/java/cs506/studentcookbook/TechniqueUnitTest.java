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
import cs506.studentcookbook.model.Technique;

import static org.junit.Assert.assertEquals;


public class TechniqueUnitTest {

    public Technique technique;

    @Before
    public void setup() {
        technique = new Technique();
    }

    @Test
    public void testTechnique() throws Exception {
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

        List<String> urls = new ArrayList<String>();
        urls.add("url1");
        urls.add("url2");

        Technique technique11 = new Technique();
        technique11.setDescription("technique11:description");
        technique11.setName("technique11");
        technique11.setImageURL("technique11:imageURL");
        technique11.setExternalURLs(urls);
        technique11.setTools(tools1);

        assertEquals(technique11.getName(), "technique11");
        assertEquals(technique11.getDescription(),"technique11:description");
        assertEquals(technique11.getImageURL(),"technique11:imageURL");
        assertEquals(technique11.getExternalURLs(),urls);
        //assertEquals(technique11.getTools(),tools1);
    }
}
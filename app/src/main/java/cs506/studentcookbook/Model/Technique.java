package cs506.studentcookbook.Model;

import java.util.ArrayList;
import java.util.List;

public class Technique {

    private String name;
    private String description;
    private List<Tool> tools;
    private List<String> externalURLs;

    public Technique() {
        createLists();
    }

    public Technique(String name) {
        if(name != null)
            this.name = name.toLowerCase();

        populateFromDatabase();
    }

    private void populateFromDatabase() {
        // TODO make this grab the description, tools, and externalURLs from the database
    }

    private void createLists() {
        tools = new ArrayList<Tool>();
        externalURLs = new ArrayList<String>();
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public List<Tool> getTools() {
        return tools;
    }

    public List<String> getExternalURLs() {
        return externalURLs;
    }

    public void setName(String name) {
        if(name == null)
            return;

        name = name.toLowerCase();

        if(!this.name.equals(name)) {
            this.name = name;
            populateFromDatabase();
        }
    }

    public void setDescription(String description) {
        this.name = description;
    }

    public void setTools(List<Tool> tools) {
        this.tools = tools;
    }

    public void setExternalURLs(List<String> urls) {
        this.externalURLs = urls;
    }

    public void addTool(Tool tool) {
        this.tools.add(tool);
    }

    public void addExternalURL(String url) {
        this.externalURLs.add(url);
    }

    public String toString() {
        return this.name;
    }
}
package cs506.studentcookbook.model;

import java.util.ArrayList;
import java.util.List;
import cs506.studentcookbook.database.DBTools;

//Encapsulates information for user settings
public class User
{
    private int id;
    private String email;
    private int countRecipesLiked;
    private int countRecipesDisliked;
    private int groupSize;
    private int prepTime;
    private int cookTime;
    private int estimateCost;
    private List<String> allergicBases;
    private List<Tool> tools;

    public User() {
        allergicBases = new ArrayList<String>();
        tools = new ArrayList<Tool>();
    }

//    public void getSettings() {
//        //Pull settings from DB
//        List<String> settings = DBTools.getInstance(DBTools.getContext()).getUserSettings();
//        allergicBases = DBTools.getInstance(DBTools.getContext()).getAllergicBases();
//        tools = DBTools.getInstance(DBTools.getContext()).getTools();
//
//        //Tries to set values to each field. This can throw an exception if
//        id = Integer.parseInt(settings.get(0));
//        email = settings.get(0);
//        countRecipesLiked = Integer.parseInt(settings.get(0));
//        countRecipesDisliked = Integer.parseInt(settings.get(0));
//        groupSize = Integer.parseInt(settings.get(0));
//        prepTime = Integer.parseInt(settings.get(0));
//        cookTime = Integer.parseInt(settings.get(0));
//        estimateCost = Integer.parseInt(settings.get(0));
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCountRecipesLiked() {
        return countRecipesLiked;
    }

    public void setCountRecipesLiked(int countRecipesLiked) {
        this.countRecipesLiked = countRecipesLiked;

    }

    public int getCountRecipesDisliked() {
        return countRecipesDisliked;
    }

    public void setCountRecipesDisliked(int countRecipesDisliked) {
        this.countRecipesDisliked = countRecipesDisliked;
    }

    public int getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(int groupSize) {
        this.groupSize = groupSize;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public int getEstimateCost() {
        return estimateCost;
    }

    public void setEstimateCost(int estimateCost) {
        this.estimateCost = estimateCost;
    }

    public List<String> getAllergicBases() {
        return allergicBases;
    }

    public void addAllergicBase(String allergicBase) {
        this.allergicBases.add(allergicBase);
    }

    public void removeAllergicBase(String allergicBase) {
        this.allergicBases.remove(allergicBase);
    }

    public void setAllergicBases(List<String> allergicBases) {
        this.allergicBases = allergicBases;
    }

    public List<Tool> getTools() {
        return tools;
    }

    public void addTool(Tool tool) {
        tools.add(tool);
    }

    public void removeTool(Tool tool) {
        tools.remove(tool);
    }

    public void setTools(List<Tool> tools) {
        this.tools = tools;
    }

    public boolean equals (User user) {
        //Written like this to make debugging easier
        if (id != user.getId())
            return false;
        else if (!email.equals(user.getEmail()))
            return false;
        else if (countRecipesLiked != user.getCountRecipesLiked())
            return false;
        else if (countRecipesDisliked != user.getCountRecipesDisliked())
            return false;
        else if (groupSize != user.getGroupSize())
            return false;
        else if (prepTime != user.getPrepTime())
            return false;
        else if (cookTime != user.getCookTime())
            return false;
        else if (estimateCost != user.getEstimateCost())
            return false;
        else if (!allergicBases.equals(user.getAllergicBases()))
            return false;
        else if (!compareListOfTools(tools, user.getTools()))
            return false;
        else
            return true;
    }

    //Used for debugging with equals() method
    private boolean compareListOfTools(List<Tool> tools1, List<Tool> tools2) {
        //Check same size..
        if (tools1.size() != tools2.size())
            return false;

        //Compare each tool element in entire list of tools
        for (int i = 0; i < tools1.size(); i++)
        {
            Tool tool1 = tools1.get(i);
            Tool tool2 = tools2.get(i);

            if (!tool1.equals(tool2))
                return false;
        }

        //Since all are equal, lists are equal
        return true;
    }
}

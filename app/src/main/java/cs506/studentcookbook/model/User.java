package cs506.studentcookbook.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import cs506.studentcookbook.model.Tool;

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
		//Pull settings from DB
		List<String> settings = DBTools.getInstance(DBTools.getContext()).getUserSettings();
		allergicBases = DBTools.getInstance(DBTools.getContext()).getAllergicBases();
		tools = DBTools.getInstance(DBTools.getContext()).getTools();
		
		//Tries to set values to each field. This can throw an exception if
		id = Integer.parseInt(settings.get(0));
		email = settings.get(0);
		countRecipesLiked = Integer.parseInt(settings.get(0));
		countRecipesDisliked = Integer.parseInt(settings.get(0));
		groupSize = Integer.parseInt(settings.get(0));
		prepTime = Integer.parseInt(settings.get(0));
		cookTime = Integer.parseInt(settings.get(0));
		estimateCost = Integer.parseInt(settings.get(0));
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
		
		DBTools.getInstance(DBTools.getContext()).setUserSetting("userId", id);
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
		
		DBTools.getInstance(DBTools.getContext()).setUserSetting("email", email);
	}
	
	public int getCountRecipesLiked() {
		return countRecipesLiked;
	}
	
	public void setCountRecipesLiked(int countRecipesLiked) {
		this.countRecipesLiked = countRecipesLiked;
		
		DBTools.getInstance(DBTools.getContext()).setUserSetting("countRecipesLiked", countRecipesLiked);
	}
	
	public int getCountRecipesDisliked() {
		return countRecipesDisliked;
	}
	
	public void setCountRecipesDisliked(int countRecipesDisliked) {
		this.countRecipesDisliked = countRecipesDisliked;
		
		DBTools.getInstance(DBTools.getContext()).setUserSetting("countRecipesDisliked", countRecipesDisliked);
	}
	
	public int getGroupSize() {
		return groupSize;
	}
	
	public void setGroupSize(int groupSize) {
		this.groupSize = groupSize;
		
		DBTools.getInstance(DBTools.getContext()).setUserSetting("groupSize", groupSize);
	}
	
	public int getPrepTime() {
		return prepTime;
	}
	
	public void setPrepTime(int prepTime) {
		this.prepTime = prepTime;
		
		DBTools.getInstance(DBTools.getContext()).setUserSetting("prepTime", prepTime);
	}
	
	public int getCookTime() {
		return cookTime;
	}
	
	public void setCookTime(int cookTime) {
		this.cookTime = cookTime;
		
		DBTools.getInstance(DBTools.getContext()).setUserSetting("cookTime", cookTime);
	}
	
	public int getEstimateCost() {
		return estimateCost;
	}
	
	public void setEstimateCost(int estimateCost) {
		this.estimateCost = estimateCost;
		
		DBTools.getInstance(DBTools.getContext()).setUserSetting("estimateCost", estimateCost);
	}
	
	public List<String> getAllergicBases() {
		return allergicBases;
	}
	
	public void addAllergicBase(String allergicBase) {
		this.allergicBases.add(allergicBase);
		
		DBTools.getInstance(DBTools.getContext()).addAllergicBase(allergicBase);
	}

	public void removeAllergicBase(String allergicBase) {
		this.allergicBases.remove(allergicBase);

		DBTools.getInstance(DBTools.getContext()).removeAllergicBase(allergicBase);
	}
	
	public List<Tool> getTools() {
		return tools;
	}
	
	public void addTool(Tool tool) {
		tools.add(tool);
		
		DBTools.getInstance(DBTools.getContext()).addTool(tool);
	}
	
	public void removeTool(Tool tool) {
		tools.remove(tool);
		
		DBTools.getInstance(DBTools.getContext()).removeTool(tool);
	}
}

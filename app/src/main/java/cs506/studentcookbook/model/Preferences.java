package cs506.studentcookbook.model;

import java.util.ArrayList;
import java.util.List;

public class Preferences {
	private String name;
	private int prepTime;
	private int cookTime;
	private int groupSize;
	private List<String> likedCuisines;
	private List<String> dislikedCuisines;
	private List<String> likedBases;
	private List<String> dislikedBases;
	private List<Ingredient> ingredients;

	public void Preferences(){
		setupObjects();
	}
	
	private void setupObjects(){
		likedCuisines = new ArrayList<String>();
		dislikedCuisines = new ArrayList<String>();
		likedBases = new ArrayList<String>();
		dislikedBases = new ArrayList<String>();
		ingredients = new ArrayList<Ingredient>();
	}

	public String getName() { return name; }

	public int getPrepTime(){
		return prepTime;
	}
	
	public int getCookTime(){
		return cookTime;
	}
	
	public int getGroupSize(){
		return groupSize;
	}
	
	public List<String> getLikedCuisines(){
		return likedCuisines;
	}
	
	public List<String> getDislikedCuisines(){
		return dislikedCuisines;
	}
	
	public List<String> getLikedBases(){
		return likedBases;
	}
	
	public List<String> getDislikedBases(){
		return dislikedBases;
	}
	
	public List<Ingredient> getIngredients(){
		return ingredients;
	}

	public void setName(String name) { this.name = name; }

	public void setPrepTime(int prepTime){
		this.prepTime = prepTime;
	}
	
	public void setCookTime(int cookTime){
		this.cookTime = cookTime;
	}
	
	public void setGroupSize(int groupSize){
		this.groupSize = groupSize;
	}
	
	public void setLikedCuisines(List<String> likedCuisines){
		this.likedCuisines = likedCuisines;
	}
	
	public void addLikedCuisine(String cuisine){
		if(cuisine == null)
            return;

		cuisine = cuisine.toLowerCase().trim();
        this.likedCuisines.add(cuisine);
	}
	
	public void removeLikedCuisine(String cuisine){
		if(cuisine == null)
            return;

		cuisine = cuisine.toLowerCase().trim();
        this.likedCuisines.remove(cuisine);
	}
	
	public void setDislikedCuisines(List<String> dislikedCuisines){
		this.dislikedCuisines = dislikedCuisines;
	}
	
	public void addDislikedCuisine(String cuisine){
		if(cuisine == null)
            return;

		cuisine = cuisine.toLowerCase().trim();
        this.dislikedCuisines.add(cuisine);
	}
	
	public void removeDislikedCuisine(String cuisine){
		if(cuisine == null)
            return;

		cuisine = cuisine.toLowerCase().trim();
        this.dislikedCuisines.remove(cuisine);
	}
	
	public void setLikedBases(List<String> likedBases){
		this.likedBases = likedBases;
	}
	
	public void addLikedBase(String base){
		if(base == null)
            return;

		base = base.toLowerCase().trim();
        this.likedBases.add(base);
	}

	public void removeLikedBase(String base){
		if(base == null)
            return;

		base = base.toLowerCase().trim();
        this.likedBases.remove(base);
	}
		
	public void setDislikedBases(List<String> dislikedBases){
		this.dislikedBases = dislikedBases;
	}
	
	public void addDislikedBase(String base){
		if(base == null)
            return;

		base = base.toLowerCase().trim();
        this.dislikedBases.add(base);
	}
	
	public void removeDislikedBase(String base){
		if(base == null)
            return;

		base = base.toLowerCase().trim();
        this.dislikedBases.remove(base);
	}
	
	public void setIngredients(List<Ingredient> ingredients){
		this.ingredients = ingredients;
	}
	
	public void addIngredient(Ingredient ingredient){
		this.ingredients.add(ingredient);
	}
	
	public void removeIngredient(Ingredient ingredient){
		this.ingredients.remove(ingredient);
	}
}
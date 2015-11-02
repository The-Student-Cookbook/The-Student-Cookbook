package cs506.studentcookbook.activity;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cs506.studentcookbook.Model.Recipe;
import cs506.studentcookbook.R;

/**
 * Created by Derek on 11/1/2015.
 */
public class RecipeListViewAdapter extends BaseAdapter  {

    private List<Recipe> recipeList;
    private Context context;

    public RecipeListViewAdapter(Context c, List<Recipe> recipesToFormat){
        recipeList = recipesToFormat;
        context = c;
    }

    @Override
    public int getCount() {
        return recipeList.size();
    }

    @Override
    public Recipe getItem(int position) {
        return recipeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.recipe_listitem, null);

        TextView recipeTitle = (TextView) row.findViewById(R.id.recipe_listitem_name);
        recipeTitle.setText(recipeList.get(position).getName());

        TextView rating = (TextView) row.findViewById(R.id.recipe_listitem_rating);
        rating.setText(recipeList.get(position).getRating());

        return null;
    }



}

package cs506.studentcookbook.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import cs506.studentcookbook.R;
import cs506.studentcookbook.model.Model;

/**
 * Created by pgoetsch on 11/16/15.
 */
public class IngredientListViewAdapter extends ArrayAdapter<Model> {

    Model[] modelItems = null;
    Context context;
    public IngredientListViewAdapter(Context context, Model[] resource) {
        super(context, R.layout.ingredient_listitem,resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.modelItems = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.ingredient_listitem, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.ingredient_name);
        CheckBox cb = (CheckBox) convertView.findViewById(R.id.ingredient_checkbox);
        name.setText(modelItems[position].getName());
        if(modelItems[position].getValue() == 1)
            cb.setChecked(true);
        else
            cb.setChecked(false);
        return convertView;
    }


}

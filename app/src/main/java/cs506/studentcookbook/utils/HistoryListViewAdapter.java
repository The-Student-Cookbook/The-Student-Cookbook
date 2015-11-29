package cs506.studentcookbook.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cs506.studentcookbook.R;
import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.Recipe;

/**
 * Created by pgoetsch on 11/23/15.
 */
public class HistoryListViewAdapter extends BaseAdapter {

    private List<Recipe> history;
    private Context context;
    private DBTools tools;

    public HistoryListViewAdapter(Context context, List<Recipe> recipes) {
        history = recipes;
        this.context = context;
        tools = new DBTools(context);
    }

    @Override
    public int getCount() {
        return history.size();
    }

    @Override
    public Object getItem(int position) {
        return history.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.history_listitem, null);

        final Recipe current = history.get(position);

        TextView recipeTitle = (TextView) row.findViewById(R.id.history_listitem_name);
        recipeTitle.setText(current.getName());

        // grabs the image from the web and puts it in the image view
        new DownloadImageTask((ImageView) row.findViewById(R.id.history_listitem_icon)).execute(current.getImageURL());

        // hookup delete button
        Button deleteBtn = (Button) row.findViewById(R.id.history_listitem_delete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: need to remove this single recipe from hasCooked
            }
        });

        // hookup "like" button
        Button likeBtn = (Button) row.findViewById(R.id.history_listitem_rateUp);
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(context, "Recipe Liked", Toast.LENGTH_SHORT);
                toast.show();
                tools.addRecipeRating(current, true);
            }
        });

        // hookup "dislike" button
        Button dislikeBtn = (Button) row.findViewById(R.id.history_listitem_rateDown);
        dislikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(context, "Recipe Disliked", Toast.LENGTH_SHORT);
                toast.show();
                tools.addRecipeRating(current, false);
            }
        });

        return row;
    }
}

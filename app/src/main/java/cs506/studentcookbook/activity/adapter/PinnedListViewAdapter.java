package cs506.studentcookbook.activity.adapter;

import android.content.Context;
import android.util.Log;
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
import cs506.studentcookbook.utils.DownloadImageTask;

/**
 * Created by pgoetsch on 11/23/15.
 */
public class PinnedListViewAdapter extends BaseAdapter {

    private static final String TAG = "PinnedListViewAdapter";

    private List<Recipe> pinned;
    private Context context;
    private DBTools tools;

    public PinnedListViewAdapter(Context context, List<Recipe> recipes) {
        pinned = recipes;
        this.context = context;
        tools = new DBTools(context);
    }

    @Override
    public int getCount() {
        return pinned.size();
    }

    @Override
    public Object getItem(int position) {
        return pinned.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.pinned_listitem, null);

        final Recipe current = pinned.get(position);

        TextView recipeTitle = (TextView) row.findViewById(R.id.pinned_listitem_name);
        recipeTitle.setText(current.getName());

        // grabs the image from the web and puts it in the image view
        new DownloadImageTask((ImageView) row.findViewById(R.id.pinned_listitem_icon)).execute(current.getImageURL());

        // hookup delete button
        Button deleteBtn = (Button) row.findViewById(R.id.pinned_listitem_delete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "TODO: need to remove this single recipe from pinned and reload list");
            }
        });

        return row;
    }
}

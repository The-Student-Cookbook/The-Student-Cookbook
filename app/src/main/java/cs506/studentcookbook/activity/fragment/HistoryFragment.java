package cs506.studentcookbook.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

import cs506.studentcookbook.R;
import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.Preferences;
import cs506.studentcookbook.model.Recipe;
import cs506.studentcookbook.activity.adapter.HistoryListViewAdapter;

/**
 * Created by pgoetsch on 11/27/15.
 */
public class HistoryFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    private static final String TAG = "HistoryFragment";

    private DBTools tools;
    private int mPage;

    public static HistoryFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);
        tools = new DBTools(getContext());
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "onCreate getView(): " + getView());
        Log.d(TAG, "onCreate getActivity(): " + getActivity());

        // hide empty msg initially
        TextView emptyView = (TextView) getView().findViewById(R.id.empty_my_recipes);
        emptyView.setVisibility(View.INVISIBLE);

        List<Recipe> history = tools.getHasCooked();
        if(history == null || history.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        }

        Iterator<Recipe> itr = history.iterator();
        while(itr.hasNext()) {
            Recipe next = itr.next();

            boolean liked = tools.getRecipeLiked(next);
            String imageUrl = next.getImageURL();
            String title = next.getName();
        }

        // TODO: need following methods from backend
        // a way to set like/dislike status of a recipe
        // a way to delete a recipe from their history
        // the cook date for a history item


        ListView listView = (ListView) getActivity().findViewById(R.id.history_listview);
        HistoryListViewAdapter adapter = new HistoryListViewAdapter(getActivity(), history);

        listView.setAdapter(adapter);
    }
}

package cs506.studentcookbook.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cs506.studentcookbook.R;
import cs506.studentcookbook.activity.adapter.PinnedListViewAdapter;
import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.Preferences;
import cs506.studentcookbook.model.Recipe;
import cs506.studentcookbook.activity.adapter.HistoryListViewAdapter;

/**
 * Created by pgoetsch on 11/27/15.
 */
public class PinnedFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private DBTools tools;

    public static PinnedFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PinnedFragment fragment = new PinnedFragment();
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

        View view = inflater.inflate(R.layout.fragment_pinned, container, false);
        tools = new DBTools(getContext());
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // hide empty msg initially
        TextView emptyView = (TextView) getView().findViewById(R.id.empty_pinned);
        emptyView.setVisibility(View.INVISIBLE);

        List<Recipe> pinned = tools.getPinnedRecipes();
        if(pinned == null || pinned.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        }

        ListView listView = (ListView) getActivity().findViewById(R.id.pinned_listview);
        PinnedListViewAdapter adapter = new PinnedListViewAdapter(getActivity(), pinned);

        listView.setAdapter(adapter);
    }
}

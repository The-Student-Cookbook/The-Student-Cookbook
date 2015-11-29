package cs506.studentcookbook.activity.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import cs506.studentcookbook.activity.fragment.HistoryFragment;
import cs506.studentcookbook.activity.fragment.PinnedFragment;

public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Cooked Recipes", "Pinned Recipes"};
    private Context context;

    private static final String TAG = "FragmentPagerAdapter";

    public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem pos=" + position);

        if(position == 0) {
            return HistoryFragment.newInstance(position + 1);
        } else if(position == 1) {
            return PinnedFragment.newInstance(position + 1);
        } else {
            return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
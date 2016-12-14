package sbingo.likecloudmusic.ui.adapter.PageAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import sbingo.likecloudmusic.utils.NavigationUtils;

/**
 * Author: Sbingo
 * Date:   2016/12/14
 */

public class MainPagerAdapter extends FragmentPagerAdapter {

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return NavigationUtils.mainFragments[position];
    }

    @Override
    public int getCount() {
        return NavigationUtils.mainFragments.length;
    }
}

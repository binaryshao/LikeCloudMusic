package sbingo.likecloudmusic.ui.adapter.PageAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import sbingo.likecloudmusic.R;
import sbingo.likecloudmusic.common.MyApplication;
import sbingo.likecloudmusic.ui.fragment.LocalMusic.DiskMusicFragment;

/**
 * Author: Sbingo
 * Date:   2016/12/15
 */

public class LocalPagerAdapter extends FragmentPagerAdapter {

    private String[] titles;
    private List<DiskMusicFragment> fragments;

    public LocalPagerAdapter(FragmentManager fm, List<DiskMusicFragment> fragments) {
        super(fm);
        titles = MyApplication.getAppContext().getResources().getStringArray(R.array.local);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

}

package sbingo.likecloudmusic.ui.adapter.RvAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import sbingo.likecloudmusic.R;
import sbingo.likecloudmusic.bean.Song;
import sbingo.likecloudmusic.ui.fragment.LocalMusic.DiskMusicFragment;

/**
 * Author: Sbingo
 * Date:   2016/12/15
 */

public class DiskMusicAdapter extends BaseRvAdapter<Song> {

    DiskMusicFragment fragment;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (fragment.getType() == 1) {
            R.layout.disk_song_item;
        } else {
            R.layout.disk_other_item;
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        return fragment.getType();
    }
}

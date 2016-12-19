package sbingo.likecloudmusic.ui.adapter.RvAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import sbingo.likecloudmusic.R;
import sbingo.likecloudmusic.bean.Song;

/**
 * Author: Sbingo
 * Date:   2016/12/15
 */

public class DiskMusicAdapter extends BaseRvAdapter<Song> {

    private int currentType;

    private Context mContext;

    private DiskMusicListener listener;

    public interface DiskMusicListener {

    }

    public DiskMusicAdapter(int currentType, DiskMusicListener listener, Context mContext) {
        this.currentType = currentType;
        this.listener = listener;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (currentType == 1) {
            return new SongHolder(LayoutInflater.from(mContext).inflate(R.layout.disk_song_item, null));
        } else {
            return new OtherHolder(LayoutInflater.from(mContext).inflate(R.layout.disk_other_item, null));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Song song = mList.get(position);
        if (holder instanceof SongHolder) {
            bindSong((SongHolder)holder, position, song);
        } else {
            bindOthers((OtherHolder)holder, position, song);
        }
    }

    void bindSong(SongHolder h, int position, Song song) {
        h.songName.setText(song.getTitle());
        h.info.setText(song.getAlbum());
    }

    void bindOthers(OtherHolder h, int position, Song song) {
        Glide.with(mContext).load(song.getAlbum()).placeholder(R.drawable.pic_loading_45).error(R.drawable.pic_error_45).into(h.thumb);
        h.title.setText(song.getTitle());
    }

    class SongHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.speaker)
        ImageView speaker;
        @BindView(R.id.mv)
        ImageView mv;
        @BindView(R.id.song_name)
        TextView songName;
        @BindView(R.id.info)
        TextView info;
        @BindView(R.id.more)
        ImageView more;

        SongHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class OtherHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thumb)
        ImageView thumb;
        @BindView(R.id.more)
        ImageView more;
        @BindView(R.id.speaker)
        ImageView speaker;
        @BindView(R.id.right)
        FrameLayout right;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.count)
        TextView count;
        @BindView(R.id.info)
        TextView info;

        OtherHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}

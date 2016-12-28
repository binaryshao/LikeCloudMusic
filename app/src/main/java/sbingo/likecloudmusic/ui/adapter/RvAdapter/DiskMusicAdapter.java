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
import com.orhanobut.logger.Logger;

import java.io.FileNotFoundException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;
import sbingo.likecloudmusic.R;
import sbingo.likecloudmusic.bean.Playlist;
import sbingo.likecloudmusic.bean.Song;
import sbingo.likecloudmusic.db.LitePalHelper;
import sbingo.likecloudmusic.event.PlayingMusicUpdateEvent;
import sbingo.likecloudmusic.event.RxBus;
import sbingo.likecloudmusic.utils.FileUtils;
import sbingo.likecloudmusic.utils.RemindUtils;

/**
 * Author: Sbingo
 * Date:   2016/12/15
 */

public class DiskMusicAdapter extends BaseRvAdapter<Song> {

    private static final String TAG = "DiskMusicAdapter :";

    private int currentType;

    private Context mContext;

    private DiskMusicListener listener;

    private int currentPlayingIndex;

    public interface DiskMusicListener {

        void toPlayerActivity(Song song);

        void playList(Playlist playlist, int index);
    }

    public DiskMusicAdapter(int currentType, DiskMusicListener listener, Context mContext) {
        this.currentType = currentType;
        this.listener = listener;
        this.mContext = mContext;

        RxBus.getInstance().toObservable(PlayingMusicUpdateEvent.class)
                .subscribe(new Action1<PlayingMusicUpdateEvent>() {
                    @Override
                    public void call(PlayingMusicUpdateEvent event) {
                        mList.get(currentPlayingIndex).setPlaying(false);
                        mList.get(event.getIndex()).setPlaying(true);
                        currentPlayingIndex = event.getIndex();
                    }
                });
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
            bindSong((SongHolder) holder, position, song);
        } else {
            bindOthers((OtherHolder) holder, position, song);
        }
    }

    void bindSong(SongHolder h, final int position, final Song song) {
        h.songName.setText(song.getTitle());
        h.info.setText(mContext.getString(R.string.song_info, song.getArtist(), song.getAlbum()));
        h.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if (song.isPlaying()) {
                        Logger.d(TAG + "toPlayerActivity");
                        listener.toPlayerActivity(song);
                    } else {
                        Logger.d(TAG + "playList");
                        if (!FileUtils.isFileExists(song.getPath())) {
                            RemindUtils.showToast("该文件不存在");
                            LitePalHelper.deleteSong(song);
                            deleteItem(position);
                            return;
                        }
                        song.setPlaying(true);
                        mList.get(currentPlayingIndex).setPlaying(false);
                        currentPlayingIndex = position;
                        Playlist playlist = new Playlist();
                        playlist.setSongs(mList);
                        playlist.setCurrentPlaylist(true);
                        listener.playList(playlist, position);
                    }
                }
            }
        });
    }

    void bindOthers(OtherHolder h, int position, Song song) {
        switch (currentType) {
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            default:
        }
        Glide.with(mContext).load(FileUtils.parseThumbToByte(song)).placeholder(R.drawable.pic_loading_45).error(R.drawable.pic_error_45).into(h.thumb);
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

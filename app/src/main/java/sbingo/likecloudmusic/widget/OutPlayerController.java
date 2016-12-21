package sbingo.likecloudmusic.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sbingo.likecloudmusic.R;

/**
 * Author: Sbingo
 * Date:   2016/12/14
 */

public class OutPlayerController extends FrameLayout {

    @BindView(R.id.thumb)
    ImageView thumb;
    @BindView(R.id.next)
    ImageView next;
    @BindView(R.id.play)
    ImageView play;
    @BindView(R.id.play_list)
    ImageView playList;
    @BindView(R.id.play_progress)
    ProgressBar playProgress;
    @BindView(R.id.controller)
    LinearLayout controller;
    @BindView(R.id.song_name)
    TextView songName;
    @BindView(R.id.singer)
    TextView singer;

    private OutPlayerControllerListener listener;
    private Context mContext;
    private boolean isPlaying;

    public OutPlayerController(Context context) {
        this(context, null);
    }

    public OutPlayerController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OutPlayerController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    void init() {
        ButterKnife.bind(LayoutInflater.from(mContext).inflate(R.layout.out_player_controller, this));
    }

    @OnClick({R.id.next, R.id.play, R.id.play_list, R.id.controller})
    public void onClick(View view) {
        if (listener == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.next:
                setPlaying(true);
                listener.next();
                break;
            case R.id.play:
                isPlaying = isPlaying ? false : true;
                Glide.with(mContext).load(isPlaying ? R.drawable.pause : R.drawable.play).placeholder(R.drawable.pic_loading_45).into(play);
                listener.play();
                break;
            case R.id.play_list:
                listener.playList();
                break;
            case R.id.controller:
                listener.controller();
                break;
        }
    }

    public interface OutPlayerControllerListener {
        void play();

        void next();

        void playList();

        void controller();
    }

    public void setPlayerListener(OutPlayerControllerListener listener) {
        this.listener = listener;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
        Glide.with(mContext).load(isPlaying ? R.drawable.pause : R.drawable.play).placeholder(R.drawable.pic_loading_45).into(play);
    }

    public void setThumb(String url) {
        Glide.with(mContext).load(url).placeholder(R.drawable.pic_loading_45).error(R.drawable.pic_error_45).into(thumb);
    }

    public void setSongName(String s) {
        songName.setText(s);
    }

    public void setSinger(String s) {
        singer.setText(s);
    }

    public void setPlayProgress(int progress) {
        playProgress.setProgress(progress);
    }
}

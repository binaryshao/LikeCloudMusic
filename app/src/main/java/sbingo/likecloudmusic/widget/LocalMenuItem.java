package sbingo.likecloudmusic.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import sbingo.likecloudmusic.R;

/**
 * Author: Sbingo
 * Date:   2016/12/14
 */

public class LocalMenuItem extends FrameLayout {

    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.count)
    TextView count;
    @BindView(R.id.speaker)
    ImageView speaker;
    private Context mContext;

    Drawable mIcon;
    String mTitle;

    public LocalMenuItem(Context context) {
        this(context, null);
    }

    public LocalMenuItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocalMenuItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }

    void init(AttributeSet attrs) {
        ButterKnife.bind(LayoutInflater.from(mContext).inflate(R.layout.local_menu_item, this));
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.LocalMenuItem);
        mIcon = ta.getDrawable(R.styleable.LocalMenuItem_icon);
        mTitle = ta.getString(R.styleable.LocalMenuItem_title);
        ta.recycle();
        icon.setImageDrawable(mIcon);
        title.setText(mTitle);
    }

    public void showSpeaker() {
        speaker.setVisibility(VISIBLE);
    }

    public void hideSpeaker() {
        speaker.setVisibility(GONE);
    }

    public void setCount(int s) {
        count.setText("(" + String.valueOf(s) + ")");
    }

}

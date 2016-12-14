package sbingo.likecloudmusic.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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

    public LocalMenuItem(Context context) {
        this(context, null);
    }

    public LocalMenuItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocalMenuItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    void init() {
        ButterKnife.bind(LayoutInflater.from(mContext).inflate(R.layout.local_menu_item, this));

    }


}

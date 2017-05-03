package sbingo.likecloudmusic.ui.activity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import butterknife.BindView;
import sbingo.likecloudmusic.R;

/**
 * Author: Sbingo
 * Date:   2016/12/11
 */

public class LoadingActivity extends BaseActivity {


    @BindView(R.id.loading_text)
    TextView loadingText;

    @Override
    public int getLayoutId() {
        return R.layout.activity_loading;
    }

    @Override
    public void initInjector() {

    }

    @Override
    public void initViews() {
    }

    @Override
    public void customToolbar() {

    }

    @Override
    protected boolean hasToolbar() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingText.postDelayed(loadingOut, 1000);
    }

    Runnable loadingOut = new Runnable() {
        @Override
        public void run() {
            Animation animation = AnimationUtils.loadAnimation(LoadingActivity.this, R.anim.loading_fade_out);
            loadingText.startAnimation(animation);
            loadingText.postDelayed(toMainActivity, 1500);
        }
    };
    Runnable toMainActivity = new Runnable() {
        @Override
        public void run() {
            openActivity(MainActivity.class);
            finish();
        }
    };
}

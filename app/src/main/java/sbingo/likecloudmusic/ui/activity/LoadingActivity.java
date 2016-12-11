package sbingo.likecloudmusic.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.ButterKnife;
import sbingo.likecloudmusic.R;

public class LoadingActivity extends BaseActivity {

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
        loadingText = ButterKnife.findById(this, R.id.loading_text);
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
        loadingText.postDelayed(toMainActivity, 1500);
    }

    Runnable toMainActivity = new Runnable() {
        @Override
        public void run() {
            startActivityTo(MainActivity.class);
        }
    };
}

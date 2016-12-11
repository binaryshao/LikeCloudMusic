package sbingo.likecloudmusic.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import sbingo.likecloudmusic.R;

/**
 * Author: Sbingo
 * Date:   2016/12/11
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected abstract int getLayoutId();

    protected abstract void initInjector();

    protected abstract void initViews();

    protected abstract void customToolbar();

    protected abstract boolean hasToolbar();

    protected Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initToolbar();
        customToolbar();
        initInjector();
        initViews();
    }

    private void initToolbar() {
        if (hasToolbar()) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }
    }

    protected void startActivityTo(Class a) {
        Intent intent = new Intent(this, a);
        startActivity(intent);
    }
}

package com.kma.demo.activity;

import android.os.Bundle;
import android.view.View;

import com.kma.demo.R;
import com.kma.demo.adapter.MusicViewPagerAdapter;
import com.kma.demo.databinding.ActivityPlayMusicBinding;


public class PlayMusicActivity extends BaseActivity {

    private ActivityPlayMusicBinding mActivityPlayMusicBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityPlayMusicBinding = ActivityPlayMusicBinding.inflate(getLayoutInflater());
        setContentView(mActivityPlayMusicBinding.getRoot());

        initToolbar();
        initUI();
    }

    private void initToolbar() {
        mActivityPlayMusicBinding.toolbar.imgLeft.setImageResource(R.drawable.ic_back_white);
        mActivityPlayMusicBinding.toolbar.tvTitle.setText(R.string.music_player);
        mActivityPlayMusicBinding.toolbar.layoutPlayAll.setVisibility(View.GONE);
        mActivityPlayMusicBinding.toolbar.imgLeft.setOnClickListener(v -> onBackPressed());
    }

    private void initUI() {
        MusicViewPagerAdapter musicViewPagerAdapter = new MusicViewPagerAdapter(this);
        mActivityPlayMusicBinding.viewpager2.setAdapter(musicViewPagerAdapter);
        mActivityPlayMusicBinding.indicator3.setViewPager(mActivityPlayMusicBinding.viewpager2);
        mActivityPlayMusicBinding.viewpager2.setCurrentItem(1);
    }
}

package com.kma.demo.ui.activity;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kma.demo.R;
import com.kma.demo.constant.Constant;
import com.kma.demo.constant.GlobalFuntion;
import com.kma.demo.databinding.ActivityMainBinding;
import com.kma.demo.ui.fragment.AllSongsFragment;
import com.kma.demo.ui.fragment.FeaturedSongsFragment;
import com.kma.demo.ui.fragment.HomeFragment;
import com.kma.demo.ui.fragment.LibraryFragment;
import com.kma.demo.ui.fragment.NewSongsFragment;
import com.kma.demo.ui.fragment.PopularSongsFragment;
import com.kma.demo.data.model.Song;
import com.kma.demo.service.MusicService;
import com.kma.demo.utils.GlideUtils;

import dagger.hilt.android.AndroidEntryPoint;

@SuppressLint("NonConstantResourceId")
@AndroidEntryPoint
public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final int TYPE_HOME = 1;
    public static final int TYPE_ALL_SONGS = 2;
    public static final int TYPE_FEATURED_SONGS = 3;
    public static final int TYPE_POPULAR_SONGS = 4;
    public static final int TYPE_NEW_SONGS = 5;
    public static final int TYPE_LIBRARY = 6;

    private int mTypeScreen = TYPE_HOME;
    private ActivityMainBinding mActivityMainBinding;
    private int mAction;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAction = intent.getIntExtra(Constant.MUSIC_ACTION, 0);
            handleMusicAction();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mActivityMainBinding.getRoot());

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Constant.CHANGE_LISTENER));
        openHomeScreen();
        initListener();
        displayLayoutBottom();

        ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();
        if (!memoryInfo.lowMemory) {
            Log.d("TAG" , "available mem " + memoryInfo.availMem);
        } else {
            Log.d("TAG" , "low mem");
        }

//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        String token = task.getResult();
//                        Log.d("TAG", token);
//                    }
//                });
    }

    private void initToolbar(String title) {
        mActivityMainBinding.header.imgLeft.setImageResource(R.drawable.ic_menu_left);
        mActivityMainBinding.header.tvTitle.setText(title);
    }

    private void initListener() {
        mActivityMainBinding.header.imgLeft.setOnClickListener(this);
        mActivityMainBinding.header.layoutPlayAll.setOnClickListener(this);

        mActivityMainBinding.menuLeft.layoutClose.setOnClickListener(this);
        mActivityMainBinding.menuLeft.tvMenuHome.setOnClickListener(this);
        mActivityMainBinding.menuLeft.tvMenuAllSongs.setOnClickListener(this);
        mActivityMainBinding.menuLeft.tvMenuFeaturedSongs.setOnClickListener(this);
        mActivityMainBinding.menuLeft.tvMenuPopularSongs.setOnClickListener(this);
        mActivityMainBinding.menuLeft.tvMenuNewSongs.setOnClickListener(this);
        mActivityMainBinding.menuLeft.tvMenuLibrary.setOnClickListener(this);

        mActivityMainBinding.layoutBottom.imgPrevious.setOnClickListener(this);
        mActivityMainBinding.layoutBottom.imgPlay.setOnClickListener(this);
        mActivityMainBinding.layoutBottom.imgNext.setOnClickListener(this);
        mActivityMainBinding.layoutBottom.imgClose.setOnClickListener(this);
        mActivityMainBinding.layoutBottom.layoutText.setOnClickListener(this);
        mActivityMainBinding.layoutBottom.imgSong.setOnClickListener(this);
    }

    private void openHomeScreen() {
        replaceFragment(new HomeFragment());
        mTypeScreen = TYPE_HOME;
        initToolbar(getString(R.string.app_name));
        displayLayoutPlayAll();
    }

    public void openPopularSongsScreen() {
        replaceFragment(new PopularSongsFragment());
        mTypeScreen = TYPE_POPULAR_SONGS;
        initToolbar(getString(R.string.menu_popular_songs));
        displayLayoutPlayAll();
    }

    public void openNewSongsScreen() {
        replaceFragment(new NewSongsFragment());
        mTypeScreen = TYPE_NEW_SONGS;
        initToolbar(getString(R.string.menu_new_songs));
        displayLayoutPlayAll();
    }

    public void openLibrarySongScreen() {
        replaceFragment(new LibraryFragment());
        mTypeScreen = TYPE_LIBRARY;
        initToolbar(getString(R.string.menu_library));
        displayLayoutPlayAll();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_close:
                mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.img_left:
                mActivityMainBinding.drawerLayout.openDrawer(GravityCompat.START);
                break;

            case R.id.tv_menu_home:
                mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                Constant.IS_LIBRARY = false;
                openHomeScreen();
                break;

            case R.id.tv_menu_all_songs:
                mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                Constant.IS_LIBRARY = false;
                replaceFragment(new AllSongsFragment());
                mTypeScreen = TYPE_ALL_SONGS;
                initToolbar(getString(R.string.menu_all_songs));
                displayLayoutPlayAll();
                break;

            case R.id.tv_menu_featured_songs:
                mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                Constant.IS_LIBRARY = false;
                replaceFragment(new FeaturedSongsFragment());
                mTypeScreen = TYPE_FEATURED_SONGS;
                initToolbar(getString(R.string.menu_featured_songs));
                displayLayoutPlayAll();
                break;

            case R.id.tv_menu_popular_songs:
                mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                Constant.IS_LIBRARY = false;
                openPopularSongsScreen();
                break;

            case R.id.tv_menu_new_songs:
                mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                Constant.IS_LIBRARY = false;
                openNewSongsScreen();
                break;

            case R.id.tv_menu_library:
                mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                Constant.IS_LIBRARY = true;
                openLibrarySongScreen();
                break;

            case R.id.img_previous:
                clickOnPrevButton();
                break;

            case R.id.img_play:
                clickOnPlayButton();
                break;

            case R.id.img_next:
                clickOnNextButton();
                break;

            case R.id.img_close:
                clickOnCloseButton();
                break;

            case R.id.layout_text:
            case R.id.img_song:
                openPlayMusicActivity();
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment).commitAllowingStateLoss();
    }

    private void showConfirmExitApp() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.app_name))
                .content(getString(R.string.msg_exit_app))
                .positiveText(getString(R.string.action_ok))
                .onPositive((dialog, which) -> finish())
                .negativeText(getString(R.string.action_cancel))
                .cancelable(false)
                .show();
    }

    private void displayLayoutPlayAll() {
        switch (mTypeScreen) {
            case TYPE_ALL_SONGS:
            case TYPE_FEATURED_SONGS:
            case TYPE_POPULAR_SONGS:
            case TYPE_NEW_SONGS:
            case TYPE_LIBRARY:
                mActivityMainBinding.header.layoutPlayAll.setVisibility(View.VISIBLE);
                break;

            default:
                mActivityMainBinding.header.layoutPlayAll.setVisibility(View.GONE);
                break;
        }
    }

    private void displayLayoutBottom() {
        if (MusicService.mPlayer == null) {
            mActivityMainBinding.layoutBottom.layoutItem.setVisibility(View.GONE);
            return;
        }
        mActivityMainBinding.layoutBottom.layoutItem.setVisibility(View.VISIBLE);
        showInforSong();
        showStatusButtonPlay();
    }

    private void handleMusicAction() {
        if (Constant.CANNEL_NOTIFICATION == mAction) {
            mActivityMainBinding.layoutBottom.layoutItem.setVisibility(View.GONE);
            return;
        }
        mActivityMainBinding.layoutBottom.layoutItem.setVisibility(View.VISIBLE);
        showInforSong();
        showStatusButtonPlay();
    }

    private void showInforSong() {
        if (MusicService.mListSongPlaying == null || MusicService.mListSongPlaying.isEmpty()) {
            return;
        }
        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
        mActivityMainBinding.layoutBottom.tvSongName.setText(currentSong.getTitle());
        mActivityMainBinding.layoutBottom.tvArtist.setText(currentSong.getArtist());
        GlideUtils.loadUrl(currentSong.getImage(), mActivityMainBinding.layoutBottom.imgSong);
    }

    private void showStatusButtonPlay() {
        if (MusicService.isPlaying) {
            mActivityMainBinding.layoutBottom.imgPlay.setImageResource(R.drawable.ic_pause_black);
        } else {
            mActivityMainBinding.layoutBottom.imgPlay.setImageResource(R.drawable.ic_play_black);
        }
    }

    private void clickOnPrevButton() {
        GlobalFuntion.startMusicService(this, Constant.PREVIOUS, MusicService.mSongPosition);
    }

    private void clickOnNextButton() {
        GlobalFuntion.startMusicService(this, Constant.NEXT, MusicService.mSongPosition);
    }

    private void clickOnPlayButton() {
        if (MusicService.isPlaying) {
            GlobalFuntion.startMusicService(this, Constant.PAUSE, MusicService.mSongPosition);
        } else {
            GlobalFuntion.startMusicService(this, Constant.RESUME, MusicService.mSongPosition);
        }
    }

    private void clickOnCloseButton() {
        GlobalFuntion.startMusicService(this, Constant.CANNEL_NOTIFICATION, MusicService.mSongPosition);
    }

    private void openPlayMusicActivity() {
        GlobalFuntion.startActivity(this, PlayMusicActivity.class);
    }

    public ActivityMainBinding getActivityMainBinding() {
        return mActivityMainBinding;
    }

    @Override
    public void onBackPressed() {
        showConfirmExitApp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        mActivityMainBinding.header.imgLeft.setOnClickListener(null);
        mActivityMainBinding.header.layoutPlayAll.setOnClickListener(null);

        mActivityMainBinding.menuLeft.layoutClose.setOnClickListener(null);
        mActivityMainBinding.menuLeft.tvMenuHome.setOnClickListener(null);
        mActivityMainBinding.menuLeft.tvMenuAllSongs.setOnClickListener(null);
        mActivityMainBinding.menuLeft.tvMenuFeaturedSongs.setOnClickListener(null);
        mActivityMainBinding.menuLeft.tvMenuPopularSongs.setOnClickListener(null);
        mActivityMainBinding.menuLeft.tvMenuNewSongs.setOnClickListener(null);
        mActivityMainBinding.menuLeft.tvMenuLibrary.setOnClickListener(null);

        mActivityMainBinding.layoutBottom.imgPrevious.setOnClickListener(null);
        mActivityMainBinding.layoutBottom.imgPlay.setOnClickListener(null);
        mActivityMainBinding.layoutBottom.imgNext.setOnClickListener(null);
        mActivityMainBinding.layoutBottom.imgClose.setOnClickListener(null);
        mActivityMainBinding.layoutBottom.layoutText.setOnClickListener(null);
        mActivityMainBinding.layoutBottom.imgSong.setOnClickListener(null);
    }
}
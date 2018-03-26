package com.techease.mf.ui.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.techease.mf.LikeListener;
import com.techease.mf.R;
import com.techease.mf.ui.fragments.HomeTabFragment;
import com.techease.mf.ui.fragments.MyLikesFragment;
import com.techease.mf.ui.fragments.ThisWeek;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public class HomeActivity extends AppCompatActivity implements LikeListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeTabFragment()).commit();
    }
    @Override
    public void onLikePressed() {
        MyLikesFragment likeFragment1 = (MyLikesFragment) getSupportFragmentManager().findFragmentById(R.id.viewpager);
        ThisWeek thisWeek = (ThisWeek) getSupportFragmentManager().findFragmentById(R.id.viewpage);
        if(likeFragment1!=null){
//            likeFragment1.updatelikes();
        } if (thisWeek !=null){
//            thisWeek.TrendsLikes();//

        }
    }
}
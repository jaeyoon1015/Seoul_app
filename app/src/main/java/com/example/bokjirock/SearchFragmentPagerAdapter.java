package com.example.bokjirock;

import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class SearchFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private String[] VIEW_MAPNTOP_TITLES = {"맞춤검색","키워드검색"};

    // 아답터

    public SearchFragmentPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    // tab title를 반환 함수.
    @Override
    public CharSequence getPageTitle(int position) {
        return VIEW_MAPNTOP_TITLES[position];
    }

    // 해당 프라그먼트 호출 함수.
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return searchActivity.newInstance();
            case 1:
                return keywordActivity.newInstance();
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return 2;
    }
}

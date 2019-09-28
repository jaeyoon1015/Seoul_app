package com.example.bokjirock;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * FragmentPagerAdapter 를 상속받는 어댑터를 정의한다.
 */
public class CustomFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private String[] VIEW_MAPNTOP_TITLES = {"최신순","관심정책"};

    // 아답터 생성자
    public CustomFragmentPagerAdapter(FragmentManager fm) {
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
                return Fragment1.newInstance();
            case 1:
                return Fragment2.newInstance("sig");
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
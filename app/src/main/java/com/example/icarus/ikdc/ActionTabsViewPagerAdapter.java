package com.example.icarus.ikdc;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by donovan on 4/26/15.
 */
public class ActionTabsViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments;

    public static final int VID = 0;
    public static final int IMG = 1;
    public static final int AUDIO = 2;
    public static final int TEXT = 3;
    public static final String UI_TAB_VID = "Videos";
    public static final String UI_TAB_IMG = "Images";
    public static final String UI_TAB_AUDIO = "Audio";
    public static final String UI_TAB_TEXT = "Text";

    public ActionTabsViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments){
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int pos){
        return fragments.get(pos);
    }

    public int getCount(){
        return fragments.size();
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case VID:
                return UI_TAB_VID;
            case IMG:
                return UI_TAB_IMG;
            case AUDIO:
                return UI_TAB_AUDIO;
            case TEXT:
                return UI_TAB_TEXT;
            default:
                break;
        }
        return null;
    }
}

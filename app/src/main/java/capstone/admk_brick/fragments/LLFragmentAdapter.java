package capstone.admk_brick.fragments;

/**
 * Created by jaeho on 2015-05-10.
 */
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import capstone.admk_brick.R;

import java.util.Locale;


/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 *
 * FragmentPageAdapter ->
 * Implementation of PagerAdapter that represents each page as a Fragment
 * that is persistently kept in the fragment manager as long as the user can return to the page.
 * 좌/우로 화면을 전환할 때 새로운 프래그먼트를 표시하면서 이전 프래그먼트를 메모리 스택에 저장한다.
 */


public class LLFragmentAdapter extends FragmentPagerAdapter {

    public static final String TAG = "RetroWatchFragmentAdapter";

    // Total count
    //public static final int FRAGMENT_COUNT = 3;
    public static final int FRAGMENT_COUNT = 6;

    // Fragment position
    /*
    public static final int FRAGMENT_POS_TIMELINE = 0;
    public static final int FRAGMENT_POS_GRAPH = 1;
    public static final int FRAGMENT_POS_SETTINGS = 2;
    */

    public static final int FRAGMENT_POS_TIMELINE = 0;
    public static final int FRAGMENT_POS_GRAPH = 4;
    public static final int FRAGMENT_POS_RECOMMEND = 3;
    public static final int FRAGMENT_POS_SETTINGS = 5;
    public static final int FRAGMENT_POS_SLEEP = 1;
    public static final int FRAGMENT_POS_WATER = 2;

    public static final String ARG_SECTION_NUMBER = "section_number";

    // System
    private Context mContext = null;						//API - import android.content.Context;
    private Handler mHandler = null;						//API - import android.os.Handler;
    private IFragmentListener mFragmentListener = null;		//make interface - fragments.IFragmentListener

    private Fragment mTimelineFragment = null;			//API - import android.support.v4.app.Fragment;
    private Fragment mGraphFragment = null;				//API - import android.support.v4.app.Fragment;
    private Fragment mLLSettingsFragment = null;		//API - import android.support.v4.app.Fragment;

    /**/
    private Fragment mSleepFragment = null;
    private Fragment mWaterFragment = null;
    private Fragment mRecommendFragment = null;

    public LLFragmentAdapter(FragmentManager fm, Context c, IFragmentListener l, Handler h) {
        super(fm);
        mContext = c;
        mFragmentListener = l;
        mHandler = h;
    }

    /**/
    static int water_position;

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        Fragment fragment;
        //boolean needToSetArguments = false;

        if(position == FRAGMENT_POS_TIMELINE) {
            if(mTimelineFragment == null) {
                mTimelineFragment = new TimelineFragment(mContext, mFragmentListener, mHandler);
                //needToSetArguments = true;
            }
            fragment = mTimelineFragment;

        } else if(position == FRAGMENT_POS_GRAPH) {
            if(mGraphFragment == null) {
                mGraphFragment = new GraphFragment(mContext, mFragmentListener);
                //needToSetArguments = true;
            }
            fragment = mGraphFragment;

        } else if(position == FRAGMENT_POS_SETTINGS) {
            if(mLLSettingsFragment == null) {
                mLLSettingsFragment = new LLSettingsFragment(mContext, mFragmentListener);
                //needToSetArguments = true;
            }
            fragment = mLLSettingsFragment;
        /**/
        }else if(position == FRAGMENT_POS_SLEEP) {
            if(mSleepFragment == null) {
                mSleepFragment = new Sleep(mContext, mFragmentListener);
                //needToSetArguments = true;
            }
            fragment = mSleepFragment;

        } else if(position == FRAGMENT_POS_WATER) {
            if (mWaterFragment == null){
                mWaterFragment = new Water(mContext, mFragmentListener);
                //needToSetArguments = true;
            }
            fragment = mWaterFragment;
        } else if(position == FRAGMENT_POS_RECOMMEND) {
            if (mRecommendFragment == null){
                mRecommendFragment = new Recommend(mContext, mFragmentListener);
                //needToSetArguments = true;
            }
            fragment = mRecommendFragment;
        }
        else {
            fragment = null;
        }

        // TODO: If you have something to notify to the fragment.
		/*
		if(needToSetArguments) {
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
		}
		*/

        return fragment;
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case FRAGMENT_POS_TIMELINE:
                return mContext.getString(R.string.title_timeline).toUpperCase(l);
            case FRAGMENT_POS_GRAPH:
                return mContext.getString(R.string.title_graph).toUpperCase(l);
            case FRAGMENT_POS_SETTINGS:
                return mContext.getString(R.string.title_ll_settings).toUpperCase(l);
            case FRAGMENT_POS_SLEEP:
                return mContext.getString(R.string.title_sleep).toUpperCase(l);
            case FRAGMENT_POS_WATER:
                return mContext.getString(R.string.title_water).toUpperCase(l);
            case FRAGMENT_POS_RECOMMEND:
                return mContext.getString(R.string.title_recommend).toUpperCase(l);

        }
        return null;
    }


}

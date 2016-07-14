package capstone.admk_brick.fragments;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

import capstone.admk_brick.R;
import capstone.admk_brick.contents.ContentManager;
import capstone.admk_brick.utils.AppSettings;
import android.view.View.OnClickListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.widget.RelativeLayout;
import android.view.WindowManager.LayoutParams;
import android.view.KeyEvent;
import android.graphics.Bitmap;


/**
 * Created by jaeho on 2015-05-11.
 */
public class Recommend extends Fragment implements IAdapterListener,View.OnClickListener {
    private static final String TAG = "RecommendFragment";
    private Context mContext = null;
    private IFragmentListener mFragmentListener = null;

    private ContentManager mContentManager = null;

    private TextView mRecommendWalkText = null;
    private TextView mTodayWalkText = null;
    private TextView mDistanceWalkText = null;
    private TextView mMoreWalkText = null;
    private ImageButton mPopWalk = null;

    private TextView mRecommendSleepText = null;
    private TextView mTodaySleepText = null;
    private TextView mMoreSleepText = null;
    private ImageButton mPopSleep = null;

    private TextView mRecommendWaterText = null;
    private TextView mTodayWaterText = null;
    private TextView mMoreWaterText = null;
    private ImageButton mPopWater = null;

    //

    //

    public Recommend() {
    }


    public Recommend(Context c, IFragmentListener l) {
        mContext = c;
        mFragmentListener = l;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ///////////
//        View view = inflater.inflate(R.layout.tab_2_head_buttons, container,false);



        ///////////////////////////////

        View rootView = inflater.inflate(R.layout.recommend_page, container, false);

        mRecommendWalkText = (TextView) rootView.findViewById(R.id.recommend_walk);
        mTodayWalkText = (TextView) rootView.findViewById(R.id.today_walk);
        mDistanceWalkText = (TextView) rootView.findViewById(R.id.distance_walk);
        mMoreWalkText = (TextView) rootView.findViewById(R.id.more_walk);

        mPopWalk = (ImageButton) rootView.findViewById(R.id.pop_walk);

        mPopWalk.setOnClickListener(this);






        mRecommendSleepText = (TextView) rootView.findViewById(R.id.recommend_sleep);
        mTodaySleepText = (TextView) rootView.findViewById(R.id.today_sleep);
        mMoreSleepText = (TextView) rootView.findViewById(R.id.more_sleep);
        mPopSleep = (ImageButton) rootView.findViewById(R.id.pop_sleep);
        mPopSleep.setOnClickListener(this);

        mRecommendWaterText = (TextView) rootView.findViewById(R.id.recommend_water);
        mTodayWaterText = (TextView) rootView.findViewById(R.id.today_water);
        mMoreWaterText = (TextView) rootView.findViewById(R.id.more_water);
        mPopWater = (ImageButton) rootView.findViewById(R.id.pop_water);
        mPopWater.setOnClickListener(this);

        String age_line;
        int walk_amount,sleep_amount;
        if(AppSettings.getAge() < 20) {
            age_line = "10";
            walk_amount = 10000;
            sleep_amount = 10;
        }else if(AppSettings.getAge() < 30){
            age_line = "20";
            walk_amount = 10000;
            sleep_amount = 9;
        }else if(AppSettings.getAge() < 40){
            age_line = "30";
            walk_amount = 10000;
            sleep_amount = 9;
        }else if(AppSettings.getAge() < 50){
            age_line = "40";
            walk_amount = 9000;
            sleep_amount = 9;
        }else if(AppSettings.getAge() < 60){
            age_line = "50";
            walk_amount = 8000;
            sleep_amount = 9;
        }else if(AppSettings.getAge() < 70){
            age_line = "60";
            walk_amount = 7000;
            sleep_amount = 8;
        }else {
            age_line = "more than 70";
            walk_amount = 5000;
            sleep_amount =8;
        }


        double walk_distance = AppSettings.getStep()/100000 * TimelineFragment.Recommend_Use_Walk ;
        mRecommendWalkText.setText( "Recommend walk amount of "+age_line + "'s is "+ Integer.toString(walk_amount) );
        mTodayWalkText.setText("Your today Walk amount is " + Integer.toString(TimelineFragment.Recommend_Use_Walk) );
        mDistanceWalkText.setText("Walk distance is "+ Double.toString(walk_distance) + " km" );
        if (walk_amount<TimelineFragment.Recommend_Use_Walk){
            mMoreWalkText.setText("You already accomplish today's goal, but you avoid walking too much");
        }else {
            mMoreWalkText.setText("Recommend walk amount remains " + Integer.toString(walk_amount - TimelineFragment.Recommend_Use_Walk));
        }
        /// Sleep

        mRecommendSleepText.setText( "Recommend Sleep amount of " +age_line+ "'s is " + Integer.toString(sleep_amount));
        mTodaySleepText.setText("Your today Sleep amount is " + Integer.toString(Sleep.Sleep_Time/60) + " Hour " +Integer.toString(Sleep.Sleep_Time%60) + " Minuites " );
        if(sleep_amount*60 < Sleep.Sleep_Time){
            mMoreSleepText.setText("You already accomplish today's goal, but you avoid sleeping too much");
        }else {
            mMoreSleepText.setText("Recommend Nap amount is " + Integer.toString((sleep_amount * 60 - Sleep.Sleep_Time) / 60) + " Hour " + Integer.toString((sleep_amount * 60 - Sleep.Sleep_Time) % 60) + " Minuites ");
        }
        /// Water

        mRecommendWaterText.setText( "Recommend Cups of Water are 10cups ( standard paper cup ( 150ml ) ");
        mTodayWaterText.setText( "Your today Cups of Water are " + Integer.toString(Water.cups_today));
        if(Water.cups_today>10){
            mMoreWaterText.setText("You already accomplish today's goal, but you avoid drinking water too much");
        }else {
            mMoreWaterText.setText("Recommend Cups of Water remains " + Integer.toString(10 - Water.cups_today));
        }
        return rootView;
    }

    private PopupWindow mPopupWindow;
    private PopupWindow mPopupWindow1;
    private PopupWindow mPopupWindow2;
    private int temp_walk=0;
    private int temp_sleep=0;
    private int temp_water=0;
    public void onClick(View v) {
        if (v.getId() == R.id.pop_walk) {
            if(temp_walk==1){
                mPopupWindow.dismiss();
                temp_walk--;
            }else {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View popupView = inflater.inflate(R.layout.walk_recommend, null);

                mPopupWindow = new PopupWindow(popupView,
                        RelativeLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                mPopupWindow.setAnimationStyle(-1);
                mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, -100);
                //mPopupWindow.setOutsideTouchable(false);
                //mPopupWindow.setBackgroundDrawable(new BitmapDrawable()) ;
                // mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

                mPopupWindow.setOutsideTouchable(true);
                // mPopupWindow.setFocusable(true);
                //mPopupWindow.update();
                temp_walk++;

            }







        }else if (v.getId() == R.id.pop_sleep) {
            if(temp_sleep==1){
                mPopupWindow1.dismiss();
                temp_sleep--;
            }else {
                LayoutInflater inflater1 = LayoutInflater.from(getActivity());
                View popupView1 = inflater1.inflate(R.layout.sleep_recommend, null);

                mPopupWindow1 = new PopupWindow(popupView1,
                        RelativeLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                mPopupWindow1.setAnimationStyle(-1);
                mPopupWindow1.showAtLocation(popupView1, Gravity.CENTER, 0, -100);
                //mPopupWindow.setOutsideTouchable(false);
                //mPopupWindow.setBackgroundDrawable(new BitmapDrawable()) ;
                // mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

                mPopupWindow1.setOutsideTouchable(true);
                // mPopupWindow.setFocusable(true);
                //mPopupWindow.update();
                temp_sleep++;

            }

        }else if (v.getId() == R.id.pop_water) {
            if(temp_water==1){
                mPopupWindow2.dismiss();
                temp_water--;
            }else {
                LayoutInflater inflater2 = LayoutInflater.from(getActivity());
                View popupView2 = inflater2.inflate(R.layout.water_recommend, null);

                mPopupWindow2 = new PopupWindow(popupView2,
                        RelativeLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                mPopupWindow2.setAnimationStyle(-1);
                mPopupWindow2.showAtLocation(popupView2, Gravity.CENTER, 0, -100);
                //mPopupWindow.setOutsideTouchable(false);
                //mPopupWindow.setBackgroundDrawable(new BitmapDrawable()) ;
                // mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

                mPopupWindow2.setOutsideTouchable(true);
                // mPopupWindow.setFocusable(true);
                //mPopupWindow.update();
                temp_water++;

            }
        }else {

        }
    }







    ///////////

    @Override
    public void OnAdapterCallback(int msgType, int arg0, int arg1, String arg2, String arg3, Object arg4) {
        switch(msgType) {
            case IAdapterListener.CALLBACK_xxx:
                // TODO:
                //if(arg4 != null)
                //	mFragmentListener.OnFragmentCallback(IFragmentListener.CALLBACK_REQUEST_ADD_FILTER, 0, 0, null, null, arg4);
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    ////////////////////

    ////////////////////


}
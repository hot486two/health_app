package capstone.admk_brick.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import capstone.admk_brick.R;
import capstone.admk_brick.contents.ContentManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jaeho on 2015-05-11.
 */
public class Sleep extends Fragment implements IAdapterListener,View.OnClickListener {
    private static final String TAG = "SleepFragment";
    private Context mContext = null;
    private IFragmentListener mFragmentListener = null;

    private ContentManager mContentManager = null;

    private TextView mTimeSleepText = null;
    private TextView mdateForTL1, mdateForTL2, mdateForTL3, mdateForTL4, mdateForTL5, mdateForTL6, mdateForTL7, mdateForTL8 = null;
    private TextView mvalueForTL1, mvalueForTL2, mvalueForTL3, mvalueForTL4, mvalueForTL5, mvalueForTL6, mvalueForTL7, mvalueForTL8 = null;

    private ImageButton bt1 = null;
    private ImageButton bt2 = null;

    int mSleepState = 0;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd -  HH:mm ");
    SimpleDateFormat formatForUse = new SimpleDateFormat("MM/dd/HH/mm");
    SimpleDateFormat formatSave = new SimpleDateFormat( "yyyy . MM . dd - HH : mm ~ ");

    Date StartTime, FinTime;
    String STime, FTime , SaveTime;
    String useStartTime, useFinTime;
    int array_s[] = new int[4];
    int array_f[] = new int[4];

    String now_value = "0";
    String date_for_timeline_get, value_for_timeline_get;
    int now_value_int = 0;
    static  int Sleep_Time = 0;


    public Sleep() {
    }


    public Sleep(Context c, IFragmentListener l) {
        mContext = c;
        mFragmentListener = l;
    }


    private String sleep_key_get(String key) {
        SharedPreferences sleep_key = mContext.getSharedPreferences("sleep_key", Context.MODE_PRIVATE);
        String result = sleep_key.getString(key, "");
        return result;
    }

    private void sleep_key_save(String key, int value) {
        SharedPreferences sleep_key = mContext.getSharedPreferences("sleep_key", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sleep_key.edit();
        editor.putString(key, String.valueOf(value));
        editor.commit();
    }

    ////////////////////////////////////////////////
    private String timeline_date_get(int key) {
        SharedPreferences timeline_date = mContext.getSharedPreferences("Timeline_date", Context.MODE_PRIVATE);
        String result = timeline_date.getString(Integer.toString(key), "");
        return result;
    }

    private void timeline_date_save(int key, String value) {
        SharedPreferences timeline_date = mContext.getSharedPreferences("Timeline_date", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = timeline_date.edit();
        editor.putString(Integer.toString(key), value);
        editor.commit();
    }

    ////////////////////////////////////////////////
    private String timeline_value_get(int key) {
        SharedPreferences timeline_value = mContext.getSharedPreferences("Timeline_value", Context.MODE_PRIVATE);
        String result = timeline_value.getString(Integer.toString(key), "");
        return result;
    }

    private void timeline_value_save(int key, int value) {
        SharedPreferences timeline_value = mContext.getSharedPreferences("Timeline_value", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = timeline_value.edit();
        editor.putString(Integer.toString(key), String.valueOf(value));
        editor.commit();
    }

    ////////////////////////////////////////////
    private String start_sleep_value_get(String key) {
        SharedPreferences start_sleep_value = mContext.getSharedPreferences("start_sleep_value", Context.MODE_PRIVATE);
        String result = start_sleep_value.getString(key, "");
        return result;
    }

    private void start_sleep_value_save(String key, String value) {
        SharedPreferences start_sleep_value = mContext.getSharedPreferences("start_sleep_value", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = start_sleep_value.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void start_sleep_value_remove(String key) {
        SharedPreferences start_sleep_value = mContext.getSharedPreferences("start_sleep_value", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = start_sleep_value.edit();
        editor.remove(key);
        editor.commit();
    }

    public void onTImeline(int now_value_int) {
        if(now_value_int==0){

        }else {
            mdateForTL1 = (TextView) getActivity().findViewById(R.id.date_sleep1);
            mdateForTL1.setText(timeline_date_get(now_value_int));

            mvalueForTL1 = (TextView) getActivity().findViewById(R.id.date_sleep_time1);
            mvalueForTL1.setText(timeline_value_get(now_value_int));

            mdateForTL2 = (TextView) getActivity().findViewById(R.id.date_sleep2);
            mdateForTL2.setText(timeline_date_get(now_value_int - 1));

            mvalueForTL2 = (TextView) getActivity().findViewById(R.id.date_sleep_time2);
            mvalueForTL2.setText(timeline_value_get(now_value_int - 1));

            mdateForTL3 = (TextView) getActivity().findViewById(R.id.date_sleep3);
            mdateForTL3.setText(timeline_date_get(now_value_int - 2));

            mvalueForTL3 = (TextView) getActivity().findViewById(R.id.date_sleep_time3);
            mvalueForTL3.setText(timeline_value_get(now_value_int - 2));

            mdateForTL4 = (TextView) getActivity().findViewById(R.id.date_sleep4);
            mdateForTL4.setText(timeline_date_get(now_value_int - 3));

            mvalueForTL4 = (TextView) getActivity().findViewById(R.id.date_sleep_time4);
            mvalueForTL4.setText(timeline_value_get(now_value_int - 3));

            mdateForTL5 = (TextView) getActivity().findViewById(R.id.date_sleep5);
            mdateForTL5.setText(timeline_date_get(now_value_int - 4));

            mvalueForTL5 = (TextView) getActivity().findViewById(R.id.date_sleep_time5);
            mvalueForTL5.setText(timeline_value_get(now_value_int - 4));

            mdateForTL6 = (TextView) getActivity().findViewById(R.id.date_sleep6);
            mdateForTL6.setText(timeline_date_get(now_value_int - 5));

            mvalueForTL6 = (TextView) getActivity().findViewById(R.id.date_sleep_time6);
            mvalueForTL6.setText(timeline_value_get(now_value_int - 5));

            mdateForTL7 = (TextView) getActivity().findViewById(R.id.date_sleep7);
            mdateForTL7.setText(timeline_date_get(now_value_int - 6));

            mvalueForTL7 = (TextView) getActivity().findViewById(R.id.date_sleep_time7);
            mvalueForTL7.setText(timeline_value_get(now_value_int - 6));

            mdateForTL8 = (TextView) getActivity().findViewById(R.id.date_sleep8);
            mdateForTL8.setText(timeline_date_get(now_value_int - 7));

            mvalueForTL8 = (TextView) getActivity().findViewById(R.id.date_sleep_time8);
            mvalueForTL8.setText(timeline_value_get(now_value_int - 7));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.sleep_timeline, container, false);

        mTimeSleepText = (TextView) rootView.findViewById(R.id.text_content_time_sleep);

        bt1 = (ImageButton) rootView.findViewById(R.id.sleep_start);
        bt1.setOnClickListener(this);

        bt2 = (ImageButton) rootView.findViewById(R.id.sleep_finish);
        bt2.setOnClickListener(this);

        if (sleep_key_get("now") == "") {
            now_value_int = 0;
        } else {
            now_value_int = Integer.parseInt(sleep_key_get("now"));
        }

        if( sleep_key_get("state") == "") {
            mSleepState = 0;
            sleep_key_save("state",mSleepState);
        } else {
            mSleepState = Integer.parseInt(sleep_key_get("state"));
        }

        if(mSleepState==1){
            mTimeSleepText.setText("Now ReCording ... press Stop ");
        }else if(mSleepState==0){
            mTimeSleepText.setText("Press Start ! ");
        }

        mdateForTL1 = (TextView) rootView.findViewById(R.id.date_sleep1);
        mdateForTL1.setText(timeline_date_get(now_value_int-1));

        mvalueForTL1 = (TextView) rootView.findViewById(R.id.date_sleep_time1);
        mvalueForTL1.setText(timeline_value_get(now_value_int-1));

        mdateForTL2 = (TextView) rootView.findViewById(R.id.date_sleep2);
        mdateForTL2.setText(timeline_date_get(now_value_int-2));

        mvalueForTL2 = (TextView) rootView.findViewById(R.id.date_sleep_time2);
        mvalueForTL2.setText(timeline_value_get(now_value_int-2));

        mdateForTL3 = (TextView) rootView.findViewById(R.id.date_sleep3);
        mdateForTL3.setText(timeline_date_get(now_value_int - 3));

        mvalueForTL3 = (TextView) rootView.findViewById(R.id.date_sleep_time3);
        mvalueForTL3.setText(timeline_value_get(now_value_int - 3));

        mdateForTL4 = (TextView) rootView.findViewById(R.id.date_sleep4);
        mdateForTL4.setText(timeline_date_get(now_value_int - 4));

        mvalueForTL4 = (TextView) rootView.findViewById(R.id.date_sleep_time4);
        mvalueForTL4.setText(timeline_value_get(now_value_int - 4));

        mdateForTL5 = (TextView) rootView.findViewById(R.id.date_sleep5);
        mdateForTL5.setText(timeline_date_get(now_value_int - 5));

        mvalueForTL5 = (TextView) rootView.findViewById(R.id.date_sleep_time5);
        mvalueForTL5.setText(timeline_value_get(now_value_int - 5));

        mdateForTL6 = (TextView) rootView.findViewById(R.id.date_sleep6);
        mdateForTL6.setText(timeline_date_get(now_value_int - 6));

        mvalueForTL6 = (TextView) rootView.findViewById(R.id.date_sleep_time6);
        mvalueForTL6.setText(timeline_value_get(now_value_int - 6));

        mdateForTL7 = (TextView) rootView.findViewById(R.id.date_sleep7);
        mdateForTL7.setText(timeline_date_get(now_value_int - 7));

        mvalueForTL7 = (TextView) rootView.findViewById(R.id.date_sleep_time7);
        mvalueForTL7.setText(timeline_value_get(now_value_int - 7));

        mdateForTL8 = (TextView) rootView.findViewById(R.id.date_sleep8);
        mdateForTL8.setText(timeline_date_get(now_value_int - 8));

        mvalueForTL8 = (TextView) rootView.findViewById(R.id.date_sleep_time8);
        mvalueForTL8.setText(timeline_value_get(now_value_int - 8));


        return rootView;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.sleep_start) {
            if (mSleepState == 1) {
                Toast.makeText(getActivity(), "oh ! already start button pressed ! if you want to finish , press the finish button .. ", Toast.LENGTH_SHORT).show();
            }else{
                if (sleep_key_get("now") != "") {
                    now_value = sleep_key_get("now");
                    now_value_int = Integer.parseInt(now_value);
                } else {
                    sleep_key_save("now", 1);
                    now_value_int = 1;
                }

                StartTime = new Date();
                STime = formatter.format(StartTime);
                useStartTime = formatForUse.format(StartTime);
                SaveTime = formatSave.format(StartTime);

                start_sleep_value_save("start", useStartTime);
                timeline_date_save(now_value_int, SaveTime);

                mTimeSleepText = (TextView) getActivity().findViewById(R.id.text_content_time_sleep);
                mTimeSleepText.setText(" Sleep time Recording Start\n\n Good Night^.^ ! ");
                mSleepState = 1;
                sleep_key_save("state", mSleepState);

                Toast.makeText(getActivity(), "Sleep Start Time : " + STime, Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.sleep_finish) {
            if(mSleepState==0){
                Toast.makeText(getActivity(), "oh ! already finish button pressed ! if you want to start , press the start button .. ", Toast.LENGTH_SHORT).show();
            }else {
                FinTime = new Date();
                FTime = formatter.format(FinTime);
                useFinTime = formatForUse.format(FinTime);

                String start_result_get = start_sleep_value_get("start");

                String[] start_result = start_result_get.split("/");

                for (int i = 0; i < start_result.length; i++) {
                    array_s[i] = Integer.parseInt(start_result[i]);
                }

                String[] fin_result = useFinTime.split("/");

                for (int i = 0; i < fin_result.length; i++) {
                    array_f[i] = Integer.parseInt(fin_result[i]);
                }

                if ((array_s[0] < array_f[0]) || (array_s[1] < array_f[1])) {
                    Sleep_Time = ((24 * 60) - ((array_f[2] * 60) + array_f[3])) + ((array_f[2] * 60) + array_f[3]);
                } else {
                    Sleep_Time = (((array_f[2]) * 60) + array_f[3]) - (((array_s[2]) * 60) + array_s[3]);
                }
                int Sleep_Hour = (Sleep_Time / 60);
                int Sleep_min = (Sleep_Time % 60);

                mTimeSleepText = (TextView) getActivity().findViewById(R.id.text_content_time_sleep);
                mTimeSleepText.setText("Good Morning *^0^* \n your sleep time is \n"+Integer.toString(Sleep_Hour) + " H " + Integer.toString(Sleep_min) + " M ");


                Toast.makeText(getActivity(), "Sleep Finish  : " + Sleep_Hour + " Hour " + Sleep_min + " Minute !", Toast.LENGTH_SHORT).show();
                timeline_value_save(now_value_int, Sleep_Time);

                onTImeline(now_value_int);

                now_value_int++;
                sleep_key_save("now", now_value_int);
                now_value = sleep_key_get("now");

                mSleepState = 0;
                sleep_key_save("state", mSleepState);
            }
        }
    }



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


}



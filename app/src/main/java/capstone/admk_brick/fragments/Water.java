package capstone.admk_brick.fragments;

/**
 * Created by jaeho on 2015-05-11.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import capstone.admk_brick.R;
import capstone.admk_brick.contents.ContentManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Water extends Fragment implements IAdapterListener, View.OnClickListener,SensorEventListener {
    private static final String TAG = "WaterFragment";
    private Context mContext = null;
    private IFragmentListener mFragmentListener = null;

    private ContentManager mContentManager = null;

    private long lastTime;
    private float speed,lastX,lastY,x,y;

    private static final int SHAKE_THRESHOLD = 1500;
    private static final int DATA_X = SensorManager.AXIS_X;
    private static final int DATA_Y = SensorManager.AXIS_Y;

    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;

    private TextView mCupsWaterText = null;
    private ImageView mCupsImage = null;
    private TextView mdateForTL1, mdateForTL2, mdateForTL3, mdateForTL4, mdateForTL5, mdateForTL6, mdateForTL7, mdateForTL8 = null;
    private TextView mvalueForTL1, mvalueForTL2, mvalueForTL3, mvalueForTL4, mvalueForTL5, mvalueForTL6, mvalueForTL7, mvalueForTL8 = null;

    SimpleDateFormat formatSave = new SimpleDateFormat( "yyyy . MM . dd ");

    int array_s[] = new int[4];

//    ActivityManager activityManager = (ActivityManager)mContext.getSystemService(mContext.ACTIVITY_SERVICE);


    Date Time;
    String saveTime;

    String now_value ;
    int now_value_int = 0;
    static int cups_today = 0;
    static int now_water_on=0;



    public Water() {
    }


    public Water(Context c, IFragmentListener l) {
        mContext = c;
        mFragmentListener = l;
    }

    public void onStart() {
        super.onStart();

        sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor,
                    SensorManager.SENSOR_DELAY_GAME);
    }
    public void onResume(){
        super.onResume();
    }
 /*   @Override
   public void onStart() {
        super.onStart();
        if (accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor,
                    SensorManager.SENSOR_DELAY_GAME);
    } */

    @Override
    public void onStop() {
        super.onStop();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }
    public void onPause(){
        super.onPause();
    }
    public void onDestroyView(){
        super.onDestroyView();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 100) {
                lastTime = currentTime;
                x = event.values[SensorManager.AXIS_X];
                y = event.values[SensorManager.AXIS_Y];

                speed = Math.abs(x + y - lastX - lastY ) / gabOfTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    //       List<ActivityManager.RunningTaskInfo> infoList = activityManager.getRunningTasks(1);
                    //       ComponentName topActivity = infoList.get(0).topActivity;
                    //        String topactivityname = topActivity.getPackageName();

                    //  if(now_water_on==1){
                    cups_today++;
                    mCupsWaterText.setText(cups_today+" Cups ! ");
                    Toast.makeText(getActivity(), cups_today + " Cups ! " , Toast.LENGTH_SHORT).show();

                    Time = new Date();
                    saveTime =formatSave.format(Time);
                    cups_today = dater_value_in(saveTime,cups_today);
                    now_water_save("now",cups_today);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //    }
                }

                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
            }

        }

    }


    private String water_key_get(String key) {
        SharedPreferences water_key = mContext.getSharedPreferences("water_key", Context.MODE_PRIVATE);
        String result = water_key.getString(key, "");
        return result;
    }

    private void water_key_save(String key, int value) {
        SharedPreferences water_key = mContext.getSharedPreferences("water_key", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = water_key.edit();
        editor.putString(key, String.valueOf(value));
        editor.commit();
    }

    ////////////////////////////////////////////////
    private String water_date_get(int key) {
        SharedPreferences water_date = mContext.getSharedPreferences("water_date", Context.MODE_PRIVATE);
        String result = water_date.getString(Integer.toString(key), "");
        return result;
    }

    private void water_date_save(int key, String value) {
        SharedPreferences water_date = mContext.getSharedPreferences("water_date", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = water_date.edit();
        editor.putString(Integer.toString(key), value);
        editor.commit();
    }

    ////////////////////////////////////////////////
    private String water_value_get(int key) {
        SharedPreferences water_value = mContext.getSharedPreferences("water_value", Context.MODE_PRIVATE);
        String result = water_value.getString(Integer.toString(key), "");
        return result;
    }

    private void water_value_save(int key, int value) {
        SharedPreferences water_value = mContext.getSharedPreferences("water_value", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = water_value.edit();
        editor.putString(Integer.toString(key), String.valueOf(value));
        editor.commit();
    }

    ////////////////////////////////////////////
    private String start_water_value_get(String key) {
        SharedPreferences start_sleep_value = mContext.getSharedPreferences("start_water_value", Context.MODE_PRIVATE);
        String result = start_sleep_value.getString(key, "");
        return result;
    }

    private void start_water_value_save(String key, String value) {
        SharedPreferences start_sleep_value = mContext.getSharedPreferences("start_water_value", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = start_sleep_value.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /////////////////////////////////////////////

    private String now_water_get(String key) {
        SharedPreferences now_water = mContext.getSharedPreferences("now_water", Context.MODE_PRIVATE);
        String result = now_water.getString(key, "");
        return result;
    }

    private void now_water_save(String key, int value) {
        SharedPreferences now_water = mContext.getSharedPreferences("now_water", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = now_water.edit();
        editor.putString(key, String.valueOf(value));
        editor.commit();
    }


    public void onTImeline(int now_value_int) {
        if(now_value_int==0){

        }else {
            mdateForTL1 = (TextView) getActivity().findViewById(R.id.date_water1);
            mdateForTL1.setText(water_date_get(now_value_int));

            mvalueForTL1 = (TextView) getActivity().findViewById(R.id.date_water_time1);
            mvalueForTL1.setText(water_value_get(now_value_int));

            mdateForTL2 = (TextView) getActivity().findViewById(R.id.date_water2);
            mdateForTL2.setText(water_date_get(now_value_int - 1));

            mvalueForTL2 = (TextView) getActivity().findViewById(R.id.date_water_time2);
            mvalueForTL2.setText(water_value_get(now_value_int - 1));

            mdateForTL3 = (TextView) getActivity().findViewById(R.id.date_water3);
            mdateForTL3.setText(water_date_get(now_value_int - 2));

            mvalueForTL3 = (TextView) getActivity().findViewById(R.id.date_water_time3);
            mvalueForTL3.setText(water_value_get(now_value_int - 2));

            mdateForTL4 = (TextView) getActivity().findViewById(R.id.date_water4);
            mdateForTL4.setText(water_date_get(now_value_int - 3));

            mvalueForTL4 = (TextView) getActivity().findViewById(R.id.date_water_time4);
            mvalueForTL4.setText(water_value_get(now_value_int - 3));

            mdateForTL5 = (TextView) getActivity().findViewById(R.id.date_water5);
            mdateForTL5.setText(water_date_get(now_value_int - 4));

            mvalueForTL5 = (TextView) getActivity().findViewById(R.id.date_water_time5);
            mvalueForTL5.setText(water_value_get(now_value_int - 4));

            mdateForTL6 = (TextView) getActivity().findViewById(R.id.date_water6);
            mdateForTL6.setText(water_date_get(now_value_int - 5));

            mvalueForTL6 = (TextView) getActivity().findViewById(R.id.date_water_time6);
            mvalueForTL6.setText(water_value_get(now_value_int - 5));

            mdateForTL7 = (TextView) getActivity().findViewById(R.id.date_water7);
            mdateForTL7.setText(water_date_get(now_value_int - 6));

            mvalueForTL7 = (TextView) getActivity().findViewById(R.id.date_water_time7);
            mvalueForTL7.setText(water_value_get(now_value_int - 6));

            mdateForTL8 = (TextView) getActivity().findViewById(R.id.date_water8);
            mdateForTL8.setText(water_date_get(now_value_int - 7));

            mvalueForTL8 = (TextView) getActivity().findViewById(R.id.date_water_time8);
            mvalueForTL8.setText(water_value_get(now_value_int - 7));
        }

    }

    public int dater_value_in(String date, int value){ // 비교할 것만 들어옴 save는 여기서 다 해줘야댐
        int state_date;
        boolean compare_int;
        String get_start ;

        get_start = start_water_value_get("start");
        //get_start와 date 비교 compare int = 0/1
        compare_int = get_start.equals(date);

        //문자열 비교로 date/get_start 맞으면 state_date =value 아니면 0
        if(compare_int==true){ //맞는 경우
            start_water_value_save("start", saveTime);
            water_date_save(now_value_int, saveTime);
            water_value_save(now_value_int,value);

            state_date = value;
        }else{// 아닌 경우
            now_value_int++;
            water_key_save("now", now_value_int);
            Time = new Date();
            saveTime = formatSave.format(Time);

            start_water_value_save("start", saveTime);
            water_date_save(now_value_int, saveTime);
            water_value_save(now_value_int,0);

            state_date=0;
        }
        onTImeline(now_value_int);

        return state_date;
    }
    private ImageButton bt1 = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.water_timeline, container, false);

        if (water_key_get("now") == "") {
            now_value_int = 0;
            water_key_save("now", now_value_int);

        } else {
            now_value_int = Integer.parseInt(water_key_get("now"));
        }
        //    mCupsImage = (ImageView) getActivity().findViewById(R.id.text_content_water);

        if(now_water_get("now") == ""){
            now_water_save("now",0);
            cups_today = 0;
        } else if (Integer.parseInt(now_water_get("now"))==0){
            cups_today = 0;
        }else {
            cups_today = Integer.parseInt(now_water_get("now"));
        }
        mdateForTL1 = (TextView) rootView.findViewById(R.id.date_water1);
        mdateForTL1.setText(water_date_get(now_value_int));

        mvalueForTL1 = (TextView) rootView.findViewById(R.id.date_water_time1);
        mvalueForTL1.setText(water_value_get(now_value_int));

        mdateForTL2 = (TextView) rootView.findViewById(R.id.date_water2);
        mdateForTL2.setText(water_date_get(now_value_int - 1));

        mvalueForTL2 = (TextView) rootView.findViewById(R.id.date_water_time2);
        mvalueForTL2.setText(water_value_get(now_value_int - 1));

        mdateForTL3 = (TextView) rootView.findViewById(R.id.date_water3);
        mdateForTL3.setText(water_date_get(now_value_int - 2));

        mvalueForTL3 = (TextView) rootView.findViewById(R.id.date_water_time3);
        mvalueForTL3.setText(water_value_get(now_value_int - 2));

        mdateForTL4 = (TextView) rootView.findViewById(R.id.date_water4);
        mdateForTL4.setText(water_date_get(now_value_int - 3));

        mvalueForTL4 = (TextView) rootView.findViewById(R.id.date_water_time4);
        mvalueForTL4.setText(water_value_get(now_value_int - 3));

        mdateForTL5 = (TextView) rootView.findViewById(R.id.date_water5);
        mdateForTL5.setText(water_date_get(now_value_int - 4));

        mvalueForTL5 = (TextView) rootView.findViewById(R.id.date_water_time5);
        mvalueForTL5.setText(water_value_get(now_value_int - 4));

        mdateForTL6 = (TextView) rootView.findViewById(R.id.date_water6);
        mdateForTL6.setText(water_date_get(now_value_int - 5));

        mvalueForTL6 = (TextView) rootView.findViewById(R.id.date_water_time6);
        mvalueForTL6.setText(water_value_get(now_value_int - 5));

        mdateForTL7 = (TextView) rootView.findViewById(R.id.date_water7);
        mdateForTL7.setText(water_date_get(now_value_int - 6));

        mvalueForTL7 = (TextView) rootView.findViewById(R.id.date_water_time7);
        mvalueForTL7.setText(water_value_get(now_value_int - 6));

        mdateForTL8 = (TextView) rootView.findViewById(R.id.date_water8);
        mdateForTL8.setText(water_date_get(now_value_int - 7));

        mvalueForTL8 = (TextView) rootView.findViewById(R.id.date_water_time8);
        mvalueForTL8.setText(water_value_get(now_value_int - 7));



        mCupsWaterText = (TextView) rootView.findViewById(R.id.text_content_cup_water);
        mCupsWaterText.setText(cups_today+" Cups ! ");
        bt1 = (ImageButton) rootView.findViewById(R.id.text_content_water);
        bt1.setOnClickListener(this);

        return rootView;
    }

    public void onClick(View v){
        if(v.getId()==R.id.text_content_water){
            cups_today --;
            if(cups_today<0)
                cups_today=0;
            mCupsWaterText.setText(cups_today+" Cups ! ");
            Toast.makeText(getActivity(), cups_today + " Cups ! " , Toast.LENGTH_SHORT).show();

            Time = new Date();
            saveTime =formatSave.format(Time);
            cups_today = dater_value_in(saveTime,cups_today);
            now_water_save("now",cups_today);
            onTImeline(now_value_int);
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


}
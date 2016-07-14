package capstone.admk_brick.fragments;

/**
 * Created by jaeho on 2015-05-10.
 */
import capstone.admk_brick.R;
import capstone.admk_brick.R.id;
import capstone.admk_brick.R.layout;
import capstone.admk_brick.logic.Analyzer;
import capstone.admk_brick.utils.AppSettings;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class LLSettingsFragment extends Fragment {

    private Context mContext = null;
    private IFragmentListener mFragmentListener = null;

    private CheckBox mCheckBackground;
    private EditText mEditWeight;

    /////
    private EditText mEditStep;
    private EditText mEditHeight;
    private EditText mEditAge;

    static int Recommend_Use_Age;
    static int Recommend_Use_Step;


    public LLSettingsFragment(Context c, IFragmentListener l) {
        mContext = c;
        mFragmentListener = l;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        AppSettings.initializeAppSettings(mContext);

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        // 'Run in background' setting
        mCheckBackground = (CheckBox) rootView.findViewById(R.id.check_background_service);
        mCheckBackground.setChecked(AppSettings.getBgService());
        mCheckBackground.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppSettings.setSettingsValue(AppSettings.SETTINGS_BACKGROUND_SERVICE, isChecked, 0, null);
                mFragmentListener.OnFragmentCallback(IFragmentListener.CALLBACK_RUN_IN_BACKGROUND, 0, 0, null, null, null);
            }
        });

        // User's weight input form
        mEditWeight = (EditText) rootView.findViewById(R.id.edit_weight);
        mEditWeight.setText(Integer.toString(AppSettings.getWeight()));
        mEditWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 0) {
                    int weight = Integer.parseInt(s.toString());
                    if (weight > 0 && weight < 1000) {
                        AppSettings.setSettingsValue(AppSettings.SETTINGS_WEIGHT, true, weight, null);
                        Analyzer.setWeight(AppSettings.getWeight());
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ////////////////////step
        // User's step input form
        mEditStep = (EditText) rootView.findViewById(R.id.edit_step);
        mEditStep.setText(Integer.toString(AppSettings.getStep()));
        mEditStep.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null && s.length() > 0) {
                    int step = Integer.parseInt(s.toString());
                    Recommend_Use_Step = step;
                    if(step > 0) {
                        AppSettings.setSettingsValue(AppSettings.SETTINGS_STEP, true, step, null);
                        Analyzer.setStep(AppSettings.getStep());
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ///////////////////
        // User's Height input form
        mEditHeight = (EditText) rootView.findViewById(R.id.edit_height);
        mEditHeight.setText(Integer.toString(AppSettings.getHeight()));
        mEditHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null && s.length() > 0) {
                    int height = Integer.parseInt(s.toString());
                    if(height > 0) {
                        AppSettings.setSettingsValue(AppSettings.SETTINGS_HEIGHT, true, height, null);
                        Analyzer.setHeight(AppSettings.getHeight());
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ///////////////////

        // User's Age input form
        mEditAge = (EditText) rootView.findViewById(R.id.edit_age);
        mEditAge.setText(Integer.toString(AppSettings.getAge()));
        mEditAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null && s.length() > 0) {
                    int age = Integer.parseInt(s.toString());
                    //Recommend_Use_Age = age;
                    if(age > 0) {
                        AppSettings.setSettingsValue(AppSettings.SETTINGS_AGE, true, age, null);
                        Analyzer.setAge(AppSettings.getAge());
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        return rootView;
    }


}

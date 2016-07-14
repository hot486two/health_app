package capstone.admk_brick.utils;

/**
 * Created by jaeho on 2015-05-10.
 */
import android.content.Context;
import android.content.SharedPreferences;

public class AppSettings {

    // Constants
    public static final int SETTINGS_BACKGROUND_SERVICE = 1;
    public static final int SETTINGS_WEIGHT = 2;

    //
    public static final int SETTINGS_STEP = 3;
    public static final int SETTINGS_AGE = 5;
    public static final int SETTINGS_HEIGHT = 4;


    private static boolean mIsInitialized = false;
    private static Context mContext;

    // Setting values
    private static boolean mUseBackgroundService;
    private static int mWeight;
    //
    private static int mStep;
    private static int mHeight;
    private static int mAge;


    public static void initializeAppSettings(Context c) {
        if(mIsInitialized)
            return;

        mContext = c;

        // Load setting values from preference
        mUseBackgroundService = loadBgService();			//true or false
        mWeight = loadWeight();								//get weight from SharedPreferences
        mStep = loadStep();
        mHeight = loadHeight();
        mAge = loadAge();

        mIsInitialized = true;
    }

    // Remember setting value
    public static void setSettingsValue(int type, boolean boolValue, int intValue, String stringValue) {
        if(mContext == null)
            return;

        SharedPreferences prefs = mContext.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        switch(type) {
            case SETTINGS_BACKGROUND_SERVICE:
                editor.putBoolean(Constants.PREFERENCE_KEY_BG_SERVICE, boolValue);
                editor.commit();
                mUseBackgroundService = boolValue;
                break;
            case SETTINGS_WEIGHT:
                editor.putInt(Constants.PREFERENCE_KEY_WEIGHT, intValue);
                editor.commit();
                mWeight = intValue;
                break;
            case SETTINGS_STEP:
                editor.putInt(Constants.PREFERENCE_KEY_STEP, intValue);
                editor.commit();
                mStep = intValue;
                break;
            case SETTINGS_HEIGHT:
                editor.putInt(Constants.PREFERENCE_KEY_HEIGHT, intValue);
                editor.commit();
                mHeight = intValue;
                break;
            case SETTINGS_AGE:
                editor.putInt(Constants.PREFERENCE_KEY_AGE, intValue);
                editor.commit();
                mAge = intValue;
                break;

            default:
                editor.commit();
                break;
        }
    }


    /**
     * Load 'Run in background' setting value from preferences
     * @return	boolean		is true
     *
     * mContext.getSharedPreferences ->
     * Retrieve and hold the contents of the preferences file 'name',
     * returning a SharedPreferences through which you can retrieve and modify its values
     *
     * getBoolean ->
     * key : The name of the preference to retrieve.
     * defValue : Value to return if this preference does not exist.
     *
     */
    public static boolean loadBgService() {

        SharedPreferences prefs = mContext.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        boolean isTrue = prefs.getBoolean(Constants.PREFERENCE_KEY_BG_SERVICE, false);
        return isTrue;
    }

    /**
     * Returns 'Run in background' setting
     * @return	boolean		is true
     */
    public static boolean getBgService() {
        return mUseBackgroundService;
    }

    /**
     * Load 'Run in background' setting value from preferences
     * @return		User's weight
     */
    public static int loadWeight() {
        SharedPreferences prefs = mContext.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(Constants.PREFERENCE_KEY_WEIGHT, 68);
    }

    public static int loadStep() {
        SharedPreferences prefs = mContext.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(Constants.PREFERENCE_KEY_STEP, 50);
    }

    public static int getStep() {
        return mStep;
    }

    /**
     * Returns 'Run in background' setting
     * @return	int		User's weight
     */
    public static int getWeight() {
        return mWeight;
    }

    ////

    public static int loadHeight() {
        SharedPreferences prefs = mContext.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(Constants.PREFERENCE_KEY_HEIGHT, 170);
    }

    public static int getHeight() {
        return mHeight;
    }

    public static int loadAge() {
        SharedPreferences prefs = mContext.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(Constants.PREFERENCE_KEY_AGE, 25);
    }

    public static int getAge() {
        return mAge;
    }

}

package capstone.admk_brick;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import capstone.admk_brick.contents.ActivityReport;
import capstone.admk_brick.contents.ContentObject;
import capstone.admk_brick.fragments.GraphFragment;
import capstone.admk_brick.fragments.IFragmentListener;
import capstone.admk_brick.fragments.LLFragmentAdapter;
import capstone.admk_brick.fragments.TimelineFragment;
import capstone.admk_brick.service.RetroBandService;
import capstone.admk_brick.utils.AppSettings;
import capstone.admk_brick.utils.Constants;
import capstone.admk_brick.utils.Logs;
import capstone.admk_brick.utils.RecycleUtils;
import capstone.admk_brick.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener, IFragmentListener {

    // Debugging
    private static final String TAG = "RetroWatchActivity";

    // Context, System
    private Context mContext;								//API - import android.content.Context
    private RetroBandService mService;					//make class - service.RetroBandService
    private Utils mUtils;									//make class - utils.Utils
    private ActivityHandler mActivityHandler;			//make class - in this java file

    // Global

    // UI stuff
    private FragmentManager mFragmentManager;			//API - import android.support.v4.app.FragmentManager
    private LLFragmentAdapter mSectionsPagerAdapter;	//make class - fragments.LLFragmentAdapter
    private ViewPager mViewPager;							//API - import android.support.v4.view.ViewPager

    private ImageView mImageBT = null;					//API - import android.widget.ImageView;
    private TextView mTextStatus = null;					//API - import android.widget.TextView;

    // Refresh timer
    private Timer mRefreshTimer = null;					//API - import java.util.Timer;



    /*****************************************************
     *	 Overrided methods
     ******************************************************/


    //무조건 Activity가 처음 실행될 때 호출된다.초기화
    //Activity가 생성될때 호출
    //Activity 상태변화 : 생성->활성(Active)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //----- System, Context
		/*
		Android에 Context는 시스템에 연결해 주는 어플리케이션에 고유 ID라고 생각하시면 됩니다.
		보통 다른 프로그램 언어에서는 어플리케이션 자체가 시스템에게 식별 받을 수 있고 별도의 작업 없이도
		시스템에서 제공하는 기능들을 사용 할 수 있게 됩니다.
		하지만 Android에서는 어플리케이션 자체가 시스템에게 식별 받을 수 없고
		Context를 통해 식별을 받은 후 시스템에서 제공하는 기능들을 사용 할 수 있게 됩니다.
		 Activity 자체도 Context의 자손이기 때문에 Context 라고 볼 수 있고요.
		 this를 넣어 준다는 건 해당 어플리케이션의 Context를 넣어 준다
		 */
        mContext = this;//.getApplicationContext();		액티비티에서 this를 호출하면 자기 자신인 액티비티를 참조.
        //액티비티는 Context를 상속받았기 때문에 Context형의 레퍼런스에 할당할 수 있다

        mActivityHandler = new ActivityHandler();		//for Activity handler--밑에 다른 class로 짜놨음
        AppSettings.initializeAppSettings(mContext);		//make class-import utils.AppSettings - method
        //for getting prior data(today_step, weight)

        setContentView(R.layout.activity_main);

        // Load static utilities
        mUtils = new Utils(mContext);						//????????Utils's methods never used..

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);		//make a menu tap on the ActionBar

        // Create the adapter that will return a fragment for each of the primary sections of the app.
        mFragmentManager = getSupportFragmentManager();				//for access to FragmentManager in Activity
        mSectionsPagerAdapter = new LLFragmentAdapter(mFragmentManager, mContext, this, mActivityHandler);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);			//import android.support.v4.view.ViewPager;
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            actionBar.addTab(actionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }

        // Setup views

        mImageBT = (ImageView) findViewById(R.id.status_title);
        mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_invisible));

        mTextStatus = (TextView) findViewById(R.id.status_text);
        mTextStatus.setText(getResources().getString(R.string.bt_state_init));

        // Do data initialization after service started and binded
        doStartService();
    }


    /*
    Activity가 초기 실행 후, 화면의 전면으로 나타날 때 onCreate()이후에 호출된다
    */
    @Override
    public synchronized void onStart() {
        super.onStart();
    }
    /*
    Activity가 사용자와 상호작용을 중단할때 호출
    거의 모든 경우에 onStop(), onDestroy()가 호출되기 이전에 호출된다.
    Activity가 사용자의 시선에서 가려지는 경우에 호출된다고 생각하면 된다.
    Activity 상태변화 : 활성(Active)->정지(Stopped)
    */
    @Override
    public synchronized void onPause() {
        super.onPause();
    }

    /*
    Activity가 화면에서 사라질때 호출
    Activity 상태변화 : 정지(Stopped)->정지(Stopped)
    액티비티가 화면에서 보이지 않다가 다시 나타나는 것을 의미합니다
    ex)하드웨어 HOME버튼을 눌렀을 때와 SMS수신, 전화수신, 다른 App실행할 때 호출된다
    */
    @Override
    public void onStop() {
        // Stop the timer
        if(mRefreshTimer != null) {
            mRefreshTimer.cancel();
            mRefreshTimer = null;
        }
        super.onStop();
    }

    /*
    * Activity가 종료될때 호출
    * Activity 상태변화 : 정지(Stopped)->소멸(Destroy)
    * */
    @Override
    public void onDestroy() {
        super.onDestroy();
        finalizeActivity();
    }


    /*
    * This is called when the overall system is running low on memory, and actively running processes should trim their memory usage
    */
    @Override
    public void onLowMemory (){
        super.onLowMemory();
        // onDestroy is not always called when applications are finished by Android system.
        finalizeActivity();
    }


    //touch the android menu ->  call the something(make discoverable)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // 옵션메뉴 추가
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    /*
     * Initialize the contents of the Activity's standard options menu.
     * OptionMenu 아이템이 선택될 때 호출 된다
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:
                // Launch the DeviceListActivity to see devices and do scan
                doScan();
                return true;
            case R.id.action_discoverable:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();		// TODO: Disable this line to run below code
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        // This prevents reload after configuration changes
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Implements TabListener
     */
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void OnFragmentCallback(int msgType, int arg0, int arg1, String arg2, String arg3, Object arg4) {
        switch(msgType) {
            case IFragmentListener.CALLBACK_RUN_IN_BACKGROUND:
                if(mService != null)
                    mService.startServiceMonitoring();
                break;

            default:
                break;
        }
    }


    /*****************************************************
     *	Private methods
     ******************************************************/

    /**
     * Service connection
     * ServiceConnection 인터페이스를 구현하는 객체 생성
     * */
    private ServiceConnection mServiceConn = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.d(TAG, "Activity - Service connected");

            mService = ((RetroBandService.LLServiceBinder) binder).getService();

            // Activity couldn't work with mService until connections are made
            // So initialize parameters and settings here, not while running onCreate()
            initialize();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    /**
     * Start service if it's not running
     */
    // Service는 안드로이드 Application을 구성하는 4가지 컴포넌트 중에 하나이며,
    // Activity처럼 사용자와 상호작용 하는 컴포넌트가 아니고,
    // Background(화면뒷단)에서 동작하는 컴포넌트를 말합니다.
	/*	service life cycle -> Local Service and Remote Cycle

		Local Service = StartService() -> onCreate() -> onResume() -> Service실행 -> 서비스 중단 -> onDestroy() -> 서비스종료
		Remote Serviec = bindService() -> onCreste() -> onBind() ->클라리언스,서비스와 상호작용 -> onUnBind() -> onDestroy() -> 서비스 종료

		여기서는 Local Service와 Remote Service 둘다 사용해야한다.
	*/


    private void doStartService() {
        Log.d(TAG, "# Activity - doStartService()");//API for sending log output
        startService(new Intent(this, RetroBandService.class));//API - import android.content.Context - method
        //startService(Intent service) - Request that a given application service be started.

        bindService(new Intent(this, RetroBandService.class), mServiceConn, Context.BIND_AUTO_CREATE);
    }

    /**
     * Stop the service
     */
    private void doStopService() {
        Log.d(TAG, "# Activity - doStopService()");
        mService.finalizeService();						//RetroBandService's Object mService->method finalizeSerrvice()
        //Save activity report(Save sum of calorie cache to DB) to DB
        //Stop the bluetooth session
        //Stop the timer
        stopService(new Intent(this, RetroBandService.class));
    }

    /**
     * Initialization / Finalization
     */
    private void initialize() {
        Logs.d(TAG, "# Activity - initialize()");
        mService.setupService(mActivityHandler);

        // If BT is not on, request that it be enabled.
        // RetroWatchService.setupBT() will then be called during onActivityResult
        if(!mService.isBluetoothEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
        }

        // Load activity reports and display
        if(mRefreshTimer != null) {
            mRefreshTimer.cancel();
        }

        // Use below timer if you want scheduled job
        //mRefreshTimer = new Timer();
        //mRefreshTimer.schedule(new RefreshTimerTask(), 5*1000);
    }

    private void finalizeActivity() {
        Logs.d(TAG, "# Activity - finalizeActivity()");

        if(!AppSettings.getBgService()) {
            doStopService();
        } else {
        }

        // Clean used resources
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
    }

    /**
     * Launch the DeviceListActivity to see devices and do scan
     * startActivityForResult()->we can get result from another activity
     */
    private void doScan() {
        Intent intent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CONNECT_DEVICE);
    }

    /**
     * Ensure this device is discoverable by others
     */
    private void ensureDiscoverable() {
        if (mService.getBluetoothScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(intent);
        }
    }


    /*****************************************************
     *	Public classes
     ******************************************************/

    /**
     * Receives result from external activity
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logs.d(TAG, "onActivityResult " + resultCode);

        switch(requestCode) {
            case Constants.REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Attempt to connect to the device
                    if(address != null && mService != null)
                        mService.connectDevice(address);
                }
                break;

            case Constants.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a BT session
                    mService.setupBT();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Logs.e(TAG, "BT is not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                }
                break;
        }	// End of switch(requestCode)
    }



    /*****************************************************
     *	Handler, Callback, Sub-classes
     ******************************************************/
	/*
	메인스레드의 UI 작업과 서브(작업)스레드의 백그라운드 작업을 나누어서 진행하게 됩니다.
	메인스레드와 서브스레드 간의 통신을 위해 Handler를 사용하게 되는데 Handler는 메시지큐를 사용한 메시지 전달 방법을 사용하게 됩니다.
	Handler에 Message가 들어오면 순서대로 쌓여서 FIFO(First in First Out) 형태로 메시지를 처리하게 됩니다.
	가장 처음 들어온 Message를 우선 처리하게 됩니다.
	 */
    public class ActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what) {
                // BT state messages
                case Constants.MESSAGE_BT_STATE_INITIALIZED:
                    mTextStatus.setText(getResources().getString(R.string.bt_title) + ": " +
                            getResources().getString(R.string.bt_state_init));
                    mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_invisible));
                    break;
                case Constants.MESSAGE_BT_STATE_LISTENING:
                    mTextStatus.setText(getResources().getString(R.string.bt_title) + ": " +
                            getResources().getString(R.string.bt_state_wait));
                    mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_invisible));
                    break;
                case Constants.MESSAGE_BT_STATE_CONNECTING:
                    mTextStatus.setText(getResources().getString(R.string.bt_title) + ": " +
                            getResources().getString(R.string.bt_state_connect));
                    mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_away));
                    break;
                case Constants.MESSAGE_BT_STATE_CONNECTED:
                    if(mService != null) {
                        String deviceName = mService.getDeviceName();
                        if(deviceName != null) {
                            mTextStatus.setText(getResources().getString(R.string.bt_title) + ": " +
                                    getResources().getString(R.string.bt_state_connected) + " " + deviceName);
                            mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_online));
                        }
                    }
                    break;
                case Constants.MESSAGE_BT_STATE_ERROR:
                    mTextStatus.setText(getResources().getString(R.string.bt_state_error));
                    mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_busy));
                    break;

                // BT Command status
                case Constants.MESSAGE_CMD_ERROR_NOT_CONNECTED:
                    mTextStatus.setText(getResources().getString(R.string.bt_cmd_sending_error));
                    mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_busy));
                    break;

                ////////////////////////////////////////////
                // Contents changed
                /////////////////////////////////////////////

                //칼로리랑 걸음수 지속적으로 업데이트 시켜주는거
                case Constants.MESSAGE_READ_ACCEL_REPORT:
                    ActivityReport ar = (ActivityReport)msg.obj;
                    if(ar != null) {
                        TimelineFragment frg = (TimelineFragment) mSectionsPagerAdapter.getItem(LLFragmentAdapter.FRAGMENT_POS_TIMELINE);
                        frg.showActivityReport(ar);
                    }
                    break;

                //그래프 그리는거
                case Constants.MESSAGE_READ_ACCEL_DATA:
                    ContentObject co = (ContentObject)msg.obj;
                    if(co != null) {
                        GraphFragment frg = (GraphFragment) mSectionsPagerAdapter.getItem(LLFragmentAdapter.FRAGMENT_POS_GRAPH);
                        frg.drawAccelData(co.mAccelData);
                    }
                    break;

                default:
                    break;
            }

            super.handleMessage(msg);
        }
    }	// End of class ActivityHandler

    /**
     * Auto-refresh Timer
     */
    private class RefreshTimerTask extends TimerTask {
        public RefreshTimerTask() {}

        public void run() {
            mActivityHandler.post(new Runnable() {
                public void run() {
                    // TODO:
                    mRefreshTimer = null;
                }
            });
        }
    }

}

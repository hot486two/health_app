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


    //������ Activity�� ó�� ����� �� ȣ��ȴ�.�ʱ�ȭ
    //Activity�� �����ɶ� ȣ��
    //Activity ���º�ȭ : ����->Ȱ��(Active)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //----- System, Context
		/*
		Android�� Context�� �ý��ۿ� ������ �ִ� ���ø����̼ǿ� ���� ID��� �����Ͻø� �˴ϴ�.
		���� �ٸ� ���α׷� ������ ���ø����̼� ��ü�� �ý��ۿ��� �ĺ� ���� �� �ְ� ������ �۾� ���̵�
		�ý��ۿ��� �����ϴ� ��ɵ��� ��� �� �� �ְ� �˴ϴ�.
		������ Android������ ���ø����̼� ��ü�� �ý��ۿ��� �ĺ� ���� �� ����
		Context�� ���� �ĺ��� ���� �� �ý��ۿ��� �����ϴ� ��ɵ��� ��� �� �� �ְ� �˴ϴ�.
		 Activity ��ü�� Context�� �ڼ��̱� ������ Context ��� �� �� �ְ��.
		 this�� �־� �شٴ� �� �ش� ���ø����̼��� Context�� �־� �ش�
		 */
        mContext = this;//.getApplicationContext();		��Ƽ��Ƽ���� this�� ȣ���ϸ� �ڱ� �ڽ��� ��Ƽ��Ƽ�� ����.
        //��Ƽ��Ƽ�� Context�� ��ӹ޾ұ� ������ Context���� ���۷����� �Ҵ��� �� �ִ�

        mActivityHandler = new ActivityHandler();		//for Activity handler--�ؿ� �ٸ� class�� ¥����
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
    Activity�� �ʱ� ���� ��, ȭ���� �������� ��Ÿ�� �� onCreate()���Ŀ� ȣ��ȴ�
    */
    @Override
    public synchronized void onStart() {
        super.onStart();
    }
    /*
    Activity�� ����ڿ� ��ȣ�ۿ��� �ߴ��Ҷ� ȣ��
    ���� ��� ��쿡 onStop(), onDestroy()�� ȣ��Ǳ� ������ ȣ��ȴ�.
    Activity�� ������� �ü����� �������� ��쿡 ȣ��ȴٰ� �����ϸ� �ȴ�.
    Activity ���º�ȭ : Ȱ��(Active)->����(Stopped)
    */
    @Override
    public synchronized void onPause() {
        super.onPause();
    }

    /*
    Activity�� ȭ�鿡�� ������� ȣ��
    Activity ���º�ȭ : ����(Stopped)->����(Stopped)
    ��Ƽ��Ƽ�� ȭ�鿡�� ������ �ʴٰ� �ٽ� ��Ÿ���� ���� �ǹ��մϴ�
    ex)�ϵ���� HOME��ư�� ������ ���� SMS����, ��ȭ����, �ٸ� App������ �� ȣ��ȴ�
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
    * Activity�� ����ɶ� ȣ��
    * Activity ���º�ȭ : ����(Stopped)->�Ҹ�(Destroy)
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
        // �ɼǸ޴� �߰�
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    /*
     * Initialize the contents of the Activity's standard options menu.
     * OptionMenu �������� ���õ� �� ȣ�� �ȴ�
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
     * ServiceConnection �������̽��� �����ϴ� ��ü ����
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
    // Service�� �ȵ���̵� Application�� �����ϴ� 4���� ������Ʈ �߿� �ϳ��̸�,
    // Activityó�� ����ڿ� ��ȣ�ۿ� �ϴ� ������Ʈ�� �ƴϰ�,
    // Background(ȭ��޴�)���� �����ϴ� ������Ʈ�� ���մϴ�.
	/*	service life cycle -> Local Service and Remote Cycle

		Local Service = StartService() -> onCreate() -> onResume() -> Service���� -> ���� �ߴ� -> onDestroy() -> ��������
		Remote Serviec = bindService() -> onCreste() -> onBind() ->Ŭ�󸮾�,���񽺿� ��ȣ�ۿ� -> onUnBind() -> onDestroy() -> ���� ����

		���⼭�� Local Service�� Remote Service �Ѵ� ����ؾ��Ѵ�.
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
	���ν������� UI �۾��� ����(�۾�)�������� ��׶��� �۾��� ����� �����ϰ� �˴ϴ�.
	���ν������ ���꽺���� ���� ����� ���� Handler�� ����ϰ� �Ǵµ� Handler�� �޽���ť�� ����� �޽��� ���� ����� ����ϰ� �˴ϴ�.
	Handler�� Message�� ������ ������� �׿��� FIFO(First in First Out) ���·� �޽����� ó���ϰ� �˴ϴ�.
	���� ó�� ���� Message�� �켱 ó���ϰ� �˴ϴ�.
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

                //Į�θ��� ������ ���������� ������Ʈ �����ִ°�
                case Constants.MESSAGE_READ_ACCEL_REPORT:
                    ActivityReport ar = (ActivityReport)msg.obj;
                    if(ar != null) {
                        TimelineFragment frg = (TimelineFragment) mSectionsPagerAdapter.getItem(LLFragmentAdapter.FRAGMENT_POS_TIMELINE);
                        frg.showActivityReport(ar);
                    }
                    break;

                //�׷��� �׸��°�
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

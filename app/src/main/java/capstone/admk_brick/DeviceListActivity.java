package capstone.admk_brick;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;


/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class DeviceListActivity extends Activity {
    // Debugging
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private BluetoothAdapter mBtAdapter;                                //API- import android.bluetooth.BluetoothAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;        //API - import android.widget.ArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;           //API - import android.widget.ArrayAdapter;
    //어댑터는 리스트 객체를 리스트뷰에서 표시해주는 기능,
    // 즉 리스트 객체의 내용과 리스트 항목의 레이아웃을 연결시켜주는 역할을 합니다

    // UI stuff
    Button mScanButton = null;                                          //API  - import android.widget.Button;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);    //Enable extended window features.
        //It is used for progress bar that simply rotating circle in the top right corner
        setContentView(R.layout.activity_device_list);

        // Set result CANCELED incase the user backs out
        // When an activity exits, it can call setResult(int) to return data back to its parent
        // RESULT_CANCELED - Standard activity result: operation canceled. Constant Value:   0 (0x00000000)
        setResult(Activity.RESULT_CANCELED);

        // Initialize the button to perform device discovery
        mScanButton = (Button) findViewById(R.id.button_scan);
        mScanButton.setOnClickListener(new OnClickListener() {              //Register a callback to be invoked when this view is clicked.
            public void onClick(View v) {
                mNewDevicesArrayAdapter.clear();                            //API - import android.widget.ArrayAdapter;
                //어댑터는 리스트 객체를 리스트뷰에서 표시해주는 기능,
                // 즉 리스트 객체의 내용과 리스트 항목의 레이아웃을 연결시켜주는 역할을 합니다
                //clear() - Remove all elements from the list.
                doDiscovery();
                v.setVisibility(View.GONE);                                   //remove 'Scan for devices' Button
                //v.setVisibility(View.VISIBLE);
            }
        });

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.adapter_device_name);      //위에서 선언은 하고 여기서 새로 생성
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.adapter_device_name);         //위에서 선언은 하고 여기서 새로 생성

        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);                             //List
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);                                              //Sets the adapter that provides the data and the views to represent the data in this widget.
        pairedListView.setOnItemClickListener(mDeviceClickListener);                                        //Register a callback to be invoked when an item in this AdapterView has been clicked.

        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);        //make visible the circle progress bar 원형 그래프 바 표시
        setTitle(R.string.scanning);                        //set the Title - R.string.scanning= "Scanning for devices..."

        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {           //Return true if the local Bluetooth adapter is currently in the device discovery process.
            mBtAdapter.cancelDiscovery();           //Cancel the current device discovery process
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();                //Start the remote device discovery process.
    }

    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            if(info != null && info.length() > 16) {
                String address = info.substring(info.length() - 17);
                Log.d(TAG, "User selected device : " + address);

                // Create the result Intent and include the MAC address
                Intent intent = new Intent();
                intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

                // Set result and finish this Activity
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    };

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
                mScanButton.setVisibility(View.VISIBLE);
            }
        }
    };

}

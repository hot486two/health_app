package capstone.admk_brick.bluetooth;

/**
 * Created by jaeho on 2015-05-10.
 */
import capstone.admk_brick.utils.Constants;


import android.os.Handler;
import android.util.Log;

/**
 * This class is for future use.
 * If you want to send something to remote
 * Add methods in Transaction class
 *
 * @author Administrator
 *
 */
public class TransactionBuilder {

    private static final String TAG = "TransactionBuilder";

    private BluetoothManager mBTManager = null;
    private Handler mHandler = null;

    public TransactionBuilder(BluetoothManager bm, Handler errorHandler) {
        mBTManager = bm;
        mHandler = errorHandler;
    }

    public Transaction makeTransaction() {
        return new Transaction();
    }

    public class Transaction {

        public static final int MAX_MESSAGE_LENGTH = 16;

        // Transaction instance status
        private static final int STATE_NONE = 0;		// Instance created
        private static final int STATE_BEGIN = 1;		// Initialize transaction
        private static final int STATE_SETTING_FINISHED = 2;	// End of setting parameters 
        private static final int STATE_TRANSFERED = 3;	// End of sending transaction data
        private static final int STATE_ERROR = -1;		// Error occurred

        // Transaction parameters
        private int mState = STATE_NONE;
        private byte[] mBuffer = null;
        private String mMsg = null;


        /**
         * Make new transaction instance
         */
        public void begin() {
            mState = STATE_BEGIN;
            mMsg = null;
            mBuffer = null;
        }

        /**
         * Set string to send
         * @param msg	String to send
         */
        public void setMessage(String msg) {
            mMsg = msg;
        }

        /**
         * Ready to send data to remote
         */
        public void settingFinished() {
            mState = STATE_SETTING_FINISHED;
            mBuffer = mMsg.getBytes();
        }

        /**
         * Send packet to remote
         * @return	boolean		is succeeded
         */
        public boolean sendTransaction() {
            if(mBuffer == null || mBuffer.length < 1) {
                Log.e(TAG, "##### Ooooooops!! No sending buffer!! Check command!!");
                return false;
            }

            // TODO: For debug. Comment out below lines if you don't want
			/*
			if(mBuffer.length > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append("Message : ");
				
				for(int i=0; i<mBuffer.length; i++) {
					sb.append(String.format("%02X, ", mBuffer[i]));
				}
				
				Log.d(TAG, " ");
				Log.d(TAG, sb.toString());
			}
			*/

            if(mState == STATE_SETTING_FINISHED) {
                if(mBTManager != null) {
                    // Check that we're actually connected before trying anything
                    if (mBTManager.getState() == BluetoothManager.STATE_CONNECTED) {
                        // Check that there's actually something to send
                        if (mBuffer.length > 0) {
                            // Get the message bytes and tell the BluetoothManager to write
                            mBTManager.write(mBuffer);

                            mState = STATE_TRANSFERED;
                            return true;
                        }
                        mState = STATE_ERROR;
                    }
                    // Report result
                    mHandler.obtainMessage(Constants.MESSAGE_CMD_ERROR_NOT_CONNECTED).sendToTarget();
                }
            }
            return false;
        }


        public byte[] getPacket() {
            if(mState == STATE_SETTING_FINISHED) {
                return mBuffer;
            }
            return null;
        }

    }	// End of class Transaction

}

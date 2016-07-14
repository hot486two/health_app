package capstone.admk_brick.logic;

/**
 * Created by jaeho on 2015-05-10.
 */
import java.util.ArrayList;

import capstone.admk_brick.contents.ActivityReport;
import capstone.admk_brick.contents.ContentObject;
import capstone.admk_brick.utils.Logs;



/* The output scale for any setting is [-32768, +32767] for each of the six axes.
 * The default setting in the I2Cdevlib class is +/- 2g for the accel and +/- 250 deg/sec for the gyro
 * 1g, which is +16384 at a sensitivity of 2g
 */


public class Analyzer {

    private static int nStepCount = 0;
    private static double duCalorie = 0.;
    private static double mWeight = 68.; // unit: kg
	private static double mStep = 50.; // unit: cm
	private static double mHeight = 170.; // unit: cm
	private static double mAge = 25.; // unit: cm
    private static int nLastDetectedTime = 0;
    public static final int SHAKE_THRESHHOLD = 800;

    public static ActivityReport analyzeAccel(ArrayList<ContentObject> objectArray, int samplingInterval, int totalTime) {

        if(objectArray == null || objectArray.size() < 1) {
            return null;
        }

        ActivityReport ar = new ActivityReport();
        ar.mType = ContentObject.CONTENT_TYPE_ACCEL;
        ar.mSamplingInterval = samplingInterval;
        ar.mTotalTime = totalTime;

        // [kbjung]
        ContentObject co1 = objectArray.get(0);				//parameter objectArray

        int idx = co1.mAccelData.length/3;				//co1.mAccelData.length = 60 -> idx=20
															//20번 샘플링

        float [] nX = new float[idx];
        float [] nY = new float[idx];
        float [] nZ = new float[idx];
        float [] n3D = new float[idx];					/*current acceleration including gravity
        													 * co1.mAccelData[i*3] = raw value from arduino
        													 * ex)co1.mAccelData[0] = 13420 -> nX[0] = 0.29523155
        													  * co1.mAccelData[1] = 3652 -> nY[0] = 0.55573356
        													  * co1.mAccelData[2] = 8956 -> nZ[0] = 0.63666743
        													  * n3D[0] = 0.8951798



															*/

		for(int i = 0; i < idx; ++i)						//idx=20
        {
            nX[i] = (float)(co1.mAccelData[i*3]+32768)/65535.f;
            //Logs.d("#"+nX[i]);
            nY[i] = (float)(co1.mAccelData[i*3+1]+32768)/65535.f;
            //Logs.d("#"+nY[i]);
            nZ[i] = (float)(co1.mAccelData[i*3+2]+32768)/65535.f;
            //Logs.d("#"+nZ[i]);

			//Math.sqrt(9)=>3.0
            n3D[i] = (float) Math.sqrt(nX[i]*nX[i]+nY[i]*nY[i]+nZ[i]*nZ[i]);
			//
//Logs.d("#"+nZ[i]);
			//

            Logs.d("#"+n3D[i]);
        }

        //PeakDetector peakDetect = new PeakDetector(nY);
        PeakDetector peakDetect = new PeakDetector(n3D);
        int[] res = peakDetect.process(3, 1.5f);				//0~20번째중 몇번째 인지 리턴. 보통 1개만 리턴. 7이라고 하면 7번째를 나타램.


		for(int kk=0;kk<res.length;kk++){
			Logs.d("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"+res[kk]);
		}


        float fStepTime = 0.f;
        float fStepVelocity = 0.f;
        float fAvgVelocity = 0.f;
        int nTotalSamplingData = 20;

        if(res.length <= 0)
        {
            //ar.mCalorie = 0;
            //return ar;
            return null;
        }

        fStepTime = (res[0] + (nTotalSamplingData-nLastDetectedTime))*0.05f;
		mStep = mStep/100;
        fStepVelocity = 0.5f/fStepTime;
		//fStepVelocity = (float)mStep/fStepTime;
        fAvgVelocity = fStepVelocity;
        for(int i = 1; i < res.length - 1; ++i)
        {
            fStepTime = (res[i+1] - res[i])*0.05f;
            fStepVelocity = 0.5f/fStepTime;
			//fStepVelocity = (float)mStep/fStepTime;
            fAvgVelocity += fStepVelocity;
        }
        fAvgVelocity /= res.length;
        fAvgVelocity *= 3.6f; // convert m/s to km/h

		// 1km/h = 1km/3600s = 1000m/3600s = 1m/3.6s
		//10m/s =1초에 10m -> 1시간=3600초 -> 3600초에 10*3600m = 10*3.6km
        nLastDetectedTime = res[res.length-1];

        nStepCount += res.length;
        ar.mShakeActionCount = nStepCount;

        Logs.d("#");
        Logs.d("# of Xdata: "+nX.length+", shake: "+ar.mShakeActionCount);

        double MET = 1.0;
        if(fAvgVelocity < 2.7)
        {
            MET = 2.3;
        }
        else if(fAvgVelocity < 4)
        {
            MET = 2.9;
        }
        else if(fAvgVelocity < 4.8)
        {
            MET = 3.3;
        }
        else if(fAvgVelocity < 5.5)
        {
            MET = 3.6;
        }
        else if(fAvgVelocity < 10)
        {
            MET = 3.8;
        }
        else if(fAvgVelocity < 16)
        {
            MET = 4.0;
        }

        // 70kg?? ????? 3.5 mph(1.5m/s)?? 30?? ????? ??: 139.65 kcal
        ar.mCalorie = MET*mWeight*(1/3600.)*1000;
        duCalorie += ar.mCalorie;
        ar.mSumOfCalorie = duCalorie;
		
/*
		int nPrevX;
		int nPrevY;
		int nPrevZ;
		int nDiffX;
		int nDiffY;
		int nDiffZ;
		int nPrevDiffX;
		int nPrevDiffY;
		int nPrevDiffZ;

		int nDirection1X = 0;
		int nDirection1Y = 0;
		int nDirection1Z = 0;
		
		int nDirection2X = 0;
		int nDirection2Y = 0;
		int nDirection2Z = 0;

		int idx = co1.mAccelData.length/3;

		nPrevX = co1.mAccelData[idx];
		nPrevY = co1.mAccelData[idx+1];
		nPrevZ = co1.mAccelData[idx+2];

		nPrevDiffX = nPrevX - co1.mAccelData[idx-3];
		nPrevDiffY = nPrevY - co1.mAccelData[idx-2];
		nPrevDiffZ = nPrevZ - co1.mAccelData[idx-1];


		int nThreshold = 100;
		if(Math.abs(nPrevDiffX) > nThreshold)
			nDirection2X = 1;
		else if(Math.abs(nPrevDiffX) < -nThreshold)
			nDirection2X = -1;
		
		if(Math.abs(nPrevDiffY) > nThreshold)
			nDirection2Y = 1;
		else if(Math.abs(nPrevDiffY) < -nThreshold)
			nDirection2Y = -1;
		
		if(Math.abs(nPrevDiffZ) > nThreshold)
			nDirection2Z = 1;
		else if(Math.abs(nPrevDiffZ) < -nThreshold)
			nDirection2Z = -1;


		for(int j=1; j<objectArray.size(); j++) {
			ContentObject co = objectArray.get(j);
			if(j == 0)
				ar.mStartTime = co.mTimeInMilli;
			
			/**
			 * Make your own analyzing code here.
			 */
			/*
			if(co.mAccelData != null) {
				int last_x = 0;
				int last_y = 0;
				int last_z = 0;
				
				// [kbjung]
				nDiffX = co.mAccelData[0] - nPrevX;
				nDiffY = co.mAccelData[1] - nPrevY;
				nDiffZ = co.mAccelData[2] - nPrevZ;
				
				if(nDiffX > nThreshold)
					nDirection1X = 1;
				else if(nDiffX < -nThreshold)
					nDirection1X = -1;
				
				if(nDiffY > nThreshold)
					nDirection1Y = 1;
				else if(nDiffY < -nThreshold)
					nDirection1Y = -1;
				
				if(nDiffZ > nThreshold)
					nDirection1Z = 1;
				else if(nDiffZ < -nThreshold)
					nDirection1Z = -1;
				
				if(nDirection1Y != 0 && nDirection2Y != 0 && nDirection1Y != nDirection2Y)
					nStepCount++;
//					ar.mShakeActionCount++;
				
				nPrevDiffX = nDiffX;
				nPrevDiffY = nDiffY;
				nPrevDiffZ = nDiffZ;
				
				for(int i=3; i<co.mAccelData.length/3; i+=3) {
					int axis_x = co.mAccelData[i];
					int axis_y = co.mAccelData[i+1];
					int axis_z = co.mAccelData[i+2];
					
					// [kbjung]
					nDiffX = axis_x - nPrevX;
					nDiffY = axis_y - nPrevY;
					nDiffZ = axis_z - nPrevZ;
					
					if(nDiffX > nThreshold)
						nDirection1X = 1;
					else if(nDiffX < -nThreshold)
						nDirection1X = -1;
					
					if(nDiffY > nThreshold)
						nDirection1Y = 1;
					else if(nDiffY < -nThreshold)
						nDirection1Y = -1;
					
					if(nDiffZ > nThreshold)
						nDirection1Z = 1;
					else if(nDiffZ < -nThreshold)
						nDirection1Z = -1;

					if(nDirection1Y != 0 && nDirection2Y != 0 && nDirection1Y != nDirection2Y)
						nStepCount++;
					ar.mShakeActionCount = nStepCount;

					nPrevDiffX = nDiffX;
					nPrevDiffY = nDiffY;
					nPrevDiffZ = nDiffZ;
						
//					double difference = 0;
//					
//					if(last_x == 0 && last_y == 0 && last_z == 0) {
//
//					} else {
//						difference = Math.abs(axis_x + axis_y + axis_z - last_x - last_y - last_z) / samplingInterval * 10000;
//						ar.mSumOfDifference += difference;
//						ar.mCount++;
//						
//						if(difference > SHAKE_THRESHHOLD) {
//							// This is shake action
//							ar.mShakeActionCount++;
//						}
//					}
//					last_x = axis_x;
//					last_y = axis_y;
//					last_z = axis_z;
					
					/*
					if(axis_x == 0 && axis_y == 0 && axis_z == 0) {
						previousMagnitude = 0;
					} else {
						difference = Math.sqrt(Math.pow(axis_x, 2) + Math.pow(axis_y, 2) + Math.pow(axis_z, 2));
						if(previousMagnitude > 0) {
							ar.mSumOfDifference += Math.abs(previousMagnitude - difference);
							ar.mCount++;
						}
						previousMagnitude = difference;
					}
					*/
					/*
				}	// End of for loop
			}
			
		}	// End of for loop
//		
//		if(ar.mCount > 0)
//			ar.mAverageDifference = ar.mSumOfDifference / ar.mCount;
//		else
//			ar.mAverageDifference = 0;
		*/

        //ar.mCalorie = Analyzer.calculateCalorie(ar.mShakeActionCount);	// Calculate calorie!!

        return ar;
    }

    @Deprecated
    public static double calculateCalorie(int shakeActionCount) {
        return shakeActionCount*50;
    }

    public static void setWeight(double weight) {
        // Set user weight
        if(weight > 0 && weight < 1000)
            mWeight = weight;
    }

	public static void setStep(double step) {
		// Set user step
		if(step > 0)
			mStep = step;
	}

	public static void setHeight(double height) {
		// Set user Height
		if(height > 0)
			mHeight = height;
	}

	public static void setAge(double age) {
		// Set user age
		if(age > 0)
			mAge = age;
	}


}
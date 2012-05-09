package com.lightbox.android.camera.device;

import static com.lightbox.android.camera.Util.Assert;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;

import com.lightbox.android.camera.CameraHardwareException;

public class PreGingerbreadCameraHolder extends CameraHolder {

	protected PreGingerbreadCameraHolder() {
    	super();
        mNumberOfCameras = 1;
    }
	
	@Override
	public boolean isFrontFacing(int cameraId) {
		return false;
	}

	@Override
	public int getCameraOrientation(int cameraId, int orientationSensorValue) {
		return 90;
	}

	@Override
	public int getNumberOfCameras() {
		return mNumberOfCameras;
	}

	@Override
	public synchronized Camera open(int cameraId) throws CameraHardwareException {
		Assert(mUsers == 0);
        if (mCameraDevice != null && mCameraId != cameraId) {
            mCameraDevice.release();
            mCameraDevice = null;
            mCameraId = -1;
        }
        if (mCameraDevice == null) {
            try {
                Log.v(TAG, "open camera " + cameraId);
                mCameraDevice = android.hardware.Camera.open();
                mCameraId = cameraId;
            } catch (RuntimeException e) {
                Log.e(TAG, "fail to connect Camera", e);
                throw new CameraHardwareException(e);
            }
            mParameters = mCameraDevice.getParameters();
        } else {
            mCameraDevice.lock();
            mCameraDevice.setParameters(mParameters);
        }
        ++mUsers;
        mHandler.removeMessages(RELEASE_CAMERA);
        mKeepBeforeTime = 0;
        return mCameraDevice;
	}
	
	@Override
	public double getAspectRatio() {
		Size size = mParameters.getPictureSize();
		return ((double) size.width / size.height);
	}

	@Override
	public int getFrontFacingCameraId() {
		return 0;
	}

	@Override
	public int getRearFacingCameraId() {
		return 0;
	}
}

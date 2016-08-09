package com.appelsin.olympicsportfacescanner.cam;

import android.hardware.Camera;
import android.util.Log;

/**
 * I manage loading and destroying the camera reference for you
 */
public class FrontCameraRetriever {

    private static final String TAG = FrontCameraRetriever.class.getSimpleName();
    public boolean isFrontal;


    public FaceDetectionCamera DetectFaces(boolean front){
        try {
            int id = 0;
            if(front)
                id = getFrontFacingCameraId();
            else
                id = getBackFacingCameraId();
            Camera camera = Camera.open(id);

            if (camera.getParameters().getMaxNumDetectedFaces() == 0) {
                Log.e(TAG, "Face detection not supported");
                return null;
            }

            return new FaceDetectionCamera(camera);
        } catch (RuntimeException e) {
            Log.e(TAG, "Likely hardware / non released camera / other app fail", e);
            return null;
        }

    }

    private int getFrontFacingCameraId() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int i = 0;
        for (; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                isFrontal = true;
                break;
            }
        }
        return i;
    }
    private int getBackFacingCameraId() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int i = 0;
        for (; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                isFrontal = false;
                break;
            }
        }
        return i;
    }
}

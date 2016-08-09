package com.appelsin.olympicsportfacescanner.cam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;

import com.appelsin.olympicsportfacescanner.CameraSurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Manages the android camera and sets it up for face detection
 * can throw an error if face detection is not supported on this device
 */
public class FaceDetectionCamera implements OneShotFaceDetectionListener.Listener {

    private Camera camera;

    private Listener listener;

    private int degrees = 90;

    private CameraSurfaceView view;

    private Camera.Size mPreviewSize;

    public FaceDetectionCamera(Camera camera) {
        this.camera = camera;
    }
    public void setView(CameraSurfaceView view) {
     this.view = view;
    }
    public void setmPreviewSize(Camera.Size mPreviewSize){
        this.mPreviewSize = mPreviewSize;
    }
    public List<Camera.Size> getSupportedSizes(){
        if(camera!=null)
            return camera.getParameters().getSupportedPreviewSizes();
        return null;
    }

    /**
     * Use this to detect faces when you have a custom surface to display upon
     *
     * @param listener the {@link com.appelsin.ageinthefacesimulator.cam.FaceDetectionCamera.Listener} for when faces are detected
     * @param holder   the {@link SurfaceHolder} to display upon
     */
    public void initialise(Listener listener, SurfaceHolder holder) {
        this.listener = listener;
        try {
            camera.stopPreview();
        } catch (Exception swallow) {
            // ignore: tried to stop a non-existent preview
        }
        try {
            if(mPreviewSize!=null) {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                view.requestLayout();
                camera.setParameters(parameters);
            }
            camera.setDisplayOrientation(degrees);
            camera.setPreviewDisplay(holder);
            camera.startPreview();
            camera.setFaceDetectionListener(new OneShotFaceDetectionListener(this));
            camera.startFaceDetection();
        } catch (IOException e) {
            this.listener.onFaceDetectionNonRecoverableError();
        }
    }

    @Override
    public void onFaceDetected(Camera.Face[] faces) {
        listener.onFaceDetected(faces);
    }

    @Override
    public void onFaceTimedOut() {
        listener.onFaceTimedOut();
    }

    public void recycle() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera=null;
        }
    }
    public void stopRel(){
        if (camera != null) {
            camera.stopFaceDetection();
            camera.stopPreview();
        }
    }

    public void TakePic(final Context ctx, final Rect rect){
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.d("AZAZA", "ЗАШЛО СЮДА КАРОЧИ");
                //String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File( Environment.getExternalStorageDirectory(),File.separator+"AppAcheImages");
                myDir.mkdirs();
                Random rnd = new Random();
                int abc = rnd.nextInt();
                String fname = "Image"+String.valueOf(abc)+".jpg";
                File file = new File(myDir, fname);
                Log.i("CLASS", "" + file);
                Bitmap bmp = null;
                Log.d("FACE RECT: ", " "+String.valueOf(rect.centerX()) + " " + String.valueOf(rect.centerY())+" "+
                        String.valueOf(rect.width()) + " " + String.valueOf(rect.height()));
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bmp = BitmapFactory.decodeByteArray(data , 0, data.length);
                    Bitmap bmp1 = Bitmap.createBitmap(bmp,rect.centerX(),rect.centerY(),
                            rect.width(),rect.height());
                    bmp1.compress(Bitmap.CompressFormat.JPEG,0,out);
                    out.flush();
                    out.close();


                } catch (Exception e) {
                    e.printStackTrace();
                }
                camera.startPreview();
                camera.startFaceDetection();

            }});}

    /*@Override
    public void onPictureTaken(byte[] data, Camera camera) {

    }

    @Override
    public void onShutter() {
        Log.d("onShutter", "taken");
    }*/

    public interface Listener {
        void onFaceDetected(Camera.Face[] faces);

        void onFaceTimedOut();

        void onFaceDetectionNonRecoverableError();

    }

    public void ChangeOrientation() {
        degrees = (degrees==90)?180:90;
        camera.setDisplayOrientation(degrees);
        view.getHolder().setFixedSize(view.getHeight(), view.getWidth());
        view.measure(view.getHeight(), view.getWidth());
    }
    public Camera getCamera(){
        return camera;
    }
}

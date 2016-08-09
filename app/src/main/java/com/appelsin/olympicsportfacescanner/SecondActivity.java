package com.appelsin.olympicsportfacescanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.appelsin.olympicsportfacescanner.cam.FaceDetectionCamera;
import com.appelsin.olympicsportfacescanner.cam.FrontCameraRetriever;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class SecondActivity extends Activity implements FaceDetectionCamera.Listener, View.OnClickListener {

    private static final String TAG = "FDT" + SecondActivity.class.getSimpleName();

    private TextView infoText;
    private Button photo_btn;
    private SurfaceView cameraSurface;
    private FrontCameraRetriever frontCameraRetriever;
    private FaceDetectionCamera camera;
    private FaceOverlayView faceOverlayView;
    private File photoFile;
    private File videoFile;
    private Rect curRect;
    private AdView mAdView;
    private boolean isFace = true;
    private boolean canShow = false;
    InterstitialAd mInterstitialAd;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        infoText = (TextView) findViewById(R.id.pointcam_txt);
        photo_btn = (Button) findViewById(R.id.btn_photo);
        photo_btn.setOnClickListener(this);
        Button back_btn = (Button) findViewById(R.id.btn_back);
        back_btn.setOnClickListener(this);
        Button change_btn = (Button) findViewById(R.id.btn_change);
        change_btn.setOnClickListener(this);
        // Go get the front facing camera of the device
        // best practice is to do this asynchronously
        File pictures = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        photoFile = new File(pictures, "MyAgePhoto.jpg");

        frontCameraRetriever = new FrontCameraRetriever();
        camera = frontCameraRetriever.DetectFaces(isFace);
        if (camera!=null) {
            cameraSurface = new CameraSurfaceView(this, camera, this);
            faceOverlayView = new FaceOverlayView(this);
            // Add the surface view (i.e. camera preview to our layout)
            ((FrameLayout) findViewById(R.id.CameraPreview)).removeAllViews();
            ((FrameLayout) findViewById(R.id.CameraPreview)).addView(cameraSurface);
            ((FrameLayout) findViewById(R.id.CameraPreview)).addView(faceOverlayView,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Intent intent = new Intent(getApplicationContext(), ThirdActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                canShow = true;
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                canShow = false;
            }
        });
        mInterstitialAd.setAdUnitId(getString(R.string.banner_fullID));

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);
        mp = MediaPlayer.create(this , R.raw.five);
    }
    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        if(mInterstitialAd!=null)
        {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mInterstitialAd.loadAd(adRequest);
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }


    @Override
    public void onFaceDetected(Camera.Face[] faces) {
        faceOverlayView.setFaces(faces);
        curRect = faces[0].rect;
        infoText.setText(R.string.btn_scan);
        photo_btn.setEnabled(true);
        photo_btn.setBackgroundResource(R.mipmap.btn_photo_on);
    }

    @Override
    public void onFaceTimedOut() {
        faceOverlayView.setFaces(null);
        infoText.setText(R.string.btn_pointcam);
        photo_btn.setEnabled(false);
        photo_btn.setBackgroundResource(R.mipmap.btn_photo_off);
    }

    @Override
    public void onFaceDetectionNonRecoverableError() {
        faceOverlayView.setFaces(null);
        // This can happen if
        // Face detection not supported on this device
        // Something went wrong in the Android api
        // or our app or another app failed to release the camera properly
        infoText.setText("Failed to release camera");
        photo_btn.setEnabled(false);
        photo_btn.setBackgroundResource(R.mipmap.btn_photo_off);
    }
    @Override
    public void onPause (){
        camera.recycle();
        finish();
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_photo:
                if (photo_btn.isEnabled()){
                    mp.start();
                    camera.getCamera().takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            Log.d("AZAZA", "ЗАШЛО СЮДА КАРОЧИ");
                            //String root = Environment.getExternalStorageDirectory().toString();
                            File myDir = new File( Environment.getExternalStorageDirectory(),File.separator+"AppAcheImages");
                            myDir.mkdirs();
                            Random rnd = new Random();
                            int abc = rnd.nextInt();
                            String fname = "Image.jpg";
                            File file = new File(myDir, fname);
                            Log.i("CLASS", "" + file);


                            try {
                                Log.d("FACE RECT: ", " "+String.valueOf(curRect.centerX()) + " " + String.valueOf(curRect.centerY())+" "+
                                        String.valueOf(curRect.width()) + " " + String.valueOf(curRect.height()));
                                FileOutputStream out = new FileOutputStream(file);
                                Bitmap bmp = BitmapFactory.decodeByteArray(data , 0, data.length);
                                Matrix matrix = new Matrix();

                                matrix.postRotate(-90);
                                matrix.postScale((frontCameraRetriever.isFrontal)?-0.3f:-0.1f,(frontCameraRetriever.isFrontal)?0.3f:-0.1f);

                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp,bmp.getWidth(),bmp.getHeight(),true);

                                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
                                //Bitmap bmp1 = Bitmap.createBitmap(bmp,curRect.centerX(),curRect.centerY(),
                                   //     curRect.width(),curRect.height());
                                bmp.recycle();
                                scaledBitmap.recycle();
                                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG,90,out);
                                rotatedBitmap.recycle();
                                out.flush();
                                out.close();


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            camera.startPreview();
                            camera.startFaceDetection();
                            showInterstitial();
                            if (!canShow) {
                                Intent intent = new Intent(getApplicationContext(), ThirdActivity.class);
                                startActivity(intent);
                            }
                        }});
                    //camera.TakePic(this,curRect);
                    /*sendBroadcast(new Intent(
                            Intent.ACTION_MEDIA_MOUNTED,
                            Uri.parse("file://" + Environment.getExternalStorageDirectory())));*/

                }
                break;
            case R.id.btn_back:
                mp.start();
                showInterstitial();
                onBackPressed();
                break;
            case R.id.btn_change:
                mp.start();
                camera.recycle();
                isFace=!isFace;
                frontCameraRetriever = new FrontCameraRetriever();
                camera = frontCameraRetriever.DetectFaces(isFace);
                if (camera!=null) {
                    cameraSurface = new CameraSurfaceView(this, camera, this);
                    faceOverlayView = new FaceOverlayView(this);
                    // Add the surface view (i.e. camera preview to our layout)
                    ((FrameLayout) findViewById(R.id.CameraPreview)).removeAllViews();
                    ((FrameLayout) findViewById(R.id.CameraPreview)).addView(cameraSurface);
                    ((FrameLayout) findViewById(R.id.CameraPreview)).addView(faceOverlayView,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                }
                break;
        }
    }

}

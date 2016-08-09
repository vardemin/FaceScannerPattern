package com.appelsin.olympicsportfacescanner;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.unity3d.ads.android.IUnityAdsListener;
import com.unity3d.ads.android.UnityAds;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.photo.VKImageParameters;
import com.vk.sdk.api.photo.VKUploadImage;
import com.vk.sdk.dialogs.VKShareDialog;

import java.io.File;
import java.util.Random;

public class FourthActivity extends AppCompatActivity implements View.OnClickListener, MyDialog.CustomDialogListener, IUnityAdsListener {
    MyDialog dialog;
    private AdView mAdView;
    private ImageView image;
    private TextView ageTxt;
    private TextView res2;
    private Bitmap bitmap;
    private ImageView medal;
    MediaPlayer mp;
    Bitmap bmp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);
        int[] chooses = {R.string.z_badm,
                R.string.z_bask,
                R.string.z_box,
                R.string.z_free,
                R.string.z_wrest,
                R.string.z_cycle,
                R.string.z_water,
                R.string.z_volley,
                R.string.z_hand,
                R.string.z_golf,
                R.string.z_row,
                R.string.z_rowAnd,
                R.string.z_judo,
                R.string.z_horse,
                R.string.z_atl,
                R.string.z_tten,
                R.string.z_sail,
                R.string.z_sw,
                R.string.z_bea,
                R.string.z_divi,
                R.string.z_jump,
                R.string.z_rugby,
                R.string.z_syn,
                R.string.z_modern,
                R.string.z_gym,
                R.string.z_shoot,
                R.string.z_arch,
                R.string.z_ten,
                R.string.z_tri,
                R.string.z_taek,
                R.string.z_weight,
                R.string.z_fenc,
                R.string.z_foot,
                R.string.z_fho};
        image = (ImageView) findViewById(R.id.scanned_img);
        ageTxt = (TextView) findViewById(R.id.result_txt);
        int index1 = randInt(0,chooses.length-1);
        String like = getString(chooses[index1]);
        ageTxt.setText(like);

        res2 = (TextView) findViewById(R.id.result2_txt);
        medal = (ImageView) findViewById(R.id.medal);

        ((Button) findViewById(R.id.btn_save_gallery)).setOnClickListener(this);
        ((Button) findViewById(R.id.share_insta)).setOnClickListener(this);
        ((Button) findViewById(R.id.share_vk)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_gender)).setOnClickListener(this);

        String _path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"AppAcheImages"+File.separator+"Image.jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmap = BitmapFactory.decodeFile(_path, options);

        image.setImageBitmap(bitmap);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        UnityAds.init((Activity)this, "1109395", (IUnityAdsListener) this);
        mp = MediaPlayer.create(this , R.raw.five);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA },42
                );

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

    }
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
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
    public void onClick(View v) {
        int id = v.getId();
        String path = "";
        Uri bmpUri;
        Intent shareIntent;
        switch (id) {
            case R.id.btn_gender:
                mp.start();
                dialog = new MyDialog();
                dialog.show(getSupportFragmentManager(),"dialog");
                break;
            case R.id.btn_save_gallery:
                mp.start();
                if (bmp==null)
                    bmp = getScreenShotC(findViewById(R.id.rootView),findViewById(R.id.info_block));
                MediaStore.Images.Media.insertImage(getContentResolver(), bmp,
                        getString(R.string.title) ,
                        getString(R.string.share));
                bmp = null;
                break;
            case R.id.share_insta:
                mp.start();
                if (bmp==null)
                    bmp = getScreenShotC(findViewById(R.id.rootView),findViewById(R.id.info_block));
                path = MediaStore.Images.Media.insertImage(getContentResolver(), bmp,
                        getString(R.string.title) ,
                        getString(R.string.share));
                bmpUri = Uri.parse(path);
                try {
                    shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    shareIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.share));
                    shareIntent.setPackage("com.instagram.android");
                    shareIntent.setType("image/png");
                    startActivity(shareIntent);
                }
                catch (ActivityNotFoundException ex) {
                    shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    shareIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.share));
                    shareIntent.setType("image/png");
                    startActivity(shareIntent);
                }
                bmp = null;
                break;
            case R.id.share_vk:
                mp.start();
                VKSdk.login(this, VKScope.WALL, VKScope.PHOTOS);
                if(bmp == null)
                bmp = getScreenShotC(findViewById(R.id.rootView),findViewById(R.id.info_block));
                new VKShareDialog()
                        .setText(getText(R.string.share))
                        .setAttachmentImages(new VKUploadImage[]{
                                new VKUploadImage(bmp, VKImageParameters.pngImage())
                        })
                            .setShareDialogListener(new VKShareDialog.VKShareDialogListener() {
                                @Override
                                public void onVkShareComplete(int postId) {
                                    Log.d("VK", "Отправлено");
                                }

                                @Override
                                public void onVkShareCancel() {
                                    //отмена
                                }

                                @Override
                                public void onVkShareError(VKError error) {
                                    Log.d("VK", error.errorMessage);
                                }
                            })
                        .show(getSupportFragmentManager(),"VK_SHARE_DIALOG");
                /*path = MediaStore.Images.Media.insertImage(getContentResolver(), bmp,
                        getString(R.string.title) ,
                        getString(R.string.share));
                bmpUri = Uri.parse(path);
                try {
                    shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    shareIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.share));
                    shareIntent.setPackage("com.vkontakte.android");
                    shareIntent.setType("image/png");
                    startActivity(shareIntent);
                }
                catch (ActivityNotFoundException ex) {
                    shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    shareIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.share));
                    shareIntent.setType("image/png");
                    startActivity(shareIntent);
                }*/
                bmp = null;
                break;
        }
    }
    public static Bitmap getScreenShot(View view) {
        /*view.setDrawingCacheEnabled(true);

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.buildDrawingCache(true);*/
        //Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        //view.setDrawingCacheEnabled(false);
        Bitmap bitmap = loadBitmapFromView(view);
        return bitmap;
    }
    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap( v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        v.requestLayout();
        return b;
    }
    public Bitmap getScreenShotC(View view, View last) {
        Bitmap bitmap = loadBitmapFromViewC(view,last);
        return bitmap;
    }
    public Bitmap loadBitmapFromViewC(View v, View last) {
        Bitmap b = Bitmap.createBitmap( v.getWidth(),getRelativeTop(last) + last.getHeight() - dpToPx(10) , Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        v.requestLayout();
        return b;
    }
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
    private static int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }
    private Bitmap overlay(Bitmap bmp1) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(loadBitmapFromView(findViewById(R.id.info_block)), new Matrix(), null);
        bmp1.recycle();
        return bmOverlay;
    }

    @Override
    public void onOk() {
        if ( UnityAds.canShow() )
        {
            UnityAds.show();
        }
    }
    public static int randInt(int min, int max) {

        Random rand = new Random();

        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
    public static int randIntWithout(int min, int max, int lishniy) {

        Random rand = new Random();

        int randomNum = rand.nextInt((max - min) + 1) + min;
        while (randomNum==lishniy)
            randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }


    @Override
    public void onHide() {

    }

    @Override
    public void onShow() {

    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoCompleted(String s, boolean b) {
        Random rnd = new Random();
        double d = rnd.nextDouble();
        if (d<=0.3) {
            medal.setVisibility(View.VISIBLE);
            medal.setImageResource(R.mipmap.pedal_bronze);
            findViewById(R.id.info_block).setVisibility(View.GONE);
        }
        else if (d<=0.6) {
            medal.setVisibility(View.VISIBLE);
            medal.setImageResource(R.mipmap.pedal_silver);
            findViewById(R.id.info_block).setVisibility(View.GONE);
        }
        else if (d<=0.9) {
            medal.setVisibility(View.VISIBLE);
            medal.setImageResource(R.mipmap.pedal_gold);
            findViewById(R.id.info_block).setVisibility(View.GONE);
        }
        else {
            res2.setText(R.string.res_alt);
        }
    }

    @Override
    public void onFetchCompleted() {

    }

    @Override
    public void onFetchFailed() {

    }
}

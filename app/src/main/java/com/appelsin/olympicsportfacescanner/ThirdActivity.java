package com.appelsin.olympicsportfacescanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;

public class ThirdActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ThirdActivity.class.getSimpleName();
    private AdView mAdView;
    ImageView image;
    private Button btn;
    InterstitialAd mInterstitialAd;
    MediaPlayer mp;
    private boolean canShow = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        image = (ImageView) findViewById(R.id.source_img);
        btn = (Button) findViewById(R.id.btn_analyze);
        btn.setOnClickListener(this);
        btn.setEnabled(false);
        btn.setBackgroundResource(R.mipmap.btn_start_off);
        String _path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"AppAcheImages"+File.separator+"Image.jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

        image.setImageBitmap(bitmap);
        triggerScannerAnimation();

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Intent intent = new Intent(getApplicationContext(), FourthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
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

    private void triggerScannerAnimation() {
        Log.d(TAG, "Starting scanner animation");
        final ImageView scannerBar = (ImageView) findViewById(R.id.scannerBar);
        Animation scannerAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim);
        scannerAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //Show scanner bar
                scannerBar.setVisibility(View.VISIBLE);
                //Vibrate
                /*Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(2000);*/
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //Hide scanner bar
                scannerBar.setVisibility(View.INVISIBLE);
                //Show result screen
                btn.setEnabled(true);
                btn.setText(R.string.btn_getresult);
                btn.setBackgroundResource(R.mipmap.btn_start_on);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //empty
            }
        });
        scannerBar.startAnimation(scannerAnimation);
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
    public void onClick(View v) {
        if (v.getId()==R.id.btn_analyze) {
            if (btn.isEnabled())
            {
                mp.start();
                showInterstitial();
                if (!canShow) {
                    Intent intent = new Intent(getApplicationContext(), FourthActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                }
            }
        }
    }
}

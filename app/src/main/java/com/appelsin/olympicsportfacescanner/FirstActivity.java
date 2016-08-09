package com.appelsin.olympicsportfacescanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.vk.sdk.util.VKUtil;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {
    private AdView mAdView;
    InterstitialAd mInterstitialAd;
    MediaPlayer mp;
    private boolean canShow = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        Button start_btn = (Button)findViewById(R.id.start_btn);
        if (start_btn != null) {
            start_btn.setOnClickListener(this);
        }
        Button more_btn = (Button)findViewById(R.id.more_btn);
        if (more_btn != null) {
            more_btn.setOnClickListener(this);
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
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                canShow = false;
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                canShow = true;
            }
        });
        mInterstitialAd.setAdUnitId(getString(R.string.banner_fullID));

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);
        mp = MediaPlayer.create(this , R.raw.five);
        String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
        for (String val :
                fingerprints) {
            Log.d("VKUTIL", val);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA) ) {

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
    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
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
        int id = v.getId();
        switch (id){
            case R.id.start_btn:
                mp.start();
                showInterstitial();
                if (!canShow){
                Intent intent = new Intent(this, SecondActivity.class);

                startActivity(intent);}
                break;
            case R.id.more_btn:
                mp.start();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=appelsin"));
                startActivity(browserIntent);
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 42) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.CAMERA)
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Permission granted",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

package com.vmax.demo;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.vmax.android.ads.api.VmaxAdView;
import com.vmax.android.ads.common.VmaxAdListener;
import com.vmax.android.ads.exception.VmaxAdError;

import java.util.Timer;
import java.util.TimerTask;


/** Its Recommended To Use VMAX plugin For Android Studio To Add Your Dependencies
 and Manage Changes in AndroidManifest as Well as Proguard,
 However You Can Manually Do This By Referring To Our Documentation Or following this Demo Project  */


public class MainActivity extends Activity {


    VmaxAdView vmaxAdView;
    PlayerView simpleExoPlayerView;
    SimpleExoPlayer player;
    FrameLayout adContainer;
    Boolean resumeVideo=false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /** Initialize App UI */
        IntiUi();
        /** Cache Instream Video Ad*/
        cacheInstreamVideo();

    }
    public void IntiUi()
    {
        simpleExoPlayerView=(PlayerView) findViewById(R.id.frame_exoplayer);
        adContainer=(FrameLayout)findViewById(R.id.adContainer);

    }

    /** Method for Loading Instream Video */
    public void cacheInstreamVideo()
    {
        vmaxAdView = new VmaxAdView(this,"V933206e4",VmaxAdView.UX_INSTREAM_VIDEO);

        vmaxAdView.setAdListener(new VmaxAdListener() {

            @Override public void onAdReady(VmaxAdView adView) {
                /** When Ad is cached play content video*/
                playContentVideo();
            }

            @Override public void onAdError(VmaxAdError error) {
                Toast.makeText(getApplicationContext(),
                        "Failed To Load Ad Please Try Again Later",Toast.LENGTH_LONG).show();
                playContentVideo();

            }

            @Override
            public void onAdClose() {
                resumeVideo=true;
                playContentVideo();
            }

            @Override
            public void onAdMediaEnd(boolean isVideoCompleted, long l) {

            }

        });

        vmaxAdView.cacheAd();
    }

public void playContentVideo()
    {

        if(!resumeVideo) {
            Uri uri = Uri.parse("http://rmcdn.2mdn.net/MotifFiles/html/1248596/android_1330378998288.mp4");

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            simpleExoPlayerView.setPlayer(player);
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), Util.getUserAgent(this, "sample"));
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            player.prepare(videoSource);

            player.setPlayWhenReady(true);
            Timer timer=new Timer();

            /** Set Timer for 30 sec after content start to show the cached Ad */

            TimerTask timerTask=new TimerTask() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(vmaxAdView.getAdState()== VmaxAdView.AdState.STATE_AD_READY) {
                                player.setPlayWhenReady(false);
                                 simpleExoPlayerView.setVisibility(View.INVISIBLE);

                                showInstream(vmaxAdView);
                            }
                        }
                    });

                }
            };
            timer.schedule(timerTask,30000);

        }
        else
        {
            simpleExoPlayerView.setVisibility(View.VISIBLE);
            player.setPlayWhenReady(true);

        }

    }


    public void showInstream(VmaxAdView vmaxAdView1)
    {
        if (vmaxAdView1.getAdState() == VmaxAdView.AdState.STATE_AD_READY)
        {

            vmaxAdView1.setVideoPlayerDetails(adContainer);
            vmaxAdView1.showAd();


        }
    }

    /** Handle vmaxAdView object for Activity Lifecycle changes */

    @Override
    protected void onDestroy() {
        if (vmaxAdView != null) {
       /** To Destroy vmaxAdView when Activity Is No Longer Available  */
            vmaxAdView.onDestroy();
        }
        super.onDestroy();
    }




}

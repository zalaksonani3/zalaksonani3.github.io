package com.simple.gallery.photos.videos.ads;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder;
import com.simple.gallery.photos.videos.R;

public class AppLovinAds {
    private static final String TAG = "AppLovin_Interstitial";
    public MaxInterstitialAd maxInterstitialAd;
    MyCallback myCallback;

    public interface MyCallback {
        void callbackCall();
    }

    private static AppLovinAds appLovin_interstitial;

    public static AppLovinAds getInstance() {
        if (appLovin_interstitial == null) {
            appLovin_interstitial = new AppLovinAds();
        }
        return appLovin_interstitial;
    }

    public MaxAdView LoadBannerAd(Activity activity, String applovin_banner, FrameLayout adLay) {
        MaxAdView adView = new MaxAdView(applovin_banner, activity);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int heightPx = activity.getResources().getDimensionPixelSize(R.dimen._45sdp);
        adView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx, Gravity.BOTTOM));
        adView.setBackgroundColor(Color.TRANSPARENT);
        adLay.removeAllViews();
        adLay.addView(adView);
        adView.loadAd();
        return adView;
    }

    public void LoadInterstitial(Activity activity, String applovin_interstitial, long applovin_counter, String applovin_ad_show) {
        Log.d(TAG, "Load_ad: ");

        if (applovin_ad_show != null && applovin_ad_show.equals("yes")) {
            maxInterstitialAd = new MaxInterstitialAd(applovin_interstitial, activity);
            maxInterstitialAd.setListener(new MaxAdListener() {
                @Override
                public void onAdLoaded(MaxAd ad) {
                    Log.d(TAG, "interstitial onAdLoaded: " + ad.getRevenue());
                }

                @Override
                public void onAdDisplayed(MaxAd ad) {
                    Log.d(TAG, "interstitial onAdDisplayed: " + ad.getRevenue());
                }

                @Override
                public void onAdHidden(MaxAd ad) {
                    Log.d(TAG, "interstitial onAdHidden: " + ad.getRevenue());
                    if (AppLovinAds.this.myCallback != null) {
                        AppLovinAds.this.myCallback.callbackCall();
                        AppLovinAds.this.myCallback = null;
                    }

                    new Handler(Looper.getMainLooper()).postDelayed(() -> maxInterstitialAd.loadAd(), applovin_counter);
                }

                @Override
                public void onAdClicked(MaxAd ad) {
                    Log.d(TAG, "interstitial onAdClicked: " + ad.getRevenue());
                }

                @Override
                public void onAdLoadFailed(String adUnitId, MaxError error) {
                    Log.d(TAG, "interstitial onAdLoadFailed: " + error.getMessage() + " getCode: " + error.getCode());
                    if (AppLovinAds.this.myCallback != null) {
                        AppLovinAds.this.myCallback.callbackCall();
                        AppLovinAds.this.myCallback = null;
                    }

                    new Handler(Looper.getMainLooper()).postDelayed(() -> maxInterstitialAd.loadAd(), applovin_counter);
                }

                @Override
                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                    Log.e(TAG, "onAdDisplayFailed: " + error.getMessage());
                    Log.e(TAG, "onAdDisplayFailed: " + error.getCode());
                    Log.e(TAG, "onAdDisplayFailed: " + error.getMediatedNetworkErrorMessage());
                    Log.e(TAG, "onAdDisplayFailed: " + error.getWaterfall());
                    Log.e(TAG, "onAdDisplayFailed: " + error);
                    Log.d(TAG, "interstitial onAdDisplayFailed: " + error.getMessage() + " getCode: " + error.getCode());
                    if (AppLovinAds.this.myCallback != null) {
                        AppLovinAds.this.myCallback.callbackCall();
                        AppLovinAds.this.myCallback = null;
                    }

                    new Handler(Looper.getMainLooper()).postDelayed(() -> maxInterstitialAd.loadAd(), applovin_counter);
                }
            });
            maxInterstitialAd.loadAd();
        }
    }

    public void Show_ad(Activity activity, MyCallback myCallback) {
        Log.d(TAG, "Show_ad: ");
        this.myCallback = myCallback;
        if (maxInterstitialAd != null && maxInterstitialAd.isReady()) {
            maxInterstitialAd.showAd();
        } else {
            if (AppLovinAds.this.myCallback != null) {
                AppLovinAds.this.myCallback.callbackCall();
                AppLovinAds.this.myCallback = null;
            }
        }
    }

    public void Load_Native_Ad(Activity activity, FrameLayout nativeAdLayout, String applovin_native,String ad_type) {
        MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(applovin_native, activity);
        nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
            @Override
            public void onNativeAdLoaded(@Nullable MaxNativeAdView maxNativeAdView, MaxAd ad) {
                super.onNativeAdLoaded(maxNativeAdView, ad);
                Log.d(TAG, "onNativeAdLoaded: ");
                MaxAd nativeAd;
                nativeAd = ad;

                MaxNativeAdView adView;
                if (ad_type.equals("native_banner")){
                    adView = createNativeBannerAdView(activity);
                }else{
                    adView = createNativeAdView(activity);
                }
                nativeAdLayout.removeAllViews();
                nativeAdLoader.render(adView, nativeAd);
                nativeAdLayout.addView(adView);
            }

            @Override
            public void onNativeAdLoadFailed(String s, MaxError maxError) {
                super.onNativeAdLoadFailed(s, maxError);
                Log.d(TAG, "onNativeAdLoadFailed: " + maxError);
            }

            @Override
            public void onNativeAdClicked(MaxAd maxAd) {
                super.onNativeAdClicked(maxAd);
            }

        });
        nativeAdLoader.loadAd();
    }

    private MaxNativeAdView createNativeBannerAdView(Activity activity) {
        MaxNativeAdViewBinder binder = new MaxNativeAdViewBinder.Builder(R.layout.applovin_native_banner)
                .setTitleTextViewId(R.id.title_text_view)
                .setBodyTextViewId(R.id.body_text_view)
                .setIconImageViewId(R.id.icon_image_view)
                .setOptionsContentViewGroupId(R.id.options_view)
                .setCallToActionButtonId(R.id.cta_button)
                .build();

        return new MaxNativeAdView(binder, activity);
    }

    private MaxNativeAdView createNativeAdView(Activity activity) {
        MaxNativeAdViewBinder binder = new MaxNativeAdViewBinder.Builder(R.layout.applovin_native_ad)
                .setTitleTextViewId(R.id.title_text_view)
                .setBodyTextViewId(R.id.body_text_view)
                .setAdvertiserTextViewId(R.id.advertiser_textView)
                .setIconImageViewId(R.id.icon_image_view)
                .setMediaContentViewGroupId(R.id.media_view_container)
                .setOptionsContentViewGroupId(R.id.options_view)
                .setCallToActionButtonId(R.id.cta_button)
                .build();

        return new MaxNativeAdView(binder, activity);
    }
}
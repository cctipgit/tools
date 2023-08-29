package com.rate.quiz.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.duxl.baselib.http.RetrofitManager;
import com.duxl.baselib.rx.SimpleObserver;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.Utils;
import com.duxl.baselib.widget.DataRunnable;
import com.rate.quiz.entity.IpInfo;
import com.rate.quiz.http.api.IpAPI;

import java.util.List;

import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 位置信息工具类
 */
public class LocationUtils {

    private static final String TAG = "LocationUtils";
    private static boolean mGetLocationSuccess;
    private static LocationListener mLocationListener;

    /**
     * 获取位置信息
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static void getLocation(Context context, DataRunnable<Location> runnable) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if(EmptyUtils.isEmpty(provider)) {
            List<String> providers = locationManager.getProviders(true);
            if (providers != null) {
                if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                    provider = LocationManager.NETWORK_PROVIDER;
                } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
                    provider = LocationManager.GPS_PROVIDER;
                }
            }
        }

        if(EmptyUtils.isEmpty(provider)) {
            provider = LocationManager.NETWORK_PROVIDER;
        }
        Log.i(TAG, "provider=" + provider);

        Location location = locationManager.getLastKnownLocation(provider);
        if(location != null) {
            Log.i(TAG, "location: hold lastKnow");
            dispatchLocation(location, runnable);
            return;
        }

        Log.i(TAG, "requestLocationUpdates");
        if(mLocationListener != null) {
            locationManager.removeUpdates(mLocationListener);
        }
        locationManager.requestLocationUpdates(provider, 3000, 1, mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location1) {
                Log.i(TAG, "requestLocationUpdates.onLocationChanged");
                mGetLocationSuccess = true;
                dispatchLocation(location1, runnable);
                locationManager.removeUpdates(this);
                mLocationListener = null;
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                LocationListener.super.onProviderEnabled(provider);
                Log.i(TAG, "requestLocationUpdates.onProviderEnabled");
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                LocationListener.super.onProviderDisabled(provider);
                Log.i(TAG, "requestLocationUpdates.onProviderDisabled");
            }
        });

        Dispatch.I.postUIDelayed(()->{
            // 1.5秒钟没有获取到定位，使用Local获取当前国家
            if(!mGetLocationSuccess) {
                /*String ip = NetCheckUtil.getIPAddress();
                Log.i(TAG, "ip=" + ip);
                if(EmptyUtils.isEmpty(ip)) {
                    return;
                }*/
                RetrofitManager
                        .getInstance()
                        .create(IpAPI.class)
                        //.get(ip)
                        .get()
                        .observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new SimpleObserver<IpInfo>() {
                            @Override
                            public void onNext(@io.reactivex.rxjava3.annotations.NonNull IpInfo ipInfo) {
                                super.onNext(ipInfo);
                                if(!mGetLocationSuccess && ipInfo != null) {
                                    String countryCode = ipInfo.countryCode;
                                    if(EmptyUtils.isNotEmpty(countryCode)) {
                                        Location location2 = new Location("locale");
                                        Bundle extra = new Bundle();
                                        extra.putString("countryCode", countryCode);
                                        location2.setExtras(extra);
                                        dispatchLocation(location2, runnable);
                                    }
                                    Log.i(TAG, "local： " + countryCode);
                                } else {
                                    Log.i(TAG, "mGetLocationSuccess=" + mGetLocationSuccess + ", ipInfo=" + ipInfo);
                                }
                            }
                        });
            }
            mGetLocationSuccess = false;
        }, 1500);
    }

    private static void dispatchLocation(Location location, DataRunnable<Location> runnable) {
        if(location != null && runnable != null) {
            runnable.run(location);
        }
    }

    /**
     * Location转Address
     * @param location
     * @return
     */
    public static Address getAddressFromLocation(Location location) {
        try {
            Geocoder geocoder = new Geocoder(Utils.getApp());
            List<Address> fromLocation = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if(EmptyUtils.isNotEmpty(fromLocation)) {
                Address address = fromLocation.get(0);
                Log.i(TAG, "getAddressFromLocation=" + address.getCountryCode() + ", " + address.getCountryName());
                return fromLocation.get(0);

            }
        } catch (Exception e) {
            Log.i(TAG, "getAddressFromLocation err=" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}

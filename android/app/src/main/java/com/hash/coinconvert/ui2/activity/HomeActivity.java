package com.hash.coinconvert.ui2.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.duxl.baselib.utils.NullUtils;
import com.duxl.baselib.utils.SPUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.R;
import com.hash.coinconvert.http.SocketSender;
import com.hash.coinconvert.utils.LocationUtils;

public class HomeActivity extends BaseActivity {

    public static final String TAG = "HomeActivity";

    private BottomNavigationView bottomNavigationView;
    private static final int[] IDS = new int[]{
            R.id.fragment_currency,
            R.id.fragment_task_list,
            R.id.fragment_redeem,
            R.id.fragment_profile
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NavHostFragment fragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_container);
        NavController controller = fragment.getNavController();

        bottomNavigationView = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNavigationView, controller);

        controller.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                Log.d("onDestinationChanged", navDestination.getDisplayName());
                if (isInNav(navDestination.getId())) {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                } else {
                    bottomNavigationView.setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean isInNav(int id) {
        for (int i : IDS) {
            if (id == i) return true;
        }
        return false;
    }

    @SuppressLint("NewApi")
    public ActivityResultLauncher<String[]> mPermissionMutLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), map -> {
        Boolean accessCoarseLocation = map.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
        Boolean accessFineLocation = map.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
        Log.i(TAG, "位置权限: accessCoarseLocation=" + accessCoarseLocation + ", accessFineLocation=" + accessFineLocation);
        if (accessCoarseLocation && accessFineLocation) {
            getCurrentCounty(true);
        } else {
            SPUtils.getInstance().put(Constants.SP.KEY.DEFAULT_LOCAL_CURRENCY, false);
        }
    });

    /**
     * 获取当前国家
     *
     * @param getCurrency 定位成功后，是否需要获取当前定位的默认货币列表
     */
    private void getCurrentCounty(boolean getCurrency) {
        LocationUtils.getLocation(this, location -> {
            if ("locale".equals(location.getProvider())) {
                String countryCode = location.getExtras().getString("countryCode");
                SPUtils.getInstance().put(Constants.SP.KEY.LOCATION_COUNTRY_CODE, countryCode);
                if (getCurrency) {
                    int rateItemCount = SPUtils.getInstance().getInt(Constants.SP.KEY.RATE_ITEM_COUNT, Constants.SP.DEFAULT.RATE_ITEM_COUNT);
                    SocketSender.sendCurrentDefaultCurrency(rateItemCount, countryCode);
                }
                Log.i(TAG, "当前国家code：" + countryCode);
                return;
            }
            Log.i(TAG, "当前位置：longitude=" + location.getLongitude() + ", latitude=" + location.getLatitude());
            Address address = LocationUtils.getAddressFromLocation(location);
            if (address == null) {
                // try again get address
                Log.i(TAG, "address is null, try again get address");
                address = LocationUtils.getAddressFromLocation(location);
            }
            if (address != null) {
                Log.i(TAG, "当前国家：" + address.getCountryName() + ", " + address.getCountryCode());
                String countryCode = NullUtils.format(address.getCountryCode()).toString();
                SPUtils.getInstance().put(Constants.SP.KEY.LOCATION_COUNTRY_CODE, countryCode);
                if (getCurrency) {
                    int rateItemCount = SPUtils.getInstance().getInt(Constants.SP.KEY.RATE_ITEM_COUNT, Constants.SP.DEFAULT.RATE_ITEM_COUNT);
                    SocketSender.sendCurrentDefaultCurrency(rateItemCount, countryCode);
                }
            }
        });
    }
}

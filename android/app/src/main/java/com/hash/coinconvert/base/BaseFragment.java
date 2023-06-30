package com.hash.coinconvert.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public abstract class BaseFragment extends Fragment {
    private boolean firstResume = true;
    public static final String KEY_INTERNAL_REQUEST_CODE = "internalRequestCode";
    private ActivityResultLauncher<Intent> mIntentActivityResultLauncher;

    public BaseFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            int requestCode = 0;
            if (result != null && result.getData() != null) {
                requestCode = result.getData().getIntExtra(KEY_INTERNAL_REQUEST_CODE, 0);
                onCommonActivityResult(requestCode, result.getResultCode(), result.getData());
            }
        });
    }

    protected void onCommonActivityResult(int requestCode, int resultCode, Intent data) {

    }

    protected NavController getNavController() {
        return NavHostFragment.findNavController(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (firstResume) {
            onFirstResume();
            firstResume = false;
        }
    }

    /**
     * lazy load
     */
    protected void onFirstResume() {

    }

    protected void goActivity(Class<? extends Activity> clsActivity, Bundle extras, int requestCode) {
        Intent intent = new Intent(requireContext(), clsActivity);
        intent.putExtra(KEY_INTERNAL_REQUEST_CODE, requestCode);
        if (extras != null) {
            intent.putExtras(extras);
        }
        mIntentActivityResultLauncher.launch(intent);
    }

}

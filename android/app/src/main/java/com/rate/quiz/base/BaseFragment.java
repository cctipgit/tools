package com.rate.quiz.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.rate.quiz.R;

public abstract class BaseFragment extends Fragment {
    private boolean firstResume = true;
    public static final String KEY_INTERNAL_REQUEST_CODE = "internalRequestCode";
    private ActivityResultLauncher<Intent> mIntentActivityResultLauncher;

    private ContentLoadingProgressBar progressBar;

    public BaseFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            int requestCode;
            if (result != null && result.getData() != null) {
                requestCode = result.getData().getIntExtra(KEY_INTERNAL_REQUEST_CODE, 0);
                onCommonActivityResult(requestCode, result.getResultCode(), result.getData());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void onCommonActivityResult(int requestCode, int resultCode, Intent data) {

    }

    protected NavController getNavController() {
        return NavHostFragment.findNavController(this);
    }

    public void navigateTo(NavDirections d) {
        NavOptions options = new NavOptions.Builder()
                .setExitAnim(R.anim.slide_out_left)
                .setEnterAnim(R.anim.slide_in_right)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)
                .build();
        getNavController().navigate(d, options);
    }

    public boolean navigateBack() {
        return getNavController().popBackStack();
    }

    protected <T> LiveData<T> getLiveDataInCurrentBackstack(String key) {
        return getNavController().getCurrentBackStackEntry().getSavedStateHandle().getLiveData(key);
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

    public FrameLayout getContentView() {
        return findContentView(requireView());
    }

    protected void showLoading() {
        ensureLoadingView();
        progressBar.show();
    }

    private synchronized void ensureLoadingView() {
        if (progressBar == null) {
            FrameLayout root = getContentView();
            if (root.getChildCount() > 1) {
                View view = root.getChildAt(root.getChildCount() - 1);
                if (view instanceof ContentLoadingProgressBar) {
                    progressBar = (ContentLoadingProgressBar) view;
                }
            }
            if (progressBar == null) {
                getLayoutInflater().inflate(R.layout.view_page_loading, root, true);
                progressBar = root.findViewById(R.id.global_progress_bar);
                setupProgressGravity(progressBar);
            }
        }
        progressBar.getIndeterminateDrawable().setColorFilter(getProgressBarTint(), PorterDuff.Mode.MULTIPLY);
    }

    protected void setupProgressGravity(ProgressBar progressBar){

    }

    @ColorInt
    protected int getProgressBarTint() {
        return requireContext().getColor(R.color.theme_text_color);
    }

    protected void hideLoading() {
        if (progressBar != null) {
            progressBar.hide();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        progressBar = null;
    }

    private FrameLayout findContentView(View view) {
        requireActivity().getWindow().getDecorView();
        if (view == null) return null;
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            Log.d("findContentView", parent.getClass().getCanonicalName());
            if ("android.widget.FrameLayout".equals(parent.getClass().getCanonicalName()) && ((ViewGroup) parent).getId() != R.id.fl_qa) {
                return ((FrameLayout) parent);
            } else {
                return findContentView(((ViewGroup) parent));
            }
        }
        return null;
    }
}

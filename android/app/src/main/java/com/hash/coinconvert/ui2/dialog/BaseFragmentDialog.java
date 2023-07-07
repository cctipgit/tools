package com.hash.coinconvert.ui2.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewbinding.ViewBinding;

import com.hash.coinconvert.R;

public abstract class BaseFragmentDialog<VB extends ViewBinding> extends DialogFragment {

    protected VB binding;

    public abstract int getLayoutId();

    public abstract VB bind(View view);

    protected NavController getNavController() {
        return NavHostFragment.findNavController(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        updateWindow();
        return inflater.inflate(getLayoutId(),container,false);
    }

    public void onPositiveClick(Action action){
        do {
            NavController controller = getNavController();
            if(controller == null)break;
            NavBackStackEntry entry = controller.getPreviousBackStackEntry();
            if(entry == null)break;
            SavedStateHandle stateHandle = entry.getSavedStateHandle();
            if(action != null){
                action.invoke(this,stateHandle);
            }
        }while (false);
    }

    @Override
    public int getTheme() {
        return R.style.Theme_ExchangeRate_Dialog;
    }

    private void updateWindow(){
        Window window = getDialog().getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        window.setGravity(getGravity());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = getTheme();
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding = bind(view);
        initView();
    }

    @Nullable
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    protected int getGravity(){
        return Gravity.CENTER;
    }

    protected abstract void initView();

    public interface Action{
        void invoke(DialogFragment dialog,SavedStateHandle stateHandle);
    }

    public abstract static class AutoCloseAction implements Action{
        public abstract void invoke(SavedStateHandle stateHandle);

        @Override
        public void invoke(DialogFragment dialog, SavedStateHandle stateHandle) {
            invoke(stateHandle);
            dialog.dismiss();
        }
    }
}

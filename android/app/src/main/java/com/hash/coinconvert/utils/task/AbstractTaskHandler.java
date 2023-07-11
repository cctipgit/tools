package com.hash.coinconvert.utils.task;

import android.content.Context;
import android.os.Bundle;

import androidx.navigation.NavController;

import com.hash.coinconvert.ui2.activity.HomeActivity;

public abstract class AbstractTaskHandler implements TaskHandler {
    @Override
    public void dispatch(NavController navController, Bundle bundle) {

    }

    public void switchTab(Context context, int id) {
        if (context instanceof HomeActivity) {
            ((HomeActivity) context).switchTab(id);
        }
    }
}

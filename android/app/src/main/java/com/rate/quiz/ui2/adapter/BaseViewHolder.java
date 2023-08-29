package com.rate.quiz.ui2.adapter;

import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<WeakReference<? extends View>> cachedViews;

    public BaseViewHolder(View itemView) {
        super(itemView);
        this.cachedViews = new SparseArray<>();
    }

    @SuppressWarnings("unchecked")
    private <T extends View> T getViewFromCache(int id) {
        WeakReference<? extends View> reference = cachedViews.get(id);
        if (reference != null) {
            return (T) reference.get();
        }
        return null;
    }

    private <T extends View> T findViewById(int id) {
        T view = getViewFromCache(id);
        if (view == null) {
            view = itemView.findViewById(id);
            cachedViews.put(id, new WeakReference<>(view));
        }
        return view;
    }

    public void setText(int id, CharSequence s) {
        TextView textView = findViewById(id);
        textView.setText(s);
    }

    public void setText(int id, int s) {
        TextView textView = findViewById(id);
        textView.setText(s);
    }
}

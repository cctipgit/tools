package com.hash.coinconvert.ui2.adapter;

import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerViewAdapter<T, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {

    private List<T> data;
    private SparseIntArray layoutMap;

    public BaseRecyclerViewAdapter(List<T> data) {
        this.data = new ArrayList<>();
        this.data.addAll(data);
    }

    protected void addItemType(int viewType, int layoutResId) {
        ensureLayoutMap();
        this.layoutMap.append(viewType, layoutResId);
    }

    private void ensureLayoutMap() {
        if (layoutMap == null) {
            layoutMap = new SparseIntArray(4);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = layoutMap.get(viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return createViewHolder(view);
    }

    public abstract VH createViewHolder(View view);

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        convertView(holder, data.get(position), position);
    }

    abstract void convertView(@NonNull VH holder, T item, int position);

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }
}

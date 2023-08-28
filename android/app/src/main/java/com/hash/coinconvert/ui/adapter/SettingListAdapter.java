package com.hash.coinconvert.ui.adapter;

import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.NullUtils;
import com.duxl.baselib.utils.Utils;
import com.hash.coinconvert.R;
import com.hash.coinconvert.utils.FontUtil;

public class SettingListAdapter extends BaseQuickAdapter<SettingListAdapter.ListItem, BaseViewHolder> {

    public SettingListAdapter() {
        super(R.layout.adapter_setting_listitem);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder viewHolder, ListItem listItem) {
        viewHolder.setText(R.id.tv_label_1, NullUtils.format(listItem.label1));
        viewHolder.setText(R.id.tv_label_2, NullUtils.format(listItem.label2));
        viewHolder.setVisible(R.id.iv_check, listItem.isChecked);
        viewHolder.setGone(R.id.tv_label_1, EmptyUtils.isEmpty(listItem.label1));
        viewHolder.setTextColor(R.id.tv_label_1, ContextCompat.getColor(Utils.getActContextOrApp(), listItem.isChecked ? R.color.white : R.color.text_unselect));

        TextView tv2 = viewHolder.getView(R.id.tv_label_2);
        tv2.setTextColor(ContextCompat.getColor(Utils.getActContextOrApp(), listItem.isChecked ? R.color.white : R.color.text_unselect));
        FontUtil.setType(tv2, TextUtils.isEmpty(listItem.label1) ? FontUtil.FOUNT_TYPE.POPPINS_MEDIUM : FontUtil.FOUNT_TYPE.POPPINS_REGULAR);
    }

    public static class ListItem<T> {
        public String label1;
        public String label2;
        public boolean isChecked;
        public T tag;

        public ListItem(String label1, String label2, boolean isChecked) {
            this.label1 = label1;
            this.label2 = label2;
            this.isChecked = isChecked;
        }

        public ListItem(String label, boolean isChecked) {
            this.label2 = label;
            this.isChecked = isChecked;
        }

        public ListItem<T> setTag(T tag) {
            this.tag = tag;
            return this;
        }
    }
}

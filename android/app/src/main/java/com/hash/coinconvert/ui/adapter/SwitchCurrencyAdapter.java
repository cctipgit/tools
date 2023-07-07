package com.hash.coinconvert.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.duxl.baselib.utils.NullUtils;
import com.hash.coinconvert.R;
import com.hash.coinconvert.database.dao.TokenDao;
import com.hash.coinconvert.database.entity.Token;
import com.hash.coinconvert.entity.CurrencyInfo;
import com.hash.coinconvert.utils.FlagLoader;

import java.util.List;

/**
 * 切换货币列表适配器
 */
public class SwitchCurrencyAdapter extends BaseMultiItemQuickAdapter<SwitchCurrencyAdapter.ItemEntity, BaseViewHolder> {

    public static final int PAYLOAD_UPDATE_CHECK = 1023;

    public SwitchCurrencyAdapter() {
        addItemType(ItemEntity.ItemType.TITLE.ordinal(), R.layout.adapter_switch_currency_title_listitem);
        addItemType(ItemEntity.ItemType.CONTENT.ordinal(), R.layout.adapter_switch_currency_content_listitem);
    }

    @Override
    protected int getDefItemViewType(int position) {
        return super.getDefItemViewType(position);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder viewHolder, ItemEntity itemEntity) {
        if (itemEntity.getItemType() == ItemEntity.ItemType.TITLE.ordinal()) {
            convertTitle(viewHolder, itemEntity);
        } else if (itemEntity.getItemType() == ItemEntity.ItemType.CONTENT.ordinal()) {
            convertContent(viewHolder, itemEntity);
        }
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, ItemEntity item, @NonNull List<?> payloads) {
        if (payloads.size() > 0 && payloads.get(0).equals(PAYLOAD_UPDATE_CHECK)) {
            holder.setVisible(R.id.iv_check, item.isChecked);
            return;
        }
        super.convert(holder, item, payloads);
    }

    private void convertTitle(BaseViewHolder viewHolder, ItemEntity itemEntity) {
        viewHolder.setText(R.id.tv_title, NullUtils.format(itemEntity.title));
    }

    private void convertContent(BaseViewHolder viewHolder, ItemEntity itemEntity) {
        FlagLoader.load(viewHolder.getView(R.id.iv_icon), 0, itemEntity.token.icon);
        viewHolder.setText(R.id.tv_full_name, NullUtils.format(itemEntity.token.name));
        viewHolder.setText(R.id.tv_sort_name, NullUtils.format(itemEntity.token.token));
        viewHolder.setVisible(R.id.iv_check, itemEntity.isChecked);
//        viewHolder.setTextColor(R.id.tv_full_name, ContextCompat.getColor(Utils.getActContextOrApp(), itemEntity.isChecked ? R.color.list_checked : R.color.theme_text_color_alpha60));
//        viewHolder.setTextColor(R.id.tv_sort_name, ContextCompat.getColor(Utils.getActContextOrApp(), itemEntity.isChecked ? R.color.list_checked_alpha60 : R.color.theme_text_color_alpha30));
    }

    public static class ItemEntity implements MultiItemEntity {

        public enum ItemType {
            TITLE, CONTENT
        }

        public String title;
        public boolean isChecked;
        public ItemType itemType;
        public Token token;
        public boolean manualAdd; // 定位货币或频繁使用的为true

        public ItemEntity(String title) {
            this.title = title;
            itemType = ItemType.TITLE;
        }

        public ItemEntity(CurrencyInfo currency, boolean isChecked) {
            itemType = ItemType.CONTENT;
            this.isChecked = isChecked;
        }

        public ItemEntity(Token token) {
            itemType = ItemType.CONTENT;
            this.token = token;
            this.isChecked = token.favorite == TokenDao.FAVORITE;
        }

        @Override
        public int getItemType() {
            return itemType.ordinal();
        }
    }
}

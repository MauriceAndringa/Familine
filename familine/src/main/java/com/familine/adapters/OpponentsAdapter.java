package com.familine.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.core.ui.adapter.BaseSelectableListAdapter;
import com.core.utils.ResourceUtils;
import com.core.utils.UiUtils;
import com.familine.R;
import com.quickblox.users.model.QBUser;

import java.util.List;

/**
 * QuickBlox team
 */
public class OpponentsAdapter extends BaseSelectableListAdapter<QBUser> {

    private SelectedItemsCountsChangedListener selectedItemsCountChangedListener;

    public OpponentsAdapter(Context context, List<QBUser> users) {
        super(context, users);
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_opponents_list, null);
            holder = new ViewHolder();
            holder.opponentIcon = (ImageView) convertView.findViewById(R.id.image_opponent_icon);
            holder.opponentName = (TextView) convertView.findViewById(R.id.opponentsName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final QBUser user = getItem(position);

        if (user != null) {
            holder.opponentName.setText(user.getFullName());

            if (selectedItems.contains(user)){
                convertView.setBackgroundResource(R.color.background_color_selected_user_item);
                holder.opponentIcon.setBackgroundDrawable(
                UiUtils.getColoredCircleDrawable(ResourceUtils.getColor(R.color.icon_background_color_selected_user)));
                holder.opponentIcon.setImageResource(R.drawable.ic_checkmark);
            } else {
                convertView.setBackgroundResource(R.color.background_color_normal_user_item);
                holder.opponentIcon.setBackgroundDrawable(UiUtils.getColorCircleDrawable(user.getId()));
                holder.opponentIcon.setImageResource(R.drawable.ic_person);
            }
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isItemSelected(position)) {
                    toggleSelection(position);
                } else {
                    selectItem(position);
                    selectedItemsCountChangedListener.onClick();
                }
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                toggleSelection(position);
                return true;
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        ImageView opponentIcon;
        TextView opponentName;
    }

    public void setSelectedItemsCountsChangedListener(SelectedItemsCountsChangedListener selectedItemsCountsChanged){
        if (selectedItemsCountsChanged != null) {
            this.selectedItemsCountChangedListener = selectedItemsCountsChanged;
        }
    }

    public interface SelectedItemsCountsChangedListener {
        void onClick();
        void onCountSelectedItemsChanged(int count);
    }
}

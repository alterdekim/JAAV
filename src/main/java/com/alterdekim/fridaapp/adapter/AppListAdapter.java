package com.alterdekim.fridaapp.adapter;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alterdekim.fridaapp.R;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder> {
    @Getter
    private List<AppPopUp> appList;
    private final ClickListener clickListener;

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_popup_item, parent, false);

        view.getLayoutParams().height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 50, Resources.getSystem().getDisplayMetrics());

        return new AppViewHolder(view, this.clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppPopUp appItem = appList.get(position);

        holder.appName.setText(appItem.getName());
        holder.appIcon.setImageDrawable(appItem.getIcon());
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView appName;
        ImageView appIcon;
        private ClickListener clickListener;

        public AppViewHolder(@NonNull View itemView, ClickListener clickListener) {
            super(itemView);
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
            appName = itemView.findViewById(R.id.app_name);
            appIcon = itemView.findViewById(R.id.app_icon);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position < 0) return;
            clickListener.onItemClick(position, v);
        }
    }
}

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

@AllArgsConstructor
public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.MyViewHolder> {
    private List<AppPopUp> appList;
    private final ClickListener clickListener;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_popup_item, parent, false);

        view.getLayoutParams().height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 50, Resources.getSystem().getDisplayMetrics());

        return new MyViewHolder(view, this.clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AppPopUp appItem = appList.get(position);

        holder.appName.setText(appItem.getName());
        holder.appIcon.setImageDrawable(appItem.getIcon());
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView appName;
        ImageView appIcon;
        private ClickListener clickListener;

        public MyViewHolder(@NonNull View itemView, ClickListener clickListener) {
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

    public interface ClickListener {
        void onItemClick(int position, View view);
    }
}

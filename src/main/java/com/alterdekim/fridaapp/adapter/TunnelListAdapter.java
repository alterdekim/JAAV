package com.alterdekim.fridaapp.adapter;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alterdekim.fridaapp.R;
import com.alterdekim.fridaapp.room.Config;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TunnelListAdapter extends RecyclerView.Adapter<TunnelListAdapter.TunnelViewHolder> {
    @Getter
    private List<Config> configList = new ArrayList<>();
    private final ClickListener clickListener;
    private final ClickListener longClickListener;
    private final CheckListener onItemCheckedListener;

    @NonNull
    @Override
    public TunnelListAdapter.TunnelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_config, parent, false);

        return new TunnelListAdapter.TunnelViewHolder(view, this.clickListener, this.longClickListener, this.onItemCheckedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TunnelListAdapter.TunnelViewHolder holder, int position) {
        Config configItem = configList.get(position);

        holder.configName.setText(configItem.getTitle());
        holder.configSwitch.setOnCheckedChangeListener(null);
        holder.configSwitch.setChecked(configItem.isEnabled());
        holder.configSwitch.setOnCheckedChangeListener(holder);
    }

    @Override
    public int getItemCount() {
        return configList.size();
    }

    static class TunnelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {
        TextView configName;
        SwitchMaterial configSwitch;
        private final ClickListener clickListener;
        private final ClickListener longClickListener;
        private final CheckListener onItemCheckedListener;

        public TunnelViewHolder(@NonNull View itemView, ClickListener clickListener, ClickListener longClickListener, CheckListener onItemCheckedListener) {
            super(itemView);
            this.clickListener         = clickListener;
            this.longClickListener     = longClickListener;
            this.onItemCheckedListener = onItemCheckedListener;
            this.configName            = itemView.findViewById(R.id.config_name);
            this.configSwitch          = itemView.findViewById(R.id.config_switch);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position < 0) return;
            clickListener.onItemClick(position, v);
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            if(position < 0) return false;
            longClickListener.onItemClick(position, v);
            return false;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            int position = getAdapterPosition();
            if(position < 0) return;
            onItemCheckedListener.onTunnelChecked(position, b);
        }
    }

    public interface CheckListener {
        void onTunnelChecked(int position, boolean isEnabled);
    }
}

package com.alterdekim.fridaapp.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alterdekim.fridaapp.R;
import com.alterdekim.fridaapp.controller.ControllerId;
import com.alterdekim.fridaapp.controller.ControllerManager;
import com.alterdekim.fridaapp.controller.SingleConfigActivityController;

public class SingleConfigActivity extends AppCompatActivity {

    private static final String TAG = SingleConfigActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_config);

        ControllerManager.putController(new SingleConfigActivityController());
        SingleConfigActivityController controller = (SingleConfigActivityController) ControllerManager.getController(ControllerId.SingleConfigActivityController);

        Intent intent = getIntent();
        String config_title = intent.getStringExtra("config_title");
        byte[] config_data = intent.getByteArrayExtra("config_data");

        controller.onConfigDataAppeared(config_title, config_data);
        controller.onCreateGUI(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        /*
        this.findViewById(R.id.add_app).setOnClickListener(view -> {
            final PackageManager pm = this.getPackageManager();
            List<PackageInfo> apps = pm.getInstalledPackages(0);
            AlertDialog.Builder builder = new AlertDialog.Builder(SingleConfigActivity.this)
                    .setTitle(R.string.choose_app);

            View mRowList = getLayoutInflater().inflate(R.layout.row, null);
            ListView mListView = mRowList.findViewById(R.id.list_view);

            final ArrayAdapter<String> names = new ArrayAdapter<>(SingleConfigActivity.this, android.R.layout.simple_list_item_1);
            for( int i = 0; i < apps.size(); i++ ) {
                names.add(pm.getApplicationLabel(apps.get(i).applicationInfo).toString());
            }
            mListView.setAdapter(names);
            names.notifyDataSetChanged();
            builder.setView(mRowList);
            builder.create().show();
        }); */
    }
}
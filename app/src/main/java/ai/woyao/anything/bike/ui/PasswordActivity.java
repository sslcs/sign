package ai.woyao.anything.bike.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ai.woyao.anything.bike.R;
import ai.woyao.anything.bike.databinding.ActivityPasswordBinding;

public class PasswordActivity extends AppCompatActivity {
    private final static String EXTRA_PASSWORD = "password";

    private ActivityPasswordBinding binding;

    public static Intent getIntent(Context context, String password) {
        Intent intent = new Intent(context, PasswordActivity.class);
        intent.putExtra(EXTRA_PASSWORD, password);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_password);

        setData();
    }

    private void setData() {
        String password = getIntent().getStringExtra(EXTRA_PASSWORD);
        if (!TextUtils.isDigitsOnly(password)) {
            binding.tvTip.setText(R.string.tip_wait);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < password.length(); i++) {
            sb.append(password.charAt(i)).append(" ");
        }
        sb.delete(sb.length() - 1, sb.length());
        binding.tvResult.setText(sb);

        binding.includeTitle.tvTitle.setText(R.string.title_unlock);
        binding.includeTitle.vBack.setVisibility(View.VISIBLE);
        binding.includeTitle.vBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFinish(v);
            }
        });

        DateFormat formatter = SimpleDateFormat.getDateTimeInstance();
        String time = formatter.format(new Date());
        binding.tvUnlockTime.setText(getString(R.string.unlock_time, time));
    }

    public void onClickFinish(View view) {
        onBackPressed();
    }
}

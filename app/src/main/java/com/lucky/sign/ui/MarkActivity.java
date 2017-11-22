package com.lucky.sign.ui;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.lucky.sign.R;
import com.lucky.sign.databinding.ActivityMarkBinding;

/**
 * 录入奖项
 * Created by cs on 17-11-22.
 */

public class MarkActivity extends ScanActivity {
    private ActivityMarkBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_mark);
    }

    @Override
    protected void handleResult(String result) {
        String prize = mBinding.etPrize.getText().toString().trim();
        if (TextUtils.isEmpty(prize)) {
            Toast.makeText(this, R.string.hint_prize, Toast.LENGTH_LONG).show();
            return;
        }

        mark(result, prize);
    }

    private void mark(String result, String prize) {
        String number = result.split(" ")[0];
        Cursor cursor = mHelper.query(number);
        if (!cursor.isAfterLast()) {
            cursor.moveToFirst();
            Record record = mHelper.convert(cursor);
            cursor.close();
            record.prize = prize;
            mHelper.update(record);
        }
    }

    public void onClickBack(View view) {
        onBackPressed();
    }
}

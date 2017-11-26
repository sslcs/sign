package com.lucky.sign.ui;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.lucky.sign.R;
import com.lucky.sign.databinding.ActivitySearchBinding;
import com.lucky.sign.utils.DBHelper;

/**
 * 查询界面
 * Created by cs on 17-11-26.
 */

public class SearchActivity extends AppCompatActivity {
    private ActivitySearchBinding mBinding;
    private DBHelper mHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        mHelper = new DBHelper(this);
    }

    public void onClickBack(View view) {
        onBackPressed();
    }

    public void onClickSearch(View view) {
        String key = mBinding.etPrize.getText().toString();
        Cursor cursor = mHelper.queryPrize(key);
        mBinding.tvCount.setText(getString(R.string.count, cursor.getCount()));
        StringBuilder sb = new StringBuilder();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Record record = mHelper.convert(cursor);
            sb.append(record.number).append(" ").append(record.name);
            if (TextUtils.isEmpty(key)) {
                sb.append(" ").append(record.prize);
            }
            sb.append("\n");
            cursor.moveToNext();
        }
        cursor.close();
        mBinding.tvResult.setText(sb.toString());
    }
}

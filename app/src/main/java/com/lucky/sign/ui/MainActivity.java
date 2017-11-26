package com.lucky.sign.ui;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.lucky.sign.R;
import com.lucky.sign.databinding.ActivityMainBinding;
import com.lucky.sign.utils.DebugLog;

import java.io.File;

import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class MainActivity extends ScanActivity {
    private ActivityMainBinding mBinding;
    private boolean isQuery = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    }

    @Override
    protected void handleResult(String result) {
        if (isQuery) {
            isQuery = false;
            query(result);
            return;
        }

        if (sign(result)) {
            mBinding.tvResult.setText(getString(R.string.result, result));
        } else {
            mBinding.tvResult.setText(getString(R.string.duplicate, result));
        }
    }

    private void query(String result) {
        String number = result.split(" ")[0];
        Cursor cursor = mHelper.query(number);
        if (!cursor.isAfterLast()) {
            cursor.moveToFirst();
            Record record = mHelper.convert(cursor);
            cursor.close();

            String tip;
            if (TextUtils.isEmpty(record.prize)) {
                tip = getString(R.string.no_prize, result);
            } else if (TextUtils.isEmpty(record.exchange)) {
                tip = getString(R.string.has_prize, result, record.prize);
                record.exchange = Record.EXCHANGE;
                mHelper.update(record);
            } else {
                tip = getString(R.string.exchanged, result, record.prize);
            }
            mBinding.tvResult.setText(tip);
        }
    }

    private boolean sign(String result) {
        boolean handle = false;
        String number = result.split(" ")[0];
        Cursor cursor = mHelper.query(number);
        if (!cursor.isAfterLast()) {
            cursor.moveToFirst();
            Record record = mHelper.convert(cursor);
            cursor.close();
            if (TextUtils.isEmpty(record.flag)) {
                record.flag = Record.FLAG_YES;
                handle = true;
                mHelper.update(record);
            }
        }
        return handle;
    }

    public void onClickImport(View view) {
        DebugLog.e("importing begin...");
        Toast.makeText(this, "importing begin...", Toast.LENGTH_LONG).show();
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = "人员名单.xls";
        Workbook workbook = null;
        File file = new File(dir, fileName);
        try {
            workbook = Workbook.getWorkbook(file);
        } catch (Exception e) {
            DebugLog.e("error : " + e.getMessage());
            e.printStackTrace();
        }

        if (workbook == null) {
            return;
        }

        Sheet mSheet = workbook.getSheet(0);
        int mCount = mSheet.getRows();

        Record record;
        mHelper.beginTransaction();
        for (int i = 1; i < mCount; i++) {
            record = new Record();
            record.number = mSheet.getCell(0, i).getContents();
            record.name = mSheet.getCell(1, i).getContents();
            record.flag = mSheet.getCell(2, i).getContents();
            record.prize = mSheet.getCell(3, i).getContents();
            record.exchange = mSheet.getCell(4, i).getContents();
            DebugLog.e("import : " + record.number + " - " + record.name + " - " + record.flag + " - " + record.prize + " - " + record.exchange);
            if (TextUtils.isEmpty(record.number)) {
                break;
            }
            mHelper.importRecord(record);
        }
        mHelper.endTransaction();
        workbook.close();
        DebugLog.e("importing end...");
        Toast.makeText(this, "importing end...", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            mHelper.close();
        }
    }

    public void onClickExport(View view) {
        DebugLog.e("exporting begin...");
        Toast.makeText(this, "exporting begin...", Toast.LENGTH_LONG).show();

        Cursor cursor = mHelper.queryAll();
        if (cursor == null || cursor.isAfterLast()) {
            Toast.makeText(this, "exporting end empty...", Toast.LENGTH_LONG).show();
            return;
        }

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = "人员名单.xls";
        String tempFile = "人员名单_new.xls";

        try {
            Workbook rwb = Workbook.getWorkbook(new File(dir, fileName));
            WritableWorkbook wwb = Workbook.createWorkbook(new File(dir, tempFile), rwb);
            WritableSheet ws = wwb.getSheet(0);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Record record = mHelper.convert(cursor);
                DebugLog.e(record.number + " " + record.name + " " + record.flag + " " + record.prize + " " + record.exchange);
                int position = cursor.getPosition() + 1;
                save(position, ws, record.flag, 2);
                save(position, ws, record.prize, 3);
                save(position, ws, record.exchange, 4);
                cursor.moveToNext();
            }
            cursor.close();
            wwb.write();
            wwb.close();
            rwb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DebugLog.e("exporting end...");
        Toast.makeText(this, "exporting end...", Toast.LENGTH_LONG).show();
    }

    private void save(int position, WritableSheet ws, String value, int index) throws WriteException {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        WritableCell wc = ws.getWritableCell(index, position);
        if (value.equals(wc.getContents())) {
            return;
        }
        if (wc.getType() == CellType.LABEL) {
            Label label = (Label) wc;
            label.setString(value);
        } else if (!TextUtils.isEmpty(ws.getCell(0, position).getContents())) {
            ws.addCell(new Label(index, position, value, wc.getCellFormat()));
        }
    }

    public void onClickExchange(View view) {
        isQuery = true;
        onClickScan(view);
    }

    public void onClickMark(View view) {
        startActivity(new Intent(this, MarkActivity.class));
    }

    public void onClickSearch(View view) {
        startActivity(new Intent(this, SearchActivity.class));
    }
}

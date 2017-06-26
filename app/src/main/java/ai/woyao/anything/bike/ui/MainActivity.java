package ai.woyao.anything.bike.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Type;

import ai.woyao.anything.bike.R;
import ai.woyao.anything.bike.constants.Action;
import ai.woyao.anything.bike.net.Api;
import ai.woyao.anything.bike.net.NetCallback;
import ai.woyao.anything.bike.net.NetParams;
import ai.woyao.anything.bike.net.bean.params.ParamsQr;
import ai.woyao.anything.bike.net.bean.response.DataPassword;
import ai.woyao.anything.bike.net.bean.response.DataResponse;
import ai.woyao.anything.bike.net.bean.response.ServerResponse;
import ai.woyao.anything.bike.utils.DebugLog;
import ai.woyao.anything.bike.utils.GsonSingleton;

public class MainActivity extends TaskActivity {
    private final int REQUEST_CODE_PERMISSION_LOCATION = 100;
    private final int REQUEST_CODE_PERMISSION_CAMERA = 101;
    private final int REQUEST_CODE_GPS_SETTING = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        checkLocation(null);
    }

    private void checkLocation(ParamsQr paramsQr) {
        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) && !hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSION_LOCATION);
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location == null) {
            if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage(getString(R.string.tip_open_gps));
                dialog.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // 转到手机设置界面，用户设置GPS
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, REQUEST_CODE_GPS_SETTING);
                            }
                        });
                dialog.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        } else if (paramsQr != null) {
            paramsQr.lat = location.getLatitude();
            paramsQr.lng = location.getLongitude();
        }
    }

    public void onClickNew(View view) {
        if (!hasPermission(Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION_CAMERA);
            return;
        }

        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.initiateScan();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_GPS_SETTING == requestCode) {
            checkLocation(null);
            return;
        }

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            String result = scanResult.getContents();
            if (TextUtils.isEmpty(result)) return;

            DebugLog.e("result : " + result);
            unlock(result);
        }
    }

    private void unlock(String bikeId) {
        String action = Action.ACTION_UNLOCK_BIKE;

        ParamsQr paramsQr = new ParamsQr();
        paramsQr.bike_id = bikeId;
        checkLocation(paramsQr);

        NetParams.Builder builder = new NetParams.Builder();
        builder.add(action, action, paramsQr);
        byte[] params = null;
        try {
            params = builder.build();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        NetCallback<ServerResponse> callback = new NetCallback<ServerResponse>() {
            @Override
            public void onSuccess(ServerResponse response) {
                DebugLog.e("onSuccess");
                String encoded = response.data;
                byte[] decode = Base64.decode(encoded, Base64.DEFAULT);
                String data = new String(decode);
                int index = data.indexOf("{");
                if (index != -1) {
                    data = data.substring(index);
                    DebugLog.e("response : " + data);
                }

                Type type = new TypeToken<DataResponse<DataPassword>>() {}.getType();
                DataResponse<DataPassword> dataResponse = GsonSingleton.get().fromJson(data, type);
                if (dataResponse.isSuccess()) {
                    showPassword(dataResponse.data.password);
                } else {
                    onFail(new Throwable(dataResponse.error));
                }
            }

            @Override
            public void onFail(Throwable e) {
                DebugLog.e(e);
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        addTask(Api.request(params, callback));
    }

    private void showPassword(String password) {
        startActivity(PasswordActivity.getIntent(this, password));
    }

    private boolean hasPermission(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION_CAMERA) {
            onClickNew(null);
        } else if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {
            checkLocation(null);
        }
    }
}

package ai.woyao.anything.bike.ui;

import android.content.Intent;
import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    public void onClickNew(View view) {
        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.initiateScan();

//        String bikeId = "http://ofo.so/plate/8329093";
//        unlock(bikeId);

//        showPassword("1212");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            String result = scanResult.getContents();
            if (TextUtils.isEmpty(result)) return;

            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
            unlock(result);
        }
    }

    private void unlock(String bikeId) {
        String action = Action.ACTION_UNLOCK_BIKE;

        ParamsQr paramsQr = new ParamsQr();
        paramsQr.bike_id = bikeId;
        paramsQr.lat = 18;
        paramsQr.lng = 122;

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
}

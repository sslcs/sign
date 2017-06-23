package ai.woyao.bike.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import ai.woyao.bike.R;
import ai.woyao.bike.constants.Action;
import ai.woyao.bike.net.Api;
import ai.woyao.bike.net.NetCallback;
import ai.woyao.bike.net.NetParams;
import ai.woyao.bike.net.bean.params.ParamsQr;
import ai.woyao.bike.net.bean.response.BaseResponse;
import ai.woyao.bike.net.bean.response.DataPassword;
import ai.woyao.bike.utils.DebugLog;

public class MainActivity extends TaskActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickNew(View view) {
//        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
//        integrator.initiateScan();

        String bikeId = "http://ofo.so/plate/8329093";
        unlock(bikeId);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            String result = scanResult.getContents();
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

        NetCallback<BaseResponse<DataPassword>> callback = new NetCallback<BaseResponse<DataPassword>>() {
            @Override
            public void onSuccess(BaseResponse<DataPassword> response) {
                DebugLog.e("onSuccess");
            }

            @Override
            public void onFail(Throwable e) {
                DebugLog.e(e);
            }
        };
        addTask(Api.request(params, callback));
    }


}

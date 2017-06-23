package ai.woyao.bike.net;

import android.util.Base64;

import ai.woyao.bike.net.bean.response.BaseResponse;
import ai.woyao.bike.net.retrofit.RetrofitSender;
import ai.woyao.bike.utils.GsonSingleton;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class Api {
    public static <T> NetTask request(byte[] params, NetCallback<BaseResponse<T>> callback) {
        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), params);
        Subscription task = RetrofitSender.getInstance()
                .post(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<BaseResponse, BaseResponse<T>>() {
                    @Override
                    public BaseResponse<T> call(BaseResponse baseResponse) {
                        String encoded = (String) baseResponse.data;
                        byte[] decode = Base64.decode(encoded, Base64.DEFAULT);
                        String data = new String(decode);
                        int index = data.indexOf("{");
                        if (index != -1) {
                            data = data.substring(index);
                        }

                        BaseResponse<T> response = GsonSingleton.get().fromJson(data, BaseResponse.class);
                        return response;
                    }
                })
                .subscribe(callback);
        return new NetTask(task);
    }
}

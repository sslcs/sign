package ai.woyao.bike.net.retrofit;


import ai.woyao.bike.net.bean.response.BaseResponse;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface AppServer {
    @POST("v1/entry")
    Observable<BaseResponse> post(@Body RequestBody body);
}

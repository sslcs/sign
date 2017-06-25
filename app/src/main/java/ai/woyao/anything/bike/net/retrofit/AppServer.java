package ai.woyao.anything.bike.net.retrofit;


import ai.woyao.anything.bike.net.bean.response.ServerResponse;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface AppServer {
    @POST("v1/entry")
    Observable<ServerResponse> post(@Body RequestBody body);
}

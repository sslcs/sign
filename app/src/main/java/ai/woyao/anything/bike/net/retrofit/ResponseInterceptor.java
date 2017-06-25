package ai.woyao.anything.bike.net.retrofit;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by LiuCongshan on 17-6-23.
 */

public class ResponseInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        response.body().source().buffer();
        return response;
    }
}

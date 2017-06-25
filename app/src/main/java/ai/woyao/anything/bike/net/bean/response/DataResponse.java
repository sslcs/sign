package ai.woyao.anything.bike.net.bean.response;

import com.google.gson.annotations.SerializedName;

public class DataResponse<T> {
    @SerializedName("c")
    public int code;

    @SerializedName("d")
    public T data;

    @SerializedName("err")
    public String error;

    public boolean isSuccess() {
        return 0 == code;
    }
}

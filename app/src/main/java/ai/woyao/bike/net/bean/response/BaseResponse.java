package ai.woyao.bike.net.bean.response;

import com.google.gson.annotations.SerializedName;

public class BaseResponse<T> {
    @SerializedName("c")
    public int code;

    @SerializedName("d")
    public T data;

    @SerializedName("err")
    public String error;
}

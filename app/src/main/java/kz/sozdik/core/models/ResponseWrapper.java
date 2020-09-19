package kz.sozdik.core.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseWrapper<T> {

    public static final int RESULT_OK = 1;
    public static final int RESULT_TOKEN_EXPIRED = -17;

    @SerializedName("api_method")
    @Expose
    private String apiMethod;
    @SerializedName("api_version")
    @Expose
    private String apiVersion;
    @SerializedName("data")
    @Expose
    private T data;
    @SerializedName("execution_time")
    @Expose
    private double executionTime;
    @SerializedName("host")
    @Expose
    private String host;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("request_time")
    @Expose
    private int requestTime;
    @SerializedName("result")
    @Expose
    private int result;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

}
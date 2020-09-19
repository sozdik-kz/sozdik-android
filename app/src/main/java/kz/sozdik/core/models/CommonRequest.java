package kz.sozdik.core.models;

public class CommonRequest {

    private String deviceToken;

    protected CommonRequest(Builder builder) {
        deviceToken = builder.deviceToken;
    }

    public static class Builder<T extends Builder<T>> {
        private String deviceToken;

        public Builder() {
        }

        public T deviceToken(String val) {
            deviceToken = val;
            return (T) this;
        }

        public CommonRequest build() {
            return new CommonRequest(this);
        }
    }

    public String getDeviceToken() {
        return deviceToken;
    }
}

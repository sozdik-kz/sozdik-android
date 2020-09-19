package kz.sozdik.presentation.utils;

import java.net.HttpURLConnection;

import kz.sozdik.R;
import kz.sozdik.core.exceptions.HttpException;
import kz.sozdik.core.exceptions.NoNetworkException;
import kz.sozdik.core.system.ResourceManager;

public class ErrorMessageFactory {

    public static String create(ResourceManager resourceManager, Throwable exception) {
        if (exception instanceof NoNetworkException) {
            return resourceManager.getString(R.string.error_no_internet_connection);
        } else if (exception instanceof HttpException) {
            int code = ((HttpException) exception).getErrorCode();
            switch (code) {
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    return resourceManager.getString(R.string.error_http_401);
            }
        }
        return resourceManager.getString(R.string.error_default_exception);
    }
}

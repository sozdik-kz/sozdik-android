package kz.sozdik.dictionary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class ObservableWebView extends WebView {
    private OnScrollChangedCallback onScrollChangedCallback;

    public ObservableWebView(final Context context) {
        super(context);
    }

    public ObservableWebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableWebView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollChangedCallback != null)
            onScrollChangedCallback.onScroll(l, t, oldl, oldt);
    }

    public OnScrollChangedCallback getOnScrollChangedCallback() {
        return onScrollChangedCallback;
    }

    public void setOnScrollChangedCallback(final OnScrollChangedCallback onScrollChangedCallback) {
        this.onScrollChangedCallback = onScrollChangedCallback;
    }

    /**
     * Implement in the activity/fragment/view that you want to listen to the webView
     */
    public interface OnScrollChangedCallback {
        void onScroll(int l, int t, int oldl, int oldt);
    }
}

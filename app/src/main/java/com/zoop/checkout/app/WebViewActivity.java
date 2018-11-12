package com.zoop.checkout.app;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.zoop.zoopandroidsdk.commons.APIParameters;

/**
 * Created by mainente on 05/05/15.
 */
public class WebViewActivity extends ZCLMenuWithHomeButtonActivity {

    private WebView WbDashboard;
    private ProgressBar progress;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        WbDashboard = (WebView) findViewById(R.id.webView1);

        String sURLPortal =ApplicationConfiguration.WEB_PORTAL_URL_WITH_SLUG+"#signin";



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WbDashboard.setWebContentsDebuggingEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE))
            { WbDashboard.setWebContentsDebuggingEnabled(true); }
        }

        WbDashboard.getSettings().setJavaScriptEnabled(true);

        WbDashboard.setWebChromeClient(new MyWebViewClient());

        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setMax(100);

        WbDashboard.loadUrl(sURLPortal);
        WebViewActivity.this.progress.setProgress(0);
        WbDashboard.setWebViewClient(
                new SSLTolerentWebViewClient()
        );

    }
    private class SSLTolerentWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.cancel();
        }

    }
    private class MyWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            WebViewActivity.this.setValue(newProgress);
            super.onProgressChanged(view, newProgress);
        }
        @Override
        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
            // TODO Auto-generated method stub
            Log.v("ChromeClient", "invoked: onConsoleMessage() - " + sourceID + ":"
                    + lineNumber + " - " + message);
            super.onConsoleMessage(message, lineNumber, sourceID);
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            Log.v("ChromeClient", cm.message() + " -- From line "
                    + cm.lineNumber() + " of "
                    + cm.sourceId());
            return true;
        }
    }



    public void setValue(int progress) {
        this.progress.setProgress(progress);
    }
}

package com.mobile.roadie.clicksandclunks;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by thinuwan on 8/15/17.
 */

public class JavaScriptInterface {
    private Activity activity;

    public JavaScriptInterface(Activity activiy) {
        this.activity = activiy;
    }

    @JavascriptInterface
    public void showHidePopUpJs(String showOrHide){
        javaScriptNativeInterface.showHidePopUpNative(showOrHide);
    }

    @JavascriptInterface
    public void getFullHtmlContentJs(String showOrHide){
        javaScriptNativeInterface.getFullHtmlContentToNative(showOrHide);
    }

    @JavascriptInterface
    public void showFullHtmlContentIfAlreadySavesJs(){
        javaScriptNativeInterface.showFullHtmlContentIfAlreadySavesNative();
    }

    @JavascriptInterface
    public void showPopupForClickedItemJs(String id_only){
        javaScriptNativeInterface.showPopupForClickedItemNative(id_only);
    }

    public void getJavaScriptNativeInterface(JavaScriptInterface.JavaScriptNativeInterface javaScriptNativeInterface){
        this.javaScriptNativeInterface = javaScriptNativeInterface;
    }

    private JavaScriptInterface.JavaScriptNativeInterface javaScriptNativeInterface = null;

    public interface JavaScriptNativeInterface {
        void showHidePopUpNative(String showOrHide);
        void getFullHtmlContentToNative(String fullHtmlContent);
        void showFullHtmlContentIfAlreadySavesNative();
        void showPopupForClickedItemNative(String id_only);
    }
}

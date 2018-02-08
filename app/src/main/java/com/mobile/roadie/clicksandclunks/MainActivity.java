package com.mobile.roadie.clicksandclunks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {

    private boolean mIsPopupOpened = false;
    private WebView mWebview;
    private LinearLayout linearLayout_popup_parent;
    private Button button_save;
    private Button button_cancel;
    private ImageButton imagebutton_delete;
    private LinearLayout linearLayout_clicks_select;
    private LinearLayout linearLayout_clanks_select;
    private LinearLayout linearLayout_clunks_select;
    private String highlightType = "";
    private String saveOrEdit = "save";
    private Context mContext;
    private View view_icon_click;
    private View view_icon_clank;
    private View view_icon_clunk;
    private EditText editText_comment;
    private HighlighObject mHighlighObject;
    private String updatingHighlightingId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        mWebview = (WebView)findViewById(R.id.activity_main_webview);
        linearLayout_popup_parent = (LinearLayout)findViewById(R.id.activity_main_linearlayout_popup_parent);
        button_save = (Button)findViewById(R.id.activity_main_button_save);
        button_cancel = (Button)findViewById(R.id.activity_main_button_cancel);
        imagebutton_delete = (ImageButton)findViewById(R.id.activity_main_imagebutton_delete);
        linearLayout_clicks_select = (LinearLayout)findViewById(R.id.activity_main_linearlayout_clicks_select);
        linearLayout_clanks_select = (LinearLayout)findViewById(R.id.activity_main_linearlayout_clanks_select);
        linearLayout_clunks_select = (LinearLayout)findViewById(R.id.activity_main_linearlayout_clunks_select);
        view_icon_click = findViewById(R.id.activity_main_view_icon_click);
        view_icon_clank = findViewById(R.id.activity_main_view_icon_clank);
        view_icon_clunk = findViewById(R.id.activity_main_view_icon_clunk);
        editText_comment = (EditText)findViewById(R.id.activity_main_edittext);


        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebview.setWebChromeClient(new WebChromeClient());
        mWebview.loadUrl("file:///android_asset/index.html");

        mWebview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                highlightType = "";
                changeHighlightType();
                chagePopupFooterStatus();
                mWebview.loadUrl("javascript:removeTemporyHighlighter()");
                return false;
            }
        });

        mWebview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                saveOrEdit = "save";
                return false;
            }
        });

        JavaScriptInterface jsInterface = new JavaScriptInterface(this);
        mWebview.addJavascriptInterface(jsInterface, "JSInterface");
        jsInterface.getJavaScriptNativeInterface(new JavaScriptInterface.JavaScriptNativeInterface() {
            @Override
            public void showHidePopUpNative(String showOrHide) {
                showHidePopUp(showOrHide);
            }

            @Override
            public void getFullHtmlContentToNative(final String fullHtmlContent) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HighlightSharedPreferences.getInstance(mContext).updateFullHtmlContent(fullHtmlContent);
                        changeHighlightType();
                    }
                });
            }

            @Override
            public void showFullHtmlContentIfAlreadySavesNative() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hitHtmlContentIfAlreadySaved();
                    }
                });
            }

            @Override
            public void showPopupForClickedItemNative(final String id_only) {
                updatingHighlightingId = id_only;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        saveOrEdit = "edit";
                        mHighlighObject = HighlightSharedPreferences.getInstance(mContext).getHighlightObjectByIdFromHighlightArray(id_only);
                        setValuesToPopUpInItemClick(mHighlighObject);
                        if(!mIsPopupOpened){
                            mIsPopupOpened = true;
                            openPopUp();
                        }
                    }
                });
            }
        });

        try {
            InputStream stream = this.getApplication().getAssets().open("index.html");
            int streamSize = stream.available();
            byte[] buffer = new byte[streamSize];
            stream.read(buffer);
            stream.close();
            String html = new String(buffer);
            mWebview.loadDataWithBaseURL("file:///android_asset/",html,"text/html","UTF-8",null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(highlightType != ""){
                    if(saveOrEdit.equals("save")){
                        saveHighlighting();
                    }
                    else{
                        updateHighlighting();
                    }
                    mWebview.clearFocus();
                    closePopUp();
                    mIsPopupOpened = false;
                    changeHighlightType();
                }
            }
        });

        imagebutton_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(highlightType != ""){
                    if(saveOrEdit.equals("edit")){
                        deleteHighlighting();
                    }
                    mWebview.clearFocus();
                    closePopUp();
                    mIsPopupOpened = false;
                    changeHighlightType();
                }
            }
        });


        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePopUp();
                mIsPopupOpened = false;
                changeHighlightType();
                chagePopupFooterStatus();
                mWebview.loadUrl("javascript:removeTemporyHighlighter()");
            }
        });

        linearLayout_clicks_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveOrEdit.equals("save")){
                    highlightType = "click";
                    changeHighlightType();
                    mWebview.loadUrl("javascript:highlighTemporaryUntilSaveOrCancel('#90db3d')");
                    mWebview.clearFocus();
                }
            }
        });

        linearLayout_clanks_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveOrEdit.equals("save")){
                    highlightType = "clank";
                    changeHighlightType();
                    mWebview.loadUrl("javascript:highlighTemporaryUntilSaveOrCancel('#ffd232')");
                    mWebview.clearFocus();
                }
            }
        });

        linearLayout_clunks_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveOrEdit.equals("save")){
                    highlightType = "clunk";
                    changeHighlightType();
                    mWebview.loadUrl("javascript:highlighTemporaryUntilSaveOrCancel('#fc92cf')");
                    mWebview.clearFocus();
                }
            }
        });

        editText_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(highlightType == ""){
                    Toast.makeText(mContext,"Please select highlight type", Toast.LENGTH_LONG).show();
                }
                else{
                    mWebview.loadUrl("javascript:highlighTemporaryUntilSaveOrCancel('#b3ecff')");
                }
            }
        });
    }

    private void hitHtmlContentIfAlreadySaved(){
        String fullHtmlContent = HighlightSharedPreferences.getInstance(mContext).getFullHtmlContent();
        if(fullHtmlContent != ""){
            mWebview.loadUrl("javascript:setfullHtmlContentIfAlreadySaves('"+fullHtmlContent+"')");
        }
    }

    private void openPopUp(){
        linearLayout_popup_parent.setVisibility(View.VISIBLE);
        Animation animSet = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in_from_bottom);
        linearLayout_popup_parent.startAnimation(animSet);
    }

    private void closePopUp(){
        editText_comment.clearFocus();
        editText_comment.setText("");
        hideKeyboard(this);
        editText_comment.setFocusable(false);
        editText_comment.setFocusableInTouchMode(false);
        Animation animSet = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_out_to_bottom);
        linearLayout_popup_parent.startAnimation(animSet);
        animSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linearLayout_popup_parent.setVisibility(View.GONE);
                highlightType ="";
                changeHighlightType();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void changeHighlightType(){
        linearLayout_clicks_select.setBackgroundColor(ContextCompat.getColor(this, R.color.activity_main_linearlayout_highlighttype_inactive));
        linearLayout_clanks_select.setBackgroundColor(ContextCompat.getColor(this, R.color.activity_main_linearlayout_highlighttype_inactive));
        linearLayout_clunks_select.setBackgroundColor(ContextCompat.getColor(this, R.color.activity_main_linearlayout_highlighttype_inactive));
        view_icon_click.setBackgroundResource(R.drawable.click_box_inactive);
        view_icon_clank.setBackgroundResource(R.drawable.clank_box_inactive);
        view_icon_clunk.setBackgroundResource(R.drawable.clunk_box_inactive);

        editText_comment.setFocusable(false);
        editText_comment.setFocusableInTouchMode(false);
//        button_save.setTextColor(ContextCompat.getColor(mContext, R.color.views_inactive_text_color));

        if(highlightType.equals("click")){
            linearLayout_clicks_select.setBackgroundColor(ContextCompat.getColor(this, R.color.activity_main_linearlayout_highlighttype_active));
            view_icon_click.setBackgroundResource(R.drawable.click_box_active);
            editText_comment.setFocusable(true);
            editText_comment.setFocusableInTouchMode(true);
        }
        else if(highlightType.equals("clank")){
            linearLayout_clanks_select.setBackgroundColor(ContextCompat.getColor(this, R.color.activity_main_linearlayout_highlighttype_active));
            view_icon_clank.setBackgroundResource(R.drawable.clank_box_active);
            editText_comment.setFocusable(true);
            editText_comment.setFocusableInTouchMode(true);
        }
        else if(highlightType.equals("clunk")){
            linearLayout_clunks_select.setBackgroundColor(ContextCompat.getColor(this, R.color.activity_main_linearlayout_highlighttype_active));
            view_icon_clunk.setBackgroundResource(R.drawable.clunk_box_active);
            editText_comment.setFocusable(true);
            editText_comment.setFocusableInTouchMode(true);
        }
        chagePopupFooterStatus();
    }

    private void saveHighlighting(){
        String highlightTagId = HighlightSharedPreferences.getInstance(mContext).addHighlight(highlightType,editText_comment.getText().toString());
        editText_comment.setText("");
        button_save.setTextColor(ContextCompat.getColor(mContext, R.color.views_inactive_text_color));
        mWebview.loadUrl("javascript:convertTemporyHighlightToOriginal('"+highlightTagId+"')");
    }

    private void updateHighlighting(){
        HighlightSharedPreferences.getInstance(mContext).updateHighlight(updatingHighlightingId, highlightType,editText_comment.getText().toString());
        editText_comment.setText("");
        button_save.setTextColor(ContextCompat.getColor(mContext, R.color.views_inactive_text_color));
        mWebview.loadUrl("javascript:getFullHtmlContent()");
    }

    private void deleteHighlighting(){
        HighlightSharedPreferences.getInstance(mContext).deleteHighlight(updatingHighlightingId);
        mWebview.loadUrl("javascript:removeOriginalHighlighter('"+updatingHighlightingId+"')");
        editText_comment.setText("");
        button_save.setTextColor(ContextCompat.getColor(mContext, R.color.views_inactive_text_color));
    }

    public void showHidePopUp(final String showOrHide){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(showOrHide.equals("show")){
                    if(!mIsPopupOpened){
                        openPopUp();
                        mIsPopupOpened = true;
                    }
                }
                else if(showOrHide.equals("hide") && highlightType.length() == 0){
                    if(mIsPopupOpened){
                        closePopUp();
                        mIsPopupOpened = false;
                    }
                }
            }
        });
    }

    public void chagePopupFooterStatus(){

        imagebutton_delete.setBackgroundResource(R.drawable.ic_delete_forever_black_24dp_inactive);
        button_save.setTextColor(ContextCompat.getColor(mContext, R.color.views_inactive_text_color));

        if(saveOrEdit.equals("save") && (highlightType != "")){
            button_save.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        }
        else if(saveOrEdit.equals("edit")){
            imagebutton_delete.setBackgroundResource(R.drawable.ic_delete_forever_black_24dp_active);
            button_save.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void setValuesToPopUpInItemClick(HighlighObject highlighObject){
        highlightType = highlighObject.highlightType;
        editText_comment.setText(highlighObject.highlightComment);
        saveOrEdit = "edit";
        changeHighlightType();
        chagePopupFooterStatus();
    }
}


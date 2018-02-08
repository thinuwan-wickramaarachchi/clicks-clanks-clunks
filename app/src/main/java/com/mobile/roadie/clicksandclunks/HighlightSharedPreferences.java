package com.mobile.roadie.clicksandclunks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by thinuwan on 8/15/17.
 */

public class HighlightSharedPreferences extends Activity{

    private static HighlightSharedPreferences sInstance;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Context mContext;
    private HighlightArray mHighlightArray;

    public HighlightSharedPreferences(Context context) {
        this.mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mSharedPreferences.edit();
    }

    public static HighlightSharedPreferences getInstance(Context context){
        if(sInstance == null){
            sInstance = new HighlightSharedPreferences(context);
        }
        return sInstance;
    }

    public void updateFullHtmlContent(String fullHtmlContent){
        mEditor.putString("fullHtmlContent", fullHtmlContent);
        mEditor.commit();
        String xxx = getFullHtmlContent();
        Log.e("xxx",xxx);
    }

    public String addHighlight(String highlight_type, String highlight_comment){
        HighlightArray highlightArray = getHighlightArray();
        int nextHighlightingId;
        if(highlightArray.highlightArray.size() == 0){
            nextHighlightingId = 1;
        }
        else{
            nextHighlightingId = Integer.parseInt( highlightArray.highlightArray.get(  highlightArray.highlightArray.size()-1  ).highlightId ) + 1;
        }
        saveHighlight(String.valueOf(nextHighlightingId),highlight_type, highlight_comment);
        return String.valueOf(nextHighlightingId);
    }

    public void updateHighlight(String highlight_id, String highlight_type, String highlight_comment){
        HighlightArray highlightArray;
        String arrayString = mSharedPreferences.getString("highlightJsonArrayInString", null);
        if(arrayString != null){
            Gson gson = new Gson();
            highlightArray = gson.fromJson(arrayString, HighlightArray.class);
            for (int i = 0; i < highlightArray.highlightArray.size(); i++){
                if(highlight_id.equals(highlightArray.highlightArray.get(i).highlightId)){
                    highlightArray.highlightArray.get(i).highlightType = highlight_type;
                    highlightArray.highlightArray.get(i).highlightComment = highlight_comment;
                    mEditor.putString("highlightJsonArrayInString", gson.toJson(highlightArray));
                    mEditor.commit();
                }
            }
        }
    }

    public void deleteHighlight(String highlight_id){
        HighlightArray highlightArray;
        String arrayString = mSharedPreferences.getString("highlightJsonArrayInString", null);
        if(arrayString != null){
            Gson gson = new Gson();
            highlightArray = gson.fromJson(arrayString, HighlightArray.class);
            for (int i = 0; i < highlightArray.highlightArray.size(); i++){
                if(highlight_id.equals(highlightArray.highlightArray.get(i).highlightId)){
                    highlightArray.highlightArray.remove(i);
                    mEditor.putString("highlightJsonArrayInString", gson.toJson(highlightArray));
                    mEditor.commit();
                }
            }
        }
    }

    public void saveHighlight(String span_id, String highlight_type, String highlight_comment){
        HighlighObject highlighObject = new HighlighObject();
        highlighObject.highlightId = span_id;
        highlighObject.highlightType = highlight_type;
        highlighObject.highlightComment = highlight_comment;
        HighlightArray highlightArray = getHighlightArray();
        highlightArray.highlightArray.add(highlighObject);
        Gson gson = new Gson();
        mEditor.putString("highlightJsonArrayInString", gson.toJson(highlightArray));
        mEditor.commit();
    }

    public HighlightArray getHighlightArray(){
        HighlightArray highlightArray;
        String arrayString = mSharedPreferences.getString("highlightJsonArrayInString", null);
        if(arrayString == null){
            highlightArray = new HighlightArray();
            highlightArray.highlightArray = new ArrayList<>();
        } else {
            Gson gson = new Gson();
            highlightArray = gson.fromJson(arrayString, HighlightArray.class);
        }
        return  highlightArray;
    }

    public HighlighObject getHighlightObjectByIdFromHighlightArray(String id){
        mHighlightArray = getHighlightArray();
        HighlighObject highlighObject;
        for (int i = 0; i < mHighlightArray.highlightArray.size(); i++){
            highlighObject = mHighlightArray.highlightArray.get(i);
            if(id.equals(highlighObject.highlightId)){
                return highlighObject;
            }
        }
        return null;
    }

    public String getFullHtmlContent(){
        String fullHtmlContent = mSharedPreferences.getString("fullHtmlContent", "");
        return fullHtmlContent;
    }
}

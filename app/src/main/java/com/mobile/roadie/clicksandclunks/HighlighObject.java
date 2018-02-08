package com.mobile.roadie.clicksandclunks;

/**
 * Created by thinuwan on 8/15/17.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HighlighObject {

    @SerializedName("highlight_id")
    @Expose
    public String highlightId;
    @SerializedName("highlight_type")
    @Expose
    public String highlightType;
    @SerializedName("highlight_comment")
    @Expose
    public String highlightComment;

}

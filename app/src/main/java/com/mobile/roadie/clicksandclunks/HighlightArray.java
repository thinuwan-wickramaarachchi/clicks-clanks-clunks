package com.mobile.roadie.clicksandclunks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by thinuwan on 8/15/17.
 */

public class HighlightArray {

    @SerializedName("highlight_array")
    @Expose
    public List<HighlighObject> highlightArray = null;

}


$(document).ready(function(){
    JSInterface.showFullHtmlContentIfAlreadySavesJs();
});

$(document).on('click', function(evt) {
    console.log("++++++++++++++++++++++++++++");
    console.log(evt.target.id);
    if(evt.target.id != null){
        if(  evt.target.id.includes("highlight_span_")  ){
            var id = evt.target.id;
            var only_id = id.split("_")[2];
            JSInterface.showPopupForClickedItemJs(only_id)
        }
    }
});

var rangeForTemporyHighlight;

document.addEventListener("selectionchange", function() {

    var selected_html_content = getHTMLOfSelection();
    if(selected_html_content.includes("highlight_span") || selected_html_content.length == 0){
        JSInterface.showHidePopUpJs("hide");
        rangeForTemporyHighlight = getSelection().getRangeAt(0);
    }
    else{
        JSInterface.showHidePopUpJs("show");
        rangeForTemporyHighlight = getSelection().getRangeAt(0);
    }
});

function convertTemporyHighlightToOriginal(nextHighlightingId){
    var span_tempory_highlight = $('.tempory_highlighter');
    span_tempory_highlight.attr('id', "highlight_span_"+nextHighlightingId);
    span_tempory_highlight.removeClass( "tempory_highlighter" );
    getFullHtmlContent();
}

function getFullHtmlContent(){
    var fullHtmlContent = $('#div_content').html();
    JSInterface.getFullHtmlContentJs(fullHtmlContent);
}

function setfullHtmlContentIfAlreadySaves(fullHtmlContent){
    $('#div_content').html(fullHtmlContent.toString());
}

function selectHighlightedHtml(array_no) {
    try {
        if (window.ActiveXObject) {
            var c = document.selection.createRange();
            return c.htmlText;
        }

        var nNd = document.createElement('highlight_span');
        nNd.setAttribute("id", "highlight_span_"+array_no);
        var w = getSelection().getRangeAt(0);
        w.surroundContents(nNd);
        return nNd.innerHTML;
    } catch (e) {
        if (window.ActiveXObject) {
            return document.selection.createRange();
        } else {
            return getSelection();
        }
    }
}

function highlighTemporaryUntilSaveOrCancel(color){
//    console.log(rangeForTemporyHighlight+"  ----");
    var nNd = document.createElement("highlight_span");
    nNd.setAttribute("class", "tempory_highlighter");
//    nNd.addEventListener("click", function(e) {
//           var only_id = (this.id).split("_")[2];
//           JSInterface.showPopupForClickedItemJs(only_id)
//        }, false);
    rangeForTemporyHighlight.surroundContents(nNd);
    $('.tempory_highlighter').css({"background": color});
//    console.log("---------------------------------------------------------------")
//    console.log( $('#div_content').html() );
//    console.log("---------------------------------------------------------------")
}

function removeTemporyHighlighter(){
    var span_tempory_highlight = $('.tempory_highlighter');
    span_tempory_highlight.contents().unwrap();
}

function removeOriginalHighlighter(highlighter_id){
    var span_tempory_highlight = $('#highlight_span_'+highlighter_id);
    span_tempory_highlight.contents().unwrap();
    getFullHtmlContent();
}

function getHTMLOfSelection () {
  var range;
  if (document.selection && document.selection.createRange) {
    range = document.selection.createRange();
    return range.htmlText;
  }
  else if (window.getSelection) {
    var selection = window.getSelection();
    if (selection.rangeCount > 0) {
      range = selection.getRangeAt(0);
      var clonedSelection = range.cloneContents();
      var div = document.createElement('div');
      div.appendChild(clonedSelection);
      return div.innerHTML;
    }
    else {
      return '';
    }
  }
  else {
    return '';
  }
}
@Override
public void onPageFinished(final android.webkit.WebView view, final String url) {
    if (view == null || url == null) {
        return;
    }

    // Inject JavaScript to wrap iframes with transparent anchors
    final String javascript = "javascript:(function() {" +
            "var iframes = document.getElementsByTagName('iframe');" +
            "for (var i = 0, l = iframes.length; i < l; i++) {" +
            "  var iframe = iframes[i];" +
            "  if (!iframe || !iframe.offsetParent) { continue; }" +
            "  var a = document.createElement('a');" +
            "  a.setAttribute('href', iframe.src);" +
            "  var d = document.createElement('div');" +
            "  d.style.width = iframe.offsetWidth + 'px';" +
            "  d.style.height = iframe.offsetHeight + 'px';" +
            "  d.style.top = iframe.offsetTop + 'px';" +
            "  d.style.left = iframe.offsetLeft + 'px';" +
            "  d.style.position = 'absolute';" +
            "  d.style.opacity = '0';" +
            "  d.style.filter = 'alpha(opacity=0)';" +
            "  d.style.background = 'black';" +
            "  a.appendChild(d);" +
            "  iframe.offsetParent.appendChild(a);" +
            "}" +
            "})();";

    view.loadUrl(javascript);

    super.onPageFinished(view, url);
}
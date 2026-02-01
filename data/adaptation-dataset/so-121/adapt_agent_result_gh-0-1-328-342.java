        @Override
        public void onPageFinished(android.webkit.WebView view, String url) {
            // Ensure WebView is valid before injecting JavaScript
            if (view != null && url != null && url.length() > 0) {
                StringBuilder javascript = new StringBuilder();
                javascript.append("javascript:(function(){");
                javascript.append("var iframes = document.getElementsByTagName('iframe');");
                javascript.append("for (var i = 0, l = iframes.length; i < l; i++) {");
                javascript.append("var iframe = iframes[i];");
                javascript.append("var a = document.createElement('a');");
                javascript.append("a.setAttribute('href', iframe.src);");
                javascript.append("var d = document.createElement('div');");
                javascript.append("d.style.width = iframe.offsetWidth + 'px';");
                javascript.append("d.style.height = iframe.offsetHeight + 'px';");
                javascript.append("d.style.top = iframe.offsetTop + 'px';");
                javascript.append("d.style.left = iframe.offsetLeft + 'px';");
                javascript.append("d.style.position = 'absolute';");
                javascript.append("d.style.opacity = '0';");
                javascript.append("d.style.filter = 'alpha(opacity=0)';");
                javascript.append("d.style.background = 'black';");
                javascript.append("a.appendChild(d);");
                javascript.append("iframe.offsetParent.appendChild(a);");
                javascript.append("}");
                javascript.append("})();");

                view.loadUrl(javascript.toString());
            }

            // Invoke superclass implementation after script injection
            super.onPageFinished(view, url);
        }
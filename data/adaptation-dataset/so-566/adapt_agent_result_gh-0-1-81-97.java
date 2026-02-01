@Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
    if (!(request instanceof HttpServletRequest)) {
      chain.doFilter(request, response);
      return;
    }

    final HttpServletRequest hsr = (HttpServletRequest) request;
    final ServletFilter[] filtersArray = this.filters;

    // Short-circuit when no filters are configured
    if (filtersArray == null || filtersArray.length == 0) {
      chain.doFilter(hsr, response);
      return;
    }

    final String requestUri = hsr.getRequestURI();
    final String contextPath = hsr.getContextPath();
    final String path;
    if (requestUri == null) {
      path = "";
    } else if (contextPath != null && !contextPath.isEmpty()) {
      path = requestUri.replaceFirst(contextPath, "");
    } else {
      path = requestUri;
    }

    final GodFilterChain godChain = new GodFilterChain(chain);

    for (ServletFilter filter : filtersArray) {
      if (filter == null) {
        continue;
      }
      java.util.regex.Pattern pattern = filter.doGetPattern();
      if (pattern != null && pattern.matcher(path).matches()) {
        godChain.addFilter(filter);
      }
    }

    // Delegate through GodFilterChain (it will fall back to the original chain if no filter matched)
    godChain.doFilter(hsr, response);
  }
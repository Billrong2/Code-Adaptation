@Override
protected void doFilterInternal(javax.servlet.http.HttpServletRequest request,
                                javax.servlet.http.HttpServletResponse response,
                                javax.servlet.FilterChain filterChain)
        throws javax.servlet.ServletException, java.io.IOException {

    if (!response.isCommitted()) {
        // Ensure the header is replaced rather than appended
        response.setHeader("Access-Control-Allow-Origin", "*");
    }

    if (request.getHeader("Access-Control-Request-Method") != null
            && "OPTIONS".equals(request.getMethod())) {
        // CORS "pre-flight" request
        if (!response.isCommitted()) {
            response.setHeader("Access-Control-Allow-Methods",
                    "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers",
                    "X-Requested-With,Origin,Content-Type, Accept, Authorization");
        }
    }

    filterChain.doFilter(request, response);
}
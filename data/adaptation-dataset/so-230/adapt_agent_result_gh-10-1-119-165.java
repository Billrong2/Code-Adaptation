/**
   * For the given {@code url}, extract a mapping of query parameter names to values.
   * <p>
   * The query portion is defined as the substring after the first {@code '?'} character. Parameters are split on
   * {@code '&'} and key/value pairs are split on {@code '='}. Both keys and values are URL-decoded using
   * {@value #ENCODING_CHARSET}. If a parameter appears multiple times, all values are aggregated (in encounter order)
   * under the same key. If a parameter has no explicit value (e.g. {@code flag}), its value defaults to an empty
   * string.
   * <p>
   * Edge cases:
   * <ul>
   *   <li>If {@code url} is {@code null}, an empty {@code Map} is returned.</li>
   *   <li>If no query portion exists, an empty {@code Map} is returned.</li>
   *   <li>Malformed or empty parameter segments are safely ignored.</li>
   * </ul>
   *
   * @param url
   *          The full URL from which query parameters are extracted.
   * @return A mapping of query parameter names to their values; never {@code null}.
   * @throws IllegalStateException
   *           If unable to URL-decode because the JVM doesn't support {@value #ENCODING_CHARSET}.
   */
  public static Map<String, List<String>> extractParametersFromUrl(String url) {
    if (url == null) {
      return emptyMap();
    }

    final int questionMarkIndex = url.indexOf('?');
    if (questionMarkIndex < 0 || questionMarkIndex == url.length() - 1) {
      return emptyMap();
    }

    final Map<String, List<String>> parameters = new HashMap<String, List<String>>();
    final String query = url.substring(questionMarkIndex + 1);

    for (final String segment : query.split("&")) {
      if (segment == null || segment.length() == 0) {
        continue;
      }

      final String[] pair = segment.split("=", 2);
      if (pair.length == 0 || pair[0].length() == 0) {
        continue;
      }

      final String key = urlDecode(pair[0]);
      final String value = pair.length > 1 ? urlDecode(pair[1]) : "";

      List<String> values = parameters.get(key);
      if (values == null) {
        values = new ArrayList<String>();
        parameters.put(key, values);
      }
      values.add(value);
    }

    return parameters;
  }
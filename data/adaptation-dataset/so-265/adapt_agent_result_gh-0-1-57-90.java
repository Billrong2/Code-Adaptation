public static URI relativize(URI base, URI child) {
  // Treat base strictly as a directory; compute a relative URI from base to child
  if (base == null || child == null) {
    return null;
  }

  // Normalize to remove . and .. segments (behavior unchanged)
  URI normalizedBase = base.normalize();
  URI normalizedChild = child.normalize();

  String basePath = normalizedBase.getPath();
  String childPath = normalizedChild.getPath();

  if (basePath == null || childPath == null) {
    return null;
  }

  // Handle empty paths safely
  if (basePath.isEmpty() && childPath.isEmpty()) {
    return URI.create(".");
  }

  final String SEPARATOR = "/";
  final String PARENT = "../";

  // Split into segments; guard against leading empty segment from absolute paths
  String[] baseSegments = basePath.split(SEPARATOR);
  String[] childSegments = childPath.split(SEPARATOR);

  int baseStart = (baseSegments.length > 0 && baseSegments[0].isEmpty()) ? 1 : 0;
  int childStart = (childSegments.length > 0 && childSegments[0].isEmpty()) ? 1 : 0;

  int baseLen = Math.max(0, baseSegments.length - baseStart);
  int childLen = Math.max(0, childSegments.length - childStart);

  // Remove common prefix segments (unchanged behavior)
  int i = 0;
  while (i < baseLen && i < childLen
      && baseSegments[baseStart + i].equals(childSegments[childStart + i])) {
    i++;
  }

  // Construct the relative path
  StringBuilder relative = new StringBuilder();

  // Navigate up from base directory for remaining base segments
  for (int j = i; j < baseLen; j++) {
    relative.append(PARENT);
  }

  // Append remaining child segments
  for (int j = i; j < childLen; j++) {
    if (relative.length() > 0 && relative.charAt(relative.length() - 1) != '/') {
      relative.append(SEPARATOR);
    }
    relative.append(childSegments[childStart + j]);
  }

  String relativePath = relative.toString();
  if (relativePath.isEmpty()) {
    relativePath = ".";
  }

  // Percent-encode before creating the URI
  String encoded = URIUtilities.uriEncode(relativePath);

  try {
    return URI.create(encoded);
  } catch (IllegalArgumentException e) {
    // Malformed relative path; propagate safest fallback
    return null;
  }
}
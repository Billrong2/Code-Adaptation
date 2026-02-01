private static List<Class<?>> getClassesForPackage(final String packageName) throws ClassNotFoundException {
    final String classSuffix = ".class";
    final String charsetUtf8 = "UTF-8";

    if (packageName == null || packageName.isEmpty()) {
      throw new ClassNotFoundException("Package name must not be null or empty");
    }

    final List<java.io.File> directories = new java.util.ArrayList<java.io.File>();
    final ClassLoader cld = Thread.currentThread().getContextClassLoader();
    if (cld == null) {
      throw new ClassNotFoundException("Can't get context class loader.");
    }

    final String path = packageName.replace('.', '/');
    try {
      final java.util.Enumeration<java.net.URL> resources = cld.getResources(path);
      if (resources == null) {
        throw new ClassNotFoundException(packageName + " does not appear to be a valid package (no resources)");
      }
      while (resources.hasMoreElements()) {
        final java.net.URL resource = resources.nextElement();
        if (resource == null) {
          continue;
        }
        // Only file-based resources are handled here; jar-based resources are intentionally skipped
        if ("file".equalsIgnoreCase(resource.getProtocol())) {
          directories.add(new java.io.File(java.net.URLDecoder.decode(resource.getPath(), charsetUtf8)));
        }
      }
    } catch (java.io.UnsupportedEncodingException e) {
      throw new ClassNotFoundException(packageName + " does not appear to be a valid package (unsupported encoding)", e);
    } catch (java.io.IOException e) {
      throw new ClassNotFoundException("IOException while getting resources for " + packageName, e);
    }

    final List<Class<?>> classes = new java.util.ArrayList<Class<?>>();
    for (final java.io.File directory : directories) {
      if (directory == null || !directory.exists()) {
        throw new ClassNotFoundException(packageName + " (" + (directory != null ? directory.getPath() : "null") + ") does not appear to be a valid package");
      }
      final String[] files = directory.list();
      if (files == null) {
        continue;
      }
      for (final String file : files) {
        if (file != null && file.endsWith(classSuffix)) {
          final String className = packageName + '.' + file.substring(0, file.length() - classSuffix.length());
          try {
            classes.add(Class.forName(className));
          } catch (NoClassDefFoundError e) {
            // Intentionally ignored: class is not loadable in this environment
          }
        }
      }
    }
    return classes;
  }
public static List<Class<?>> searchAnnotatedClasses(String basePackage, Class<? extends java.lang.annotation.Annotation> annotation)
      throws IOException, ClassNotFoundException {
    if (basePackage == null || basePackage.isEmpty()) {
      return new ArrayList<>();
    }
    if (annotation == null) {
      return new ArrayList<>();
    }

    final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    final MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

    final List<Class<?>> candidates = new ArrayList<>();
    final String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
        + resolveBasePackage(basePackage) + "/" + "**/*.class";

    final Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
    for (Resource resource : resources) {
      if (!resource.isReadable()) {
        continue;
      }
      final MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
      if (isCandidate(metadataReader, annotation)) {
        final String className = metadataReader.getClassMetadata().getClassName();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
          cl = ClassScanner.class.getClassLoader();
        }
        candidates.add(Class.forName(className, false, cl));
      }
    }
    return candidates;
  }
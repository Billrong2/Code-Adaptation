@Override
protected java.util.List<org.junit.runners.model.FrameworkMethod> computeTestMethods() {
    final java.util.List<org.junit.runners.model.FrameworkMethod> list = super.computeTestMethods();
    java.util.Collections.sort(list, new java.util.Comparator<org.junit.runners.model.FrameworkMethod>() {
        @Override
        public int compare(org.junit.runners.model.FrameworkMethod f1, org.junit.runners.model.FrameworkMethod f2) {
            final Order o1 = f1.getAnnotation(Order.class);
            final Order o2 = f2.getAnnotation(Order.class);

            if (o1 == null || o2 == null) {
                return -1;
            }

            return Integer.compare(o1.order(), o2.order());
        }
    });
    return list;
}
@SuppressWarnings("unchecked")
public static <T> T[] concat(T[] a, T[] b) {
    if (a == null || b == null) {
        throw new NullPointerException("Input arrays must not be null");
    }
    final int alen = a.length;
    final int blen = b.length;
    final Class<?> componentType = a.getClass().getComponentType();
    final T[] result = (T[]) java.lang.reflect.Array
            .newInstance(componentType, alen + blen);
    if (alen > 0) {
        System.arraycopy(a, 0, result, 0, alen);
    }
    if (blen > 0) {
        System.arraycopy(b, 0, result, alen, blen);
    }
    return result;
}
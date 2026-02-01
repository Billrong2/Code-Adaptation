/**
     * Compares two maps for equality using explicit, step-by-step checks.
     * <p>
     * The comparison follows these rules in order:
     * <ol>
     *   <li>If both references point to the same map instance, the maps are equal.</li>
     *   <li>If either map is {@code null}, the maps are not equal.</li>
     *   <li>If the maps have different sizes, the maps are not equal.</li>
     *   <li>For each key in {@code mapA}, the corresponding value in {@code mapB} is compared:</li>
     *   <ul>
     *     <li>If both values are {@code null}, they are considered equal.</li>
     *     <li>If exactly one value is {@code null}, the maps are not equal.</li>
     *     <li>Otherwise, {@link Object#equals(Object)} is used to compare the values.</li>
     *   </ul>
     * </ol>
     * <p>
     * If any key from {@code mapA} is missing in {@code mapB}, the corresponding value
     * lookup will result in inequality according to the rules above.
     *
     * @param mapA the first map to compare, may be {@code null}
     * @param mapB the second map to compare, may be {@code null}
     * @param <K> the key type
     * @param <V> the value type
     * @return {@code true} if both maps are considered equal according to the rules above;
     *         {@code false} otherwise
     * @see <a href="https://stackoverflow.com/">Stack Overflow discussion</a>
     */
    public static <K, V> boolean compare(Map<K, V> mapA, Map<K, V> mapB) {
        if (mapA == mapB) {
            return true;
        }
        if (mapA == null || mapB == null || mapA.size() != mapB.size()) {
            return false;
        }
        for (K key : mapA.keySet()) {
            V valueA = mapA.get(key);
            V valueB = mapB.get(key);
            if (valueA == null && valueB == null) {
                continue;
            } else if (valueA == null || valueB == null) {
                return false;
            }
            if (!valueA.equals(valueB)) {
                return false;
            }
        }
        return true;
    }
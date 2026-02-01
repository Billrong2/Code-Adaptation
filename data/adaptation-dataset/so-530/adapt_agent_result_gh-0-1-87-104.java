/**
 * Creates a shape-preserving shallow copy of a 3D Block array.
 * <p>
 * The array structure is duplicated, but the contained {@link Block} instances
 * are <b>not</b> cloned; only their references are copied.
 * </p>
 * @param array the source 3D Block array
 * @return a new 3D array with the same dimensions containing the same Block references,
 *         or {@code null} if the input array is {@code null}
 */
private Block[][][] copyOf3Dim(final Block[][][] array) {
    if (array == null) {
        return null;
    }

    final Block[][][] result = new Block[array.length][][];
    for (int i = 0; i < array.length; i++) {
        if (array[i] == null) {
            result[i] = null;
            continue;
        }
        result[i] = new Block[array[i].length][];
        for (int j = 0; j < array[i].length; j++) {
            if (array[i][j] == null) {
                result[i][j] = null;
                continue;
            }
            result[i][j] = new Block[array[i][j].length];
            System.arraycopy(array[i][j], 0, result[i][j], 0, array[i][j].length);
        }
    }
    return result;
}
private static int partition(float[] a, int[] index, int left, int right) {
    // Precondition: left < right
    int i = left - 1;
    int j = right;
    // Pivot is the value referenced by the index at 'right'
    float pivot = a[index[right]];

    while (true) {
        // find item on left to swap; a[index[right]] acts as sentinel
        while (a[index[++i]] < pivot) {
            // empty body by design
        }

        // find item on right to swap
        while (pivot < a[index[--j]]) {
            if (j == left) {
                break; // don't go out-of-bounds
            }
        }

        if (i >= j) {
            break; // check if pointers cross
        }

        // swap positions in the index array only
        swap(a, index, i, j);
    }

    // swap with partition element (pivot position)
    swap(a, index, i, right);
    return i;
}
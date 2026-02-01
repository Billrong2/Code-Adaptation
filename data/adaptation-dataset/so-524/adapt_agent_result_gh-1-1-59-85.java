private static int partition(float[] a, int[] index, int left, int right) {
    // Preconditions: a and index are non-null, same length, and left < right
    final float pivot = a[index[right]]; // use rightmost element as pivot/sentinel
    int i = left - 1;
    int j = right;

    while (true) {
      // scan from the left until an element >= pivot is found
      while (a[index[++i]] < pivot) {
        // no-op
      }
      // scan from the right until an element <= pivot is found;
      // explicit boundary check prevents j from moving past left
      while (pivot < a[index[--j]]) {
        if (j <= left) {
          break;
        }
      }
      if (i >= j) {
        break;
      }
      swap(a, index, i, j); // swap index positions only
    }
    // place pivot into its final position
    swap(a, index, i, right);
    return i;
  }
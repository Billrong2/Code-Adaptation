public static void main(String[] params) {
    final String[] safeParams = (params == null) ? new String[0] : params;

    final java.util.Set<String> baseSet = new java.util.HashSet<String>(
            safeParams.length == 0
                    ? java.util.Arrays.asList("Hello", "World", "this", "is", "a", "Test")
                    : java.util.Arrays.asList(safeParams)
    );


    System.out.println("baseSet: " + baseSet);

    final int baseSize = baseSet.size();
    for (int i = 0; i <= baseSize + 1; i++) {
        final java.util.Set<java.util.Set<String>> pSet = new de.fencing_game.paul.examples.FiniteSubSets<String>(baseSet, i);
        System.out.println("------");
        System.out.println("subsets of size " + i + ":");
        int count = 0;
        for (java.util.Set<String> subset : pSet) {
            System.out.println("    " + subset);
            count++;
        }
        System.out.println("in total: " + count + ", " + pSet.size());
    }
}
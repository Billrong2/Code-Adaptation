@Test
public void testSortByValue() {
    // deterministic random for reproducible test
    Random random = new Random(12345L);

    Map<String, Integer> original = new HashMap<String, Integer>(1000);
    for (int i = 0; i < 1000; ++i) {
        // random.nextInt() never returns null
        original.put("SomeString" + i, random.nextInt());
    }

    Map<String, Integer> sorted = MapUtils.sortByValue(original);

    assertNotNull(sorted);
    assertEquals(original.size(), sorted.size());

    Integer previous = null;
    for (Map.Entry<String, Integer> entry : sorted.entrySet()) {
        assertNotNull(entry.getValue());
        if (previous != null) {
            // ascending (non-decreasing) order
            assertTrue(entry.getValue() >= previous);
        }
        previous = entry.getValue();
    }
}
private static Map<String, Integer> initDimensionConstantLookup() {
    final Map<String, Integer> map = new HashMap<>(7);
    map.put("px", TypedValue.COMPLEX_UNIT_PX);
    map.put("dip", TypedValue.COMPLEX_UNIT_DIP);
    map.put("dp", TypedValue.COMPLEX_UNIT_DIP);
    map.put("sp", TypedValue.COMPLEX_UNIT_SP);
    map.put("pt", TypedValue.COMPLEX_UNIT_PT);
    map.put("in", TypedValue.COMPLEX_UNIT_IN);
    map.put("mm", TypedValue.COMPLEX_UNIT_MM);
    return Collections.unmodifiableMap(map);
}
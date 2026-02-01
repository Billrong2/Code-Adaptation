private void processAttributes(XMLReader reader) {
        // Reflection-based extraction relies on internal XMLReader implementation details
        attributes.clear();
        if (reader == null) {
            return;
        }
        try {
            final java.lang.reflect.Field elementField = reader.getClass().getDeclaredField("theNewElement");
            elementField.setAccessible(true);
            final Object element = elementField.get(reader);
            if (element == null) {
                return;
            }

            final java.lang.reflect.Field attsField = element.getClass().getDeclaredField("theAtts");
            attsField.setAccessible(true);
            final Object atts = attsField.get(element);
            if (atts == null) {
                return;
            }

            final java.lang.reflect.Field dataField = atts.getClass().getDeclaredField("data");
            dataField.setAccessible(true);
            final Object dataObj = dataField.get(atts);
            if (!(dataObj instanceof String[])) {
                return;
            }
            final String[] data = (String[]) dataObj;

            final java.lang.reflect.Field lengthField = atts.getClass().getDeclaredField("length");
            lengthField.setAccessible(true);
            final Object lenObj = lengthField.get(atts);
            if (!(lenObj instanceof Integer)) {
                return;
            }
            final int attributesLength = (Integer) lenObj;

            for (int i = 0; i < attributesLength; i++) {
                int nameIndex = i * 5 + 1;
                int valueIndex = i * 5 + 4;
                if (nameIndex >= 0 && valueIndex >= 0 && nameIndex < data.length && valueIndex < data.length) {
                    String name = data[nameIndex];
                    String value = data[valueIndex];
                    if (name != null) {
                        attributes.put(name, value);
                    }
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException | SecurityException e) {
            if (DEBUG) {
                Log.d(TAG, "Failed to extract attributes via XMLReader reflection", e);
            }
        }
    }
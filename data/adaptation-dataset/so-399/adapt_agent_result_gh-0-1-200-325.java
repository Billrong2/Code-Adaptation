private void decompressXML(final byte[] xml) {
        if (xml == null || xml.length < 24) return;
        final int WORD = 4;
        // number of strings
        if (4 * WORD + WORD > xml.length) return;
        int numStrings = LEW(xml, 4 * WORD);
        if (numStrings < 0) return;

        int sitOff = 0x24;
        if (sitOff < 0 || sitOff + numStrings * WORD > xml.length) return;
        int stOff = sitOff + numStrings * WORD;
        if (stOff < 0 || stOff >= xml.length) return;

        // find start of first start tag
        if (3 * WORD + WORD > xml.length) return;
        int xmlTagOff = LEW(xml, 3 * WORD);
        if (xmlTagOff < 0 || xmlTagOff >= xml.length) return;
        for (int ii = xmlTagOff; ii + WORD <= xml.length; ii += WORD) {
            if (LEW(xml, ii) == startTag) {
                xmlTagOff = ii;
                break;
            }
        }

        int off = xmlTagOff;
        int indent = 0;
        while (off + 6 * WORD <= xml.length) {
            int tag0 = LEW(xml, off);
            int nameSi = LEW(xml, off + 5 * WORD);

            if (tag0 == startTag) {
                // ensure header is readable
                if (off + 9 * WORD > xml.length) break;
                int numAttrs = LEW(xml, off + 7 * WORD);
                off += 9 * WORD;

                String name = (nameSi >= 0 && nameSi < numStrings) ? compXmlString(xml, sitOff, stOff, nameSi) : null;
                java.util.Map<String, String> attributes = new java.util.LinkedHashMap<>();

                if (numAttrs < 0) break;
                for (int ii = 0; ii < numAttrs; ii++) {
                    if (off + 5 * WORD > xml.length) break;
                    int attrNameSi = LEW(xml, off + 1 * WORD);
                    int attrValueSi = LEW(xml, off + 2 * WORD);
                    int attrResId = LEW(xml, off + 4 * WORD);
                    off += 5 * WORD;

                    if (attrNameSi < 0 || attrNameSi >= numStrings) continue;
                    String attrName = compXmlString(xml, sitOff, stOff, attrNameSi);
                    String attrValue = (attrValueSi >= 0 && attrValueSi < numStrings)
                            ? compXmlString(xml, sitOff, stOff, attrValueSi)
                            : "resourceID 0x" + Integer.toHexString(attrResId);
                    if (attrName != null) attributes.put(attrName, attrValue);
                }
                evaluateTag(indent, name, attributes);
                indent++;

            } else if (tag0 == endTag) {
                if (indent > 0) indent--;
                off += 6 * WORD;
                String name = (nameSi >= 0 && nameSi < numStrings) ? compXmlString(xml, sitOff, stOff, nameSi) : null;
                evaluateTag(indent, name, null);

            } else if (tag0 == endDocTag) {
                break;
            } else {
                // unrecognized tag: stop silently
                break;
            }
        }
    }
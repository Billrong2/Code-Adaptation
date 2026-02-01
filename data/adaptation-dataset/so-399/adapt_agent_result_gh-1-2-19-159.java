private static String decompressXML(final byte[] xml) {
        // Guard against null or too-short input
        if (xml == null || xml.length < 4 * 9) {
            return "";
        }

        final StringBuilder xmlBuilder = new StringBuilder();

        // Number of strings in string table (word 4)
        if (xml.length < 4 * 5) {
            return "";
        }
        int numbStrings = LEW(xml, 4 * 4);

        // Offsets
        int sitOff = 0x24; // StringIndexTable offset
        int stOff = sitOff + numbStrings * 4; // StringTable offset

        // Find start of XML tag tree
        if (xml.length < 4 * 4) {
            return "";
        }
        int xmlTagOff = LEW(xml, 3 * 4);
        for (int ii = xmlTagOff; ii <= xml.length - 4; ii += 4) {
            if (ii >= 0 && ii + 4 <= xml.length && LEW(xml, ii) == startTag) {
                xmlTagOff = ii;
                break;
            }
        }

        int off = xmlTagOff;
        int indent = 0;

        while (off >= 0 && off + 4 <= xml.length) {
            int tag0 = LEW(xml, off);

            if (tag0 == startTag) { // XML START TAG
                // Ensure we can read the fixed part of a start tag
                if (off + 9 * 4 > xml.length) {
                    break;
                }
                int lineNo = LEW(xml, off + 2 * 4); // preserved for debug parity
                int nameSi = LEW(xml, off + 5 * 4);
                int numbAttrs = LEW(xml, off + 7 * 4);
                off += 9 * 4;

                String name = compXmlString(xml, sitOff, stOff, nameSi);
                if (name == null) {
                    name = "";
                }

                StringBuilder attrBuilder = new StringBuilder();
                for (int ii = 0; ii < numbAttrs; ii++) {
                    if (off + 5 * 4 > xml.length) {
                        break;
                    }
                    int attrNameSi = LEW(xml, off + 1 * 4);
                    int attrValueSi = LEW(xml, off + 2 * 4);
                    int attrResId = LEW(xml, off + 4 * 4);
                    off += 5 * 4;

                    String attrName = compXmlString(xml, sitOff, stOff, attrNameSi);
                    if (attrName == null) {
                        continue;
                    }
                    String attrValue = attrValueSi != -1
                            ? compXmlString(xml, sitOff, stOff, attrValueSi)
                            : "resourceID 0x" + Integer.toHexString(attrResId);
                    if (attrValue == null) {
                        attrValue = "";
                    }
                    attrBuilder.append(" ").append(attrName).append("=\"").append(attrValue).append("\"");
                }

                String startTagStr = "<" + name + attrBuilder + ">";
                prtIndent(indent, startTagStr);
                xmlBuilder.append(startTagStr);
                indent++;

            } else if (tag0 == endTag) { // XML END TAG
                if (off + 6 * 4 > xml.length) {
                    break;
                }
                indent--;
                int nameSi = LEW(xml, off + 5 * 4);
                off += 6 * 4;

                String name = compXmlString(xml, sitOff, stOff, nameSi);
                if (name == null) {
                    name = "";
                }
                String endTagStr = "</" + name + ">";
                prtIndent(indent, endTagStr);
                xmlBuilder.append(endTagStr);

            } else if (tag0 == endDocTag) { // END OF XML DOC
                break;
            } else {
                prt("  Unrecognized tag code '" + Integer.toHexString(tag0) + "' at offset " + off);
                break;
            }
        }

        // Do not print final "end at offset" message; return accumulated XML
        return xmlBuilder.toString();
    }
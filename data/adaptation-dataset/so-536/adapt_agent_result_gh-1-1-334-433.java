public static String decompressXML(byte[] xml) {
	StringBuilder resultXml = new StringBuilder();
	if (xml == null || xml.length < 8) {
		return resultXml.toString();
	}

	int safeLen = xml.length;
	int numbStrings;
	if (4 * 4 + 3 >= safeLen) {
		return resultXml.toString();
	}
	numbStrings = LEW(xml, 4 * 4);

	int sitOff = 0x24;
	int stOff = sitOff + numbStrings * 4;
	if (stOff < 0 || stOff >= safeLen) {
		return resultXml.toString();
	}

	int xmlTagOff;
	if (3 * 4 + 3 >= safeLen) {
		return resultXml.toString();
	}
	xmlTagOff = LEW(xml, 3 * 4);

	for (int ii = xmlTagOff; ii <= safeLen - 4; ii += 4) {
		if (ii + 3 < safeLen && LEW(xml, ii) == startTag) {
			xmlTagOff = ii;
			break;
		}
	}

	int off = xmlTagOff;
	int indent = 0;
	while (off + 3 < safeLen) {
		int tag0 = LEW(xml, off);
		if (tag0 == startTag) {
			if (off + 9 * 4 > safeLen) {
				break;
			}
			int nameSi = LEW(xml, off + 5 * 4);
			int numbAttrs = LEW(xml, off + 7 * 4);
			off += 9 * 4;
			String name = compXmlString(xml, sitOff, stOff, nameSi);
			StringBuilder attrs = new StringBuilder();
			for (int ii = 0; ii < numbAttrs; ii++) {
				if (off + 5 * 4 > safeLen) {
					break;
				}
				int attrNameSi = LEW(xml, off + 1 * 4);
				int attrValueSi = LEW(xml, off + 2 * 4);
				int attrResId = LEW(xml, off + 4 * 4);
				off += 5 * 4;
				String attrName = compXmlString(xml, sitOff, stOff, attrNameSi);
				String attrValue = (attrValueSi != -1)
						? compXmlString(xml, sitOff, stOff, attrValueSi)
						: (M.e("resourceID 0x") + Integer.toHexString(attrResId));
				attrs.append(" ").append(attrName).append("=\"").append(attrValue).append("\"");
			}
			resultXml.append(prtIndent(indent, "<" + name + attrs + ">"));
			indent++;
		} else if (tag0 == endTag) {
			if (off + 6 * 4 > safeLen) {
				break;
			}
			indent--;
			int nameSi = LEW(xml, off + 5 * 4);
			String name = compXmlString(xml, sitOff, stOff, nameSi);
			off += 6 * 4;
			resultXml.append(prtIndent(indent, "</" + name + ">"));
			resultXml.append("\n");
		} else if (tag0 == endDocTag) {
			break;
		} else {
			if (Cfg.DEBUG) {
				Check.log(TAG + " (decompressXML): unrecognized tag '" + Integer.toHexString(tag0) + "' at offset " + off);
			}
			break;
		}
	}
	if (Cfg.DEBUG) {
		Check.log(TAG + " (decompressXML): end at offset " + off);
	}
	return resultXml.toString();
}
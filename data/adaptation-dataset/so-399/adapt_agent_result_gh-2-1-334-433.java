public static String decompressXML(byte[] xml) {
	final int START_TAG = startTag;
	final int END_TAG = endTag;
	final int END_DOC_TAG = endDocTag;
	final int WORD_SIZE = 4;
	final StringBuilder out = new StringBuilder();
	if (xml == null || xml.length < 4 * 9) {
		return "";
	}
	try {
		// number of strings
		if (4 * 4 + 3 >= xml.length) {
			return "";
		}
		int numbStrings = LEW(xml, 4 * 4);
		if (numbStrings < 0) {
			return "";
		}

		int sitOff = 0x24;
		int stOff = sitOff + numbStrings * WORD_SIZE;
		if (stOff < 0 || stOff >= xml.length) {
			return "";
		}

		// initial XML tag offset
		if (3 * 4 + 3 >= xml.length) {
			return "";
		}
		int xmlTagOff = LEW(xml, 3 * 4);
		if (xmlTagOff < 0 || xmlTagOff >= xml.length) {
			xmlTagOff = 0;
		}
		// scan forward for first start tag
		for (int i = xmlTagOff; i + 3 < xml.length; i += WORD_SIZE) {
			if (LEW(xml, i) == START_TAG) {
				xmlTagOff = i;
				break;
			}
		}

		int offset = xmlTagOff;
		int indent = 0;
		while (offset + 3 < xml.length) {
			int tag0 = LEW(xml, offset);

			if (tag0 == START_TAG) {
				// bounds for fixed part of start tag
				if (offset + 8 * WORD_SIZE + 3 >= xml.length) {
					break;
				}
				int nameSi = LEW(xml, offset + 5 * WORD_SIZE);
				int numbAttrs = LEW(xml, offset + 7 * WORD_SIZE);
				if (numbAttrs < 0) {
					break;
				}
				offset += 9 * WORD_SIZE;

				String name = compXmlString(xml, sitOff, stOff, nameSi);
				if (name == null) {
					name = "";
				}
				StringBuilder attrs = new StringBuilder();
				for (int i = 0; i < numbAttrs; i++) {
					if (offset + 5 * WORD_SIZE - 1 >= xml.length) {
						break;
					}
					int attrNameSi = LEW(xml, offset + 1 * WORD_SIZE);
					int attrValueSi = LEW(xml, offset + 2 * WORD_SIZE);
					int attrResId = LEW(xml, offset + 4 * WORD_SIZE);
					offset += 5 * WORD_SIZE;

					String attrName = compXmlString(xml, sitOff, stOff, attrNameSi);
					if (attrName == null) {
						attrName = "";
					}
					String attrValue;
					if (attrValueSi != -1) {
						attrValue = compXmlString(xml, sitOff, stOff, attrValueSi);
						if (attrValue == null) {
							attrValue = "";
						}
					} else {
						attrValue = M.e("resourceID 0x") + Integer.toHexString(attrResId);
					}
					attrs.append(" ").append(attrName).append("=\"").append(attrValue).append("\"");
				}
				out.append(prtIndent(indent, "<" + name + attrs + ">"));
				indent++;

			} else if (tag0 == END_TAG) {
				// bounds for end tag
				if (offset + 5 * WORD_SIZE + 3 >= xml.length) {
					break;
				}
				indent = Math.max(0, indent - 1);
				int nameSi = LEW(xml, offset + 5 * WORD_SIZE);
				offset += 6 * WORD_SIZE;
				String name = compXmlString(xml, sitOff, stOff, nameSi);
				if (name == null) {
					name = "";
				}
				out.append(prtIndent(indent, "</" + name + ">"));
				out.append("\n");

			} else if (tag0 == END_DOC_TAG) {
				break;

			} else {
				if (Cfg.DEBUG) {
					Check.log(TAG + " (decompressXML): unrecognized tag '" + Integer.toHexString(tag0) + "' at offset " + offset);
				}
				break;
			}
		}
		if (Cfg.DEBUG) {
			Check.log(TAG + " (decompressXML): end at offset " + offset);
		}
		return out.toString();
	} catch (Throwable t) {
		if (Cfg.DEBUG) {
			Check.log(TAG + " (decompressXML): exception: " + t);
		}
		return out.toString();
	}
}
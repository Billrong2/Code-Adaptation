public String format(String s) {
	if (s == null || s.length() == 0) return "";
	int indent = 0;
	StringBuilder sb = new StringBuilder();
	this.singleLine = false;
	for (int i = 0; i < s.length(); i++) {
		char currentChar = s.charAt(i);
		if (currentChar == '<') {
			char nextChar = (i + 1 < s.length()) ? s.charAt(i + 1) : '\0';
			if (nextChar == '/') indent -= this.indentNumChars;
			if (!this.singleLine) sb.append(this.createIndentation(Math.max(indent, 0)));
			if (nextChar != '?' && nextChar != '!' && nextChar != '/') indent += this.indentNumChars;
			this.singleLine = false;
		}
		sb.append(currentChar);
		if (currentChar == '>') {
			char prevChar = (i - 1 >= 0) ? s.charAt(i - 1) : '\0';
			if (prevChar == '/') {
				indent -= this.indentNumChars;
				sb.append(NEW_LINE);
			} else {
				int nextStartElementPos = s.indexOf('<', i + 1);
				if (nextStartElementPos > i + 1) {
					String textBetweenElements = s.substring(i + 1, nextStartElementPos);
					String whitespaceCheck = textBetweenElements.replace("\n", "").replace("\r", "");
					if (whitespaceCheck.length() == 0) {
						sb.append(textBetweenElements).append(NEW_LINE);
					} else {
						sb.append(textBetweenElements);
						this.singleLine = true;
					}
					i = nextStartElementPos - 1;
				} else {
					sb.append(NEW_LINE);
				}
			}
		}
	}
	return sb.toString();
}
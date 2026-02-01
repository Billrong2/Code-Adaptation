String step1( String str ) {
	// Code hardening: null/empty checks
	if ( str == null || str.length() == 0 )
		return str;

	NewString stem = new NewString();

	// ----- Step 1a: Plurals -----
	if ( str.length() > 3 && hasSuffix( str, "sses", stem ) ) {
		str = stem.str + "ss";
	}
	else if ( str.length() > 3 && hasSuffix( str, "ies", stem ) ) {
		str = stem.str + "i";
	}
	else if ( str.length() > 1 && str.endsWith("s") && !str.endsWith("ss") ) {
		// remove terminal 's' when appropriate (avoid single-char 's')
		str = str.substring( 0, str.length() - 1 );
	}

	// ----- Step 1b: eed -----
	if ( str.length() > 3 && hasSuffix( str, "eed", stem ) ) {
		if ( measure( stem.str ) > 0 ) {
			str = stem.str + "ee";
		}
	}
	else {
		// ----- Step 1b: ed / ing -----
		boolean flag = false;
		if ( str.length() > 2 && hasSuffix( str, "ed", stem ) ) {
			if ( containsVowel( stem.str ) ) {
				str = stem.str;
				flag = true;
			}
		}
		else if ( str.length() > 3 && hasSuffix( str, "ing", stem ) ) {
			if ( containsVowel( stem.str ) ) {
				str = stem.str;
				flag = true;
			}
		}

		// ----- Step 1b follow-up adjustments -----
		if ( flag ) {
			if ( str.endsWith("at") || str.endsWith("bl") || str.endsWith("iz") ) {
				str = str + "e";
			}
			else if ( str.length() >= 2 ) {
				char last = str.charAt( str.length() - 1 );
				char prev = str.charAt( str.length() - 2 );
				if ( last == prev && last != 'l' && last != 's' && last != 'z' ) {
					str = str.substring( 0, str.length() - 1 );
				}
			}

			if ( measure( str ) == 1 && cvc( str ) ) {
				str = str + "e";
			}
		}
	}

	// ----- Step 1c: terminal y -----
	if ( str.length() > 1 && str.endsWith("y") ) {
		String base = str.substring( 0, str.length() - 1 );
		if ( containsVowel( base ) ) {
			str = base + "i";
		}
	}

	return str;
}
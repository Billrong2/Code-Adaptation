String step3( String str ) {

		java.util.Map<String, String> suffixMap = new java.util.LinkedHashMap<String, String>();
		// ordered rules; empty string means removal
		suffixMap.put("al", "");
		suffixMap.put("ance", "");
		suffixMap.put("ence", "");
		suffixMap.put("er", "");
		suffixMap.put("ic", "");
		suffixMap.put("able", "");
		suffixMap.put("ible", "");
		suffixMap.put("ant", "");
		suffixMap.put("ement", "");
		suffixMap.put("ment", "");
		suffixMap.put("ent", "");
		suffixMap.put("sion", "");
		suffixMap.put("tion", "");
		suffixMap.put("ou", "");
		suffixMap.put("ism", "");
		suffixMap.put("ate", "");
		suffixMap.put("iti", "");
		suffixMap.put("ous", "");
		suffixMap.put("ive", "");
		suffixMap.put("ize", "");
		suffixMap.put("ise", "");

		NewString stem = new NewString();

		for ( java.util.Map.Entry<String, String> entry : suffixMap.entrySet() ) {
			String suffix = entry.getKey();
			String replacement = entry.getValue();
			if ( hasSuffix( str, suffix, stem ) ) {
				if ( measure( stem.str ) > 0 ) {
					str = stem.str + replacement;
					return str;
				}
			}
		}

		return str;
	}
boolean cvc(final String str) {
    // Checks whether the string ends with a consonant-vowel-consonant pattern
    if (str == null) {
        return false;
    }

    final int length = str.length();
    if (length < 3) {
        return false;
    }

    final char lastChar = str.charAt(length - 1);
    // Last character must be a consonant and not w, x, or y
    if (vowel(lastChar, (length > 1 ? str.charAt(length - 2) : '?')) || lastChar == 'w' || lastChar == 'x' || lastChar == 'y') {
        return false;
    }

    final char middleChar = str.charAt(length - 2);
    final char middlePrev = (length > 2) ? str.charAt(length - 3) : '?';
    // Second-to-last character must be a vowel (context-aware for 'y')
    if (!vowel(middleChar, middlePrev)) {
        return false;
    }

    final char firstChar = str.charAt(length - 3);
    // Third-to-last character must be a consonant; special-case length == 3
    if (length == 3) {
        if (vowel(firstChar, '?')) {
            return false;
        }
    } else {
        final char firstPrev = str.charAt(length - 4);
        if (vowel(firstChar, firstPrev)) {
            return false;
        }
    }

    return true;
  }
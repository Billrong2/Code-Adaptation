boolean hasSuffix(String word, String suffix, NewString stem) {
    // reset stem to avoid stale state
    if (stem != null) {
        stem.str = "";
    }

    // null and basic validation checks
    if (word == null || suffix == null || stem == null) {
        return false;
    }

    int wordLen = word.length();
    int suffixLen = suffix.length();

    // suffix must be shorter than word
    if (suffixLen == 0 || wordLen <= suffixLen) {
        return false;
    }

    // early character check: second-to-last character
    if (suffixLen > 1) {
        if (word.charAt(wordLen - 2) != suffix.charAt(suffixLen - 2)) {
            return false;
        }
    }

    // build stem by removing suffix
    String candidateStem = word.substring(0, wordLen - suffixLen);
    stem.str = candidateStem;

    // reconstruct and verify exact suffix match
    String reconstructed = candidateStem + suffix;
    if (reconstructed.equals(word)) {
        return true;
    }

    // no exact suffix match
    stem.str = "";
    return false;
}
@Override
public int compareTo(mavenVersion other) {
    if (other == null) {
        return 1;
    }

    // Use pre-parsed parts arrays for efficiency
    final String[] thisParts = this.parts;
    final String[] otherParts = other.parts;

    if (thisParts == null && otherParts == null) {
        return 0;
    }
    if (thisParts == null) {
        return -1;
    }
    if (otherParts == null) {
        return 1;
    }

    int length = Math.max(thisParts.length, otherParts.length);
    for (int i = 0; i < length; i++) {
        int thisPart = (i < thisParts.length) ? Integer.parseInt(thisParts[i]) : 0;
        int otherPart = (i < otherParts.length) ? Integer.parseInt(otherParts[i]) : 0;

        if (thisPart < otherPart) {
            return -1;
        }
        if (thisPart > otherPart) {
            return 1;
        }
    }
    return 0;
}
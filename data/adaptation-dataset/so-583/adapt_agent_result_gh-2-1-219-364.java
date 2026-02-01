public static String reshape(String input) {
    if (input == null || input.isEmpty()) {
        return "";
    }

    final int CASE_MASK = 0x000F;

    final String padded = " " + input + " ";
    final int length = input.length();
    final StringBuilder result = new StringBuilder(length);

    int index = 0;
    char previousPrevious = ' ';

    while (index < length) {
        final int base = index;

        final char previous = padded.charAt(base);
        final char current = padded.charAt(base + 1);
        final char next = (base + 2 < padded.length()) ? padded.charAt(base + 2) : ' ';

        int currentCase = getCase(current);
        int nextCase = getCase(next);
        int previousCase = getCase(previous);

        int preStep = 0;
        char effectivePrevious = previous;

        if (previousCase == TASHKEEL) {
            effectivePrevious = previousPrevious;
            previousCase = getCase(effectivePrevious);
        }

        if ((previousCase & LEFT_CHAR_MASK) == LEFT_CHAR_MASK) {
            preStep = 1;
        }

        switch (currentCase & CASE_MASK) {
            case NOTUSED_CHAR:
            case NOTARABIC_CHAR:
                result.append(current);
                index++;
                continue;

            case NORIGHT_NOLEFT_CHAR:
            case TATWEEL_CHAR:
                result.append(getShape(current, 0));
                index++;
                continue;

            case RIGHT_LEFT_CHAR_LAM:
                if ((nextCase & CASE_MASK) == RIGHT_NOLEFT_CHAR_ALEF) {
                    result.append(getShape(next, preStep + 2));
                    index += 2;
                    continue;
                } else if ((nextCase & RIGHT_NOLEFT_CHAR_MASK) == RIGHT_NOLEFT_CHAR_MASK) {
                    result.append(getShape(current, 2 + preStep));
                    index++;
                    continue;
                } else if (nextCase == TANWEEN) {
                    result.append(getShape(current, preStep));
                    index++;
                    continue;
                } else if (nextCase == TASHKEEL) {
                    final char nextNext = (base + 3 < padded.length()) ? padded.charAt(base + 3) : ' ';
                    final int nextNextCase = getCase(nextNext);

                    if ((nextNextCase & RIGHT_NOLEFT_CHAR_MASK) == RIGHT_NOLEFT_CHAR_MASK) {
                        result.append(getShape(current, 2 + preStep));
                        index++;
                        continue;
                    } else {
                        result.append(getShape(current, preStep));
                        index++;
                        continue;
                    }
                } else {
                    result.append(getShape(current, preStep));
                    index++;
                    continue;
                }

            case RIGHT_LEFT_CHAR:
                if ((nextCase & RIGHT_NOLEFT_CHAR_MASK) == RIGHT_NOLEFT_CHAR_MASK) {
                    result.append(getShape(current, 2 + preStep));
                    index++;
                    continue;
                } else if (nextCase == TANWEEN) {
                    result.append(getShape(current, preStep));
                    index++;
                    continue;
                } else if (nextCase == TASHKEEL) {
                    final char nextNext = (base + 3 < padded.length()) ? padded.charAt(base + 3) : ' ';
                    final int nextNextCase = getCase(nextNext);

                    if ((nextNextCase & RIGHT_NOLEFT_CHAR_MASK) == RIGHT_NOLEFT_CHAR_MASK) {
                        result.append(getShape(current, 2 + preStep));
                        index++;
                        continue;
                    } else {
                        result.append(getShape(current, preStep));
                        index++;
                        continue;
                    }
                } else {
                    result.append(getShape(current, preStep));
                    index++;
                    continue;
                }

            case RIGHT_NOLEFT_CHAR_ALEF:
            case RIGHT_NOLEFT_CHAR:
                result.append(getShape(current, preStep));
                index++;
                continue;

            case TASHKEEL:
            case TANWEEN:
                result.append(getShape(current, 0));
                index++;
                previousPrevious = effectivePrevious;
                continue;

            default:
                result.append(getShape(current, 0));
                index++;
        }
    }

    return result.toString();
}
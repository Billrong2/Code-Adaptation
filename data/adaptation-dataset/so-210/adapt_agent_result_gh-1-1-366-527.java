public static String reshape_reverse(String str) {
    // Reverse-direction Arabic shaping: neighbors are interpreted as next/current/previous
    if (str == null || str.length() == 0) {
        return str;
    }

    // Padding increased to safely allow i+3 lookups when scanning ahead for diacritics
    String temp = "    " + str + "    ";
    StringBuilder reshaped = new StringBuilder(str.length());

    int len = str.length();
    int i = 0;

    while (i < len) {
        // reversed semantics
        char post = temp.charAt(i + 2);      // previous logical character
        char at = temp.charAt(i + 3);        // current logical character
        char pre = temp.charAt(i + 4);       // next logical character

        int whichCase = getCase(at);
        int postCase = getCase(post);
        int preCase = getCase(pre);

        // compute pre_step based on reversed joining direction
        int pre_step = 0;

        // Skip diacritics when determining the effective preceding joiner (scan ahead)
        if (preCase == TASHKEEL) {
            // look further ahead for the real joining character
            int scanIndex = i + 5;
            char scanChar = ' ';
            int scanCase = 0;
            while (scanIndex < temp.length()) {
                scanChar = temp.charAt(scanIndex);
                scanCase = getCase(scanChar);
                if (scanCase != TASHKEEL) {
                    break;
                }
                scanIndex++;
            }
            pre = scanChar;
            preCase = scanCase;
        }

        if ((preCase & LEFT_CHAR_MASK) == LEFT_CHAR_MASK) {
            pre_step = 1;
        }

        switch (whichCase & 0x000F) {
            case NOTUSED_CHAR:
            case NOTARABIC_CHAR:
                reshaped.append(at);
                i++;
                continue;

            case NORIGHT_NOLEFT_CHAR:
            case TATWEEL_CHAR:
                reshaped.append(getShape(at, 0));
                i++;
                continue;

            case RIGHT_NOLEFT_CHAR_ALEF:
                // Inverted Lam–Alef logic: detect preceding LAM in reversed context
                if ((postCase & 0x000F) == RIGHT_LEFT_CHAR_LAM) {
                    // recompute pre_step after consuming ligature
                    int newPreStep = pre_step;
                    reshaped.append(getShape(at, newPreStep + 2));
                    i += 2; // consume ALEF + LAM
                    continue;
                }
                reshaped.append(getShape(at, pre_step));
                i++;
                continue;

            case RIGHT_LEFT_CHAR:
            case RIGHT_LEFT_CHAR_LAM:
                // General joining logic (LAM merged here; ligature handled above)
                if ((postCase & RIGHT_NOLEFT_CHAR_MASK) == RIGHT_NOLEFT_CHAR_MASK) {
                    reshaped.append(getShape(at, 2 + pre_step));
                    i++;
                    continue;
                } else if (postCase == TANWEEN) {
                    reshaped.append(getShape(at, pre_step));
                    i++;
                    continue;
                } else if (postCase == TASHKEEL) {
                    // look further back (reversed) to decide joining
                    char postPost = (i + 1 >= 0) ? temp.charAt(i + 1) : ' ';
                    int postPostCase = getCase(postPost);
                    if ((postPostCase & RIGHT_NOLEFT_CHAR_MASK) == RIGHT_NOLEFT_CHAR_MASK) {
                        reshaped.append(getShape(at, 2 + pre_step));
                        i++;
                        continue;
                    } else {
                        reshaped.append(getShape(at, pre_step));
                        i++;
                        continue;
                    }
                } else {
                    reshaped.append(getShape(at, pre_step));
                    i++;
                    continue;
                }

            case RIGHT_NOLEFT_CHAR:
                reshaped.append(getShape(at, pre_step));
                i++;
                continue;

            case TASHKEEL:
            case TANWEEN:
                reshaped.append(getShape(at, 0));
                i++;
                continue;

            default:
                reshaped.append(getShape(at, 0));
                i++;
        }
    }

    return reshaped.toString();
}
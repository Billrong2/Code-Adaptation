public static String reshape_reverse(String Str) {
        if (Str == null || Str.length() == 0) {
            return Str;
        }

        // Extra padding on both ends to allow reverse neighbor access (i+3)
        String Temp = "  " + Str + "  ";
        StringBuilder reshapedString = new StringBuilder();
        int len = Str.length();
        int i = 0;

        while (i < len) {
            // Reverse context: preRight = next (right), at = current, postLeft = previous (left)
            char postLeft = Temp.charAt(i + 1);
            char at = Temp.charAt(i + 2);
            char preRight = Temp.charAt(i + 3);

            int which_case = getCase(at);
            int what_case_preRight = getCase(preRight);
            int what_case_postLeft = getCase(postLeft);

            // Connectivity now depends on the right-side neighbor
            int pre_step = 0;
            if (what_case_preRight == TASHKEEL) {
                // Skip forward to next base letter when right neighbor is tashkeel
                if (i + 4 < Temp.length()) {
                    preRight = Temp.charAt(i + 4);
                    what_case_preRight = getCase(preRight);
                }
            }
            if ((what_case_preRight & LEFT_CHAR_MASK) == LEFT_CHAR_MASK) {
                pre_step = 1;
            }

            switch (which_case & 0x000F) {
                case NOTUSED_CHAR:
                case NOTARABIC_CHAR:
                    reshapedString.append(at);
                    i++;
                    continue;

                case NORIGHT_NOLEFT_CHAR:
                case TATWEEL_CHAR:
                    reshapedString.append(getShape(at, 0));
                    i++;
                    continue;

                case RIGHT_LEFT_CHAR:
                case RIGHT_LEFT_CHAR_LAM:
                    // Generic join logic in reverse context
                    if ((what_case_postLeft & RIGHT_NOLEFT_CHAR_MASK) == RIGHT_NOLEFT_CHAR_MASK) {
                        reshapedString.append(getShape(at, 2 + pre_step));
                    } else {
                        reshapedString.append(getShape(at, pre_step));
                    }
                    i++;
                    continue;

                case RIGHT_NOLEFT_CHAR_ALEF:
                    // Lam–Alef ligature detection moved here (reverse context)
                    if ((what_case_postLeft & 0x000F) == RIGHT_LEFT_CHAR_LAM) {
                        reshapedString.append(getShape(at, pre_step + 2));
                        i += 2; // consume Lam + Alef
                        continue;
                    }
                    // fall through to default Alef behavior
                case RIGHT_NOLEFT_CHAR:
                    reshapedString.append(getShape(at, pre_step));
                    i++;
                    continue;

                case TASHKEEL:
                case TANWEEN:
                    reshapedString.append(getShape(at, 0));
                    i++;
                    continue;

                default:
                    reshapedString.append(getShape(at, 0));
                    i++;
            }
        }

        return reshapedString.toString();
    }
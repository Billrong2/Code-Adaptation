public static String reshape_reverse(String Str) {
        if (Str == null || Str.length() == 0) {
            return "";
        }

        // Pad with three trailing spaces to allow i+3 lookups in reverse context
        String Temp = " " + Str + "   ";
        StringBuilder reshapedString = new StringBuilder();
        int len = Str.length();
        int i = 0;

        while (i < len) {
            // Reverse neighbor interpretation:
            // post = Temp[i], at = Temp[i+1], pre = Temp[i+2]
            char post = (i < Temp.length()) ? Temp.charAt(i) : ' ';
            char at = (i + 1 < Temp.length()) ? Temp.charAt(i + 1) : ' ';
            char pre = (i + 2 < Temp.length()) ? Temp.charAt(i + 2) : ' ';

            int which_case = getCase(at);
            int what_case_pre = getCase(pre);
            int what_case_post = getCase(post);

            // Compute pre_step based on reversed pre
            int pre_step = 0;
            if ((what_case_pre & LEFT_CHAR_MASK) == LEFT_CHAR_MASK) {
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
                    // Lam is treated as a general RIGHT_LEFT_CHAR here
                    if ((what_case_post & RIGHT_NOLEFT_CHAR_MASK) == RIGHT_NOLEFT_CHAR_MASK) {
                        reshapedString.append(getShape(at, 2 + pre_step));
                        i++;
                        continue;
                    } else if (what_case_post == TANWEEN) {
                        reshapedString.append(getShape(at, pre_step));
                        i++;
                        continue;
                    } else if (what_case_post == TASHKEEL) {
                        // Look further backward safely
                        char post_post = (i - 1 >= 0) ? Temp.charAt(i - 1) : ' ';
                        int what_case_post_post = getCase(post_post);
                        if ((what_case_post_post & RIGHT_NOLEFT_CHAR_MASK) == RIGHT_NOLEFT_CHAR_MASK) {
                            reshapedString.append(getShape(at, 2 + pre_step));
                            i++;
                            continue;
                        } else {
                            reshapedString.append(getShape(at, pre_step));
                            i++;
                            continue;
                        }
                    } else {
                        reshapedString.append(getShape(at, pre_step));
                        i++;
                        continue;
                    }

                case RIGHT_NOLEFT_CHAR_ALEF:
                    // Lam–Alef ligature detection moved here:
                    // current is Alef, preceding (pre) is Lam
                    if ((what_case_pre & 0x000F) == RIGHT_LEFT_CHAR_LAM) {
                        reshapedString.append(getShape(at, pre_step + 2));
                        i += 2;
                        continue;
                    } else {
                        reshapedString.append(getShape(at, pre_step));
                        i++;
                        continue;
                    }

                case RIGHT_NOLEFT_CHAR:
                    reshapedString.append(getShape(at, pre_step));
                    i++;
                    continue;

                case TASHKEEL:
                    reshapedString.append(getShape(at, 0));
                    i++;
                    continue;

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
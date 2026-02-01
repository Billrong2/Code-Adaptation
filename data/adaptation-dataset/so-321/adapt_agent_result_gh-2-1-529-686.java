public static String reshape_browser(String Str) {
        if (Str == null || Str.length() == 0) {
            return Str;
        }

        String Temp = " " + Str + " ";
        char pre, at, post;
        StringBuilder reshapedString = new StringBuilder(Str.length());
        int i = 0;
        int len = Str.length();

        char post_post;
        char pre_pre = ' ';
        boolean pre_can_connect = false;

        while (i < len) {
            pre = Temp.charAt(i);
            at = Temp.charAt(i + 1);
            post = Temp.charAt(i + 2);

            // Special-case Arabic comma U+060C
            if (at == '\u060C') {
                reshapedString.append(',');
                pre_can_connect = false;
                i++;
                continue;
            }

            int which_case = getCase(at);
            int what_case_post = getCase(post);
            int what_case_pre = getCase(pre);
            int what_case_post_post;

            int pre_step = pre_can_connect ? 1 : 0;

            if (what_case_pre == TASHKEEL) {
                pre = pre_pre;
                what_case_pre = getCase(pre);
            }
            if ((what_case_pre & LEFT_CHAR_MASK) == LEFT_CHAR_MASK) {
                pre_step = 1;
            }

            switch (which_case & 0x000F) {

                case NOTUSED_CHAR:
                case NOTARABIC_CHAR:
                    reshapedString.append(at);
                    pre_can_connect = false;
                    i++;
                    continue;

                case NORIGHT_NOLEFT_CHAR:
                    reshapedString.append(getShape(at, 0));
                    pre_can_connect = false;
                    i++;
                    continue;

                case TATWEEL_CHAR:
                    reshapedString.append(getShape(at, 0));
                    pre_can_connect = false;
                    i++;
                    continue;

                case RIGHT_LEFT_CHAR_LAM:
                    // lam + alef ligature
                    if ((what_case_post & 0x000F) == RIGHT_NOLEFT_CHAR_ALEF) {
                        reshapedString.append(getShape(post, pre_step + 2));
                        pre_can_connect = false;
                        i += 2;
                        continue;
                    } else if (what_case_post == TASHKEEL) {
                        // look ahead safely to next non-diacritic
                        if (i + 3 < Temp.length()) {
                            post_post = Temp.charAt(i + 3);
                            what_case_post_post = getCase(post_post);
                            if ((what_case_post_post & RIGHT_NOLEFT_CHAR_MASK) == RIGHT_NOLEFT_CHAR_MASK) {
                                reshapedString.append(getShape(at, 2 + pre_step));
                                pre_can_connect = true;
                            } else {
                                reshapedString.append(getShape(at, pre_step));
                                pre_can_connect = false;
                            }
                        } else {
                            reshapedString.append(getShape(at, pre_step));
                            pre_can_connect = false;
                        }
                        i++;
                        continue;
                    } else if ((what_case_post & RIGHT_NOLEFT_CHAR_MASK) == RIGHT_NOLEFT_CHAR_MASK) {
                        reshapedString.append(getShape(at, 2 + pre_step));
                        pre_can_connect = true;
                        i++;
                        continue;
                    } else {
                        reshapedString.append(getShape(at, pre_step));
                        pre_can_connect = false;
                        i++;
                        continue;
                    }

                case RIGHT_LEFT_CHAR:
                    if ((what_case_post & RIGHT_NOLEFT_CHAR_MASK) == RIGHT_NOLEFT_CHAR_MASK) {
                        reshapedString.append(getShape(at, 2 + pre_step));
                        pre_can_connect = true;
                        i++;
                        continue;
                    } else if (what_case_post == TASHKEEL) {
                        if (i + 3 < Temp.length()) {
                            post_post = Temp.charAt(i + 3);
                            what_case_post_post = getCase(post_post);
                            if ((what_case_post_post & RIGHT_NOLEFT_CHAR_MASK) == RIGHT_NOLEFT_CHAR_MASK) {
                                reshapedString.append(getShape(at, 2 + pre_step));
                                pre_can_connect = true;
                            } else {
                                reshapedString.append(getShape(at, pre_step));
                                pre_can_connect = false;
                            }
                        } else {
                            reshapedString.append(getShape(at, pre_step));
                            pre_can_connect = false;
                        }
                        i++;
                        continue;
                    } else {
                        reshapedString.append(getShape(at, pre_step));
                        pre_can_connect = false;
                        i++;
                        continue;
                    }

                case RIGHT_NOLEFT_CHAR_ALEF:
                case RIGHT_NOLEFT_CHAR:
                    reshapedString.append(getShape(at, pre_step));
                    pre_can_connect = false;
                    i++;
                    continue;

                case TASHKEEL:
                case TANWEEN:
                    reshapedString.append(getShape(at, 0));
                    pre_pre = pre;
                    // connectivity unchanged
                    i++;
                    continue;

                default:
                    reshapedString.append(getShape(at, 0));
                    pre_can_connect = false;
                    i++;
            }
        }

        return reshapedString.toString();
    }
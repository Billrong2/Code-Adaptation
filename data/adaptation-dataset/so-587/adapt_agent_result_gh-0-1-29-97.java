@Override
    public int compare(final Object o1, final Object o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        if (!(o1 instanceof String) || !(o2 instanceof String)) {
            throw new IllegalArgumentException("VersionComparator expects String arguments");
        }

        final String version1 = (String) o1;
        final String version2 = (String) o2;

        final VersionTokenizer tokenizer1 = new VersionTokenizer(version1);
        final VersionTokenizer tokenizer2 = new VersionTokenizer(version2);

        int number1 = 0, number2 = 0;
        String suffix1 = "", suffix2 = "";

        while (tokenizer1.MoveNext()) {
            if (!tokenizer2.MoveNext()) {
                do {
                    number1 = tokenizer1.getNumber();
                    suffix1 = tokenizer1.getSuffix();
                    if (number1 != 0 || suffix1.length() != 0) {
                        return 1;
                    }
                } while (tokenizer1.MoveNext());
                return 0;
            }

            number1 = tokenizer1.getNumber();
            suffix1 = tokenizer1.getSuffix();
            number2 = tokenizer2.getNumber();
            suffix2 = tokenizer2.getSuffix();

            if (number1 < number2) {
                return -1;
            }
            if (number1 > number2) {
                return 1;
            }

            final boolean empty1 = suffix1.length() == 0;
            final boolean empty2 = suffix2.length() == 0;

            if (empty1 && empty2) {
                continue;
            }
            if (empty1) {
                return 1;
            }
            if (empty2) {
                return -1;
            }

            final int result = suffix1.compareTo(suffix2);
            if (result != 0) {
                return result;
            }
        }
        if (tokenizer2.MoveNext()) {
            do {
                number2 = tokenizer2.getNumber();
                suffix2 = tokenizer2.getSuffix();
                if (number2 != 0 || suffix2.length() != 0) {
                    return -1;
                }
            } while (tokenizer2.MoveNext());
            return 0;
        }
        return 0;
    }
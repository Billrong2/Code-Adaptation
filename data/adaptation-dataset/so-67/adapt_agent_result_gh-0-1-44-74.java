public int read() throws IOException {
    switch (state) {
        case bol: {
            // beginning of line, read until non-indentation character
            while (true) {
                final int ch = in.read();
                if (ch == -1) {
                    state = State.eof;
                    return -1;
                }
                if (ch != (int) ' ' && ch != (int) '\t') {
                    state = State.content;
                    return processChar(ch);
                }
            }
        }
        case content: {
            final int ch = in.read();
            if (ch == -1) {
                state = State.eof;
                return -1;
            }
            return processChar(ch);
        }
        // eol states replace all "\n" by "##\n"
        case eol1: {
            state = State.eol2;
            return (int) '#';
        }
        case eol2: {
            state = State.bol;
            return (int) '\n';
        }
        case eof: {
            return -1;
        }
        default: {
            // unexpected state safeguard
            state = State.eof;
            return -1;
        }
    }
}